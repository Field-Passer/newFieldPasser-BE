package com.example.newfieldpasser.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.newfieldpasser.dto.BoardDTO;
import com.example.newfieldpasser.dto.DistrictDTO;
import com.example.newfieldpasser.dto.MypageDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Category;
import com.example.newfieldpasser.entity.District;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.exception.board.BoardException;
import com.example.newfieldpasser.exception.board.ErrorCode;
import com.example.newfieldpasser.exception.member.MemberException;
import com.example.newfieldpasser.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final DistrictRepository districtRepository;
    private final MemberRepository memberRepository;
    private final WishBoardRepository wishBoardRepository;
    private final AmazonS3 amazonS3;
    private final Response response;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${s3-url}")
    private String s3url;

    /*
    게시글 등록
     */
    @Transactional
    public ResponseEntity<?> registerBoard(MultipartFile file, Authentication authentication, BoardDTO.BoardReqDTO boardReqDTO) {
        try {
            String imageUrl = null;
            if (file != null && !file.isEmpty()) {
                imageUrl = uploadPic(file);
            }
            Member member = memberRepository.findByMemberId(authentication.getName()).get();
            Category category = categoryRepository.findByCategoryName(boardReqDTO.getCategoryName()).get();
            District district = districtRepository.findByDistrictName(boardReqDTO.getDistrictName()).get();

            boardRepository.save(boardReqDTO.toEntity(member, category, district, imageUrl));

            return response.success("Board Registration Success!");

        } catch (IOException e) {
            log.error("Fail Upload file!");
            return response.fail("Fail Upload file!");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Fail Register Board");
            return response.fail("Fail Register Board");
        }
    }

    /*
    파일 업로드 관련 메서드
     */
    public String uploadPic(MultipartFile file) throws IOException {

        UUID uuid = UUID.randomUUID(); // 중복 방지를 위한 랜덤 값
        String originalFilename = file.getOriginalFilename();
        String fullName = uuid.toString() + "_" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucket, fullName, file.getInputStream(), metadata);

        return amazonS3.getUrl(bucket, fullName).toString();
    }

    /*
    게시글 조회 시 조회 수 카운트
     */
    @Transactional
    public void updateViewCount(long boardId) {
        boardRepository.updateViewCount(boardId);
    }

    /*
    게시글 상세조회
     */
    public ResponseEntity<?> boardInquiryDetail(long boardId, Authentication authentication) {
        try {
            BoardDTO.BoardDetailResDTO result = boardRepository.findByBoardId(boardId).map(BoardDTO.BoardDetailResDTO::new).get();

            String loginMemberId = authentication != null ? authentication.getName() : "";

            if (loginMemberId.equals(result.getMemberId())) { //본인 작성 게시글 여부 반환
                result.setMyBoard(true);
            }

            if (wishBoardRepository.existsByMember_MemberIdAndBoard_BoardId(loginMemberId, boardId)) { // 관심 게시글 여부 반환
                result.setLikeBoard(true);
            }

            return response.success(result, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 상세조회 실패!");
            e.printStackTrace();
            throw new BoardException(ErrorCode.BOARD_INQUIRY_DETAIL_FAIL);
        }
    }

    /*
    게시글 수정
     */
    @Transactional
    public ResponseEntity<?> editBoard(long boardId, MultipartFile file, BoardDTO.BoardEditReqDTO boardEditReqDTO) {
        try {

            Board board = boardRepository.findByBoardId(boardId).get();

            String imageUrl = board.getImageUrl() != null ? board.getImageUrl() : null;
            if (file != null && !file.isEmpty()) { //파일이 있으면 재업로드 -> 기존 파일이 있을 경우 삭제 후 업로드
                if (imageUrl != null) {
                    deleteFile(imageUrl);
                }
                imageUrl = uploadPic(file);
            } else {
                if (boardEditReqDTO.isImageUrlDel()) { //기존 이미지를 삭제하는지 여부 -> 삭제한다면 DB에 저장된 url 삭제
                    deleteFile(imageUrl);
                    imageUrl = null;
                }
            }


            Category category = categoryRepository.findByCategoryName(boardEditReqDTO.getCategoryName()).get();
            District district = districtRepository.findByDistrictName(boardEditReqDTO.getDistrictName()).get();

            board.updatePost(category, district, imageUrl,
                    boardEditReqDTO.getTitle(), boardEditReqDTO.getContent(), boardEditReqDTO.getStartTime(),
                    boardEditReqDTO.getEndTime(), boardEditReqDTO.getTransactionStatus(), boardEditReqDTO.getPrice());

            return response.success("Edit Board Success!");

        } catch (IOException e) {
            log.error("Fail Upload file!");
            return response.fail("Fail Upload file!");
        } catch (BoardException e) {
            log.error("게시글 수정 실패!");
            throw new BoardException(ErrorCode.BOARD_EDIT_FAIL);
        }
    }

    /*
     * S3에 업로드된 파일 삭제
     */
    public void deleteFile(String imageUrl) {

        try {
            String fileName = imageUrl.replace(s3url, "");
            boolean isObjectExist = amazonS3.doesObjectExist(bucket, fileName);
            if (isObjectExist) {
                amazonS3.deleteObject(bucket, fileName);
            }
        } catch (Exception e) {
            log.debug("Delete File failed", e);
        }
    }

    /*
    게시글 삭제
     */
    @Transactional
    public ResponseEntity<?> deleteBoard(long boardId) {
        try {

            boardRepository.deleteByBoardId(boardId);

            return response.success("Delete Board Success!");

        } catch (BoardException e) {
            log.error("게시글 삭제 실패!");
            throw new BoardException(ErrorCode.BOARD_DELETE_FAIL);
        }
    }

    /*
    게시글 리스트 조회
     */
    public ResponseEntity<?> boardListInquiry(int page) {
        try {

            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository.findDefaultAll(pageRequest).map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 카테고리별
     */
    public ResponseEntity<?> boardListInquiryByCategory(int categoryId, int page) {
        try {

            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository.findByCategory_CategoryId(categoryId, pageRequest).map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 지역별
     */
    public ResponseEntity<?> boardListInquiryByDistrict(DistrictDTO.DistrictReqDTO districtReqDTO, int page) {
        try {

            List<Integer> districtIds = districtReqDTO.getDistrictIds();
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository.findByDistricts(districtIds, pageRequest).map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 카테고리 + 지역
     */
    public ResponseEntity<?> boardListInquiryByCategoryAndDistrict(int categoryId, DistrictDTO.DistrictReqDTO districtReqDTO, int page) {
        try {

            List<Integer> districtIds = districtReqDTO.getDistrictIds();
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository.findByCategoryAndDistricts(categoryId, districtIds, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 제목
     */
    public ResponseEntity<?> boardListInquiryByTitle(String title, int page) {
        try {

            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository.findByTitleContaining(title, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 제목 + 카테고리
     */
    public ResponseEntity<?> boardListInquiryByTitleAndCategory(String title, int categoryId, int page) {
        try {

            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository.findByTitleContainingAndCategory_CategoryId(title, categoryId, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 제목 + 지역
     */
    public ResponseEntity<?> boardListInquiryByTitleAndDistrict(String title, DistrictDTO.DistrictReqDTO districtReqDTO, int page) {
        try {

            List<Integer> districtIds = districtReqDTO.getDistrictIds();
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository.findByTitleAndDistricts(title, districtIds, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 제목 + 카테고리 + 지역
     */
    public ResponseEntity<?> boardListInquiryByTitleAndCategoryAndDistrict(String title, int categoryId, DistrictDTO.DistrictReqDTO districtReqDTO, int page) {
        try {

            List<Integer> districtIds = districtReqDTO.getDistrictIds();
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository.findByTitleAndCategoryAndDistricts(title, categoryId, districtIds, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 날짜 + 제목 + 카테고리 + 지역
     */
    public ResponseEntity<?> boardListInquiryByDateAndTitleAndCategoryAndDistrict(String title, String startTime, String endTime,
                                                                                  int categoryId, DistrictDTO.DistrictReqDTO districtReqDTO, int page) {
        try {
            List<Integer> districtIds = districtReqDTO.getDistrictIds();
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository
                            .findByDateAndTitleAndCategoryAndDistricts(title, categoryId, districtIds, start, end, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 날짜 + 제목 + 카테고리
     */
    public ResponseEntity<?> boardListInquiryByDateAndTitleAndCategory(String title, String startTime, String endTime,
                                                                       int categoryId, int page) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository
                            .findByDateAndTitleAndCategory(title, categoryId, start, end, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 날짜 + 제목 + 지역
     */
    public ResponseEntity<?> boardListInquiryByDateAndTitleAndDistrict(String title, String startTime, String endTime,
                                                                       DistrictDTO.DistrictReqDTO districtReqDTO, int page) {
        try {
            List<Integer> districtIds = districtReqDTO.getDistrictIds();
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository
                            .findByDateAndTitleAndDistricts(title, districtIds, start, end, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 날짜  + 제목으로 검색
     */
    public ResponseEntity<?> boardListInquiryByDateAndTitle(String title, String startTime, String endTime, int page) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository
                            .findByDateAndTitle(title, start, end, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 날짜만으로 검색
     */
    public ResponseEntity<?> boardListInquiryByDate(String startTime, String endTime, int page) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository
                            .findByDate(start, end, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 날짜 + 카테고리로 검색
     */
    public ResponseEntity<?> boardListInquiryByDateAndCategory(String startTime, String endTime, int categoryId, int page) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository
                            .findByDateAndCategory(start, end, categoryId, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 날짜 + 지역(중복선택)으로 검색
     */
    public ResponseEntity<?> boardListInquiryByDateAndDistrict(String startTime, String endTime, DistrictDTO.DistrictReqDTO districtReqDTO, int page) {
        try {
            List<Integer> districtIds = districtReqDTO.getDistrictIds();
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository
                            .findByDateAndDistrict(start, end, districtIds, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    게시글 리스트 조회 - 날짜 + 카테고리 + 지역(중복선택)으로 검색
     */
    public ResponseEntity<?> boardListInquiryByDateAndCategoryAndDistrict(String startTime, String endTime,
                                                                          DistrictDTO.DistrictReqDTO districtReqDTO, int categoryId, int page) {
        try {
            List<Integer> districtIds = districtReqDTO.getDistrictIds();
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository
                            .findByDateAndCategoryAndDistrict(start, end, categoryId, districtIds, pageRequest)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    public ResponseEntity<?> searchBoard(String title, String categoryName, String start, String end, List<String> districtNames, int page) {

        try {
            Pageable pageable = PageRequest.of(page - 1, 10);
            LocalDateTime startTime = LocalDateTime.parse(start);
            LocalDateTime endTime = LocalDateTime.parse(end);

            Slice<BoardDTO.BoardResDTO> boardList =
                    boardRepository
                            .findBySearchOption(pageable, title, categoryName, districtNames, startTime, endTime)
                            .map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }

    }

    /*
    게시글 블라인드 또는 블라인드 해제 처리 (관리자용)
     */
    @Transactional
    public ResponseEntity<?> blindBoard(long boardId) {
        try {
            Board findBoard = boardRepository.findByBoardIdNative(boardId).get();
            findBoard.blindBoard();

            return response.success("Blind Success!");

        } catch (BoardException e) {
            e.printStackTrace();
            log.error("게시글 블라인드 처리 실패");
            throw new BoardException(ErrorCode.BOARD_BLIND_FAIL);
        }


    }

    /*
    블라인드 된 게시글만 조회 (관리자용)
     */
    public ResponseEntity<?> blindBoardLookup(int page) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, 10);
            Slice<BoardDTO.BoardResDTO> boardList = boardRepository.findBlindBoardNative(pageRequest).map(BoardDTO.BoardResDTO::new);

            return response.success(boardList, "Blind Board Lookup Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
        }
    }

    /*
    회원 닉네임 누르면 회원 정보 표시
     */
    public ResponseEntity<?> boardByMemberInquiry(long boardId,int page){
        try{
            Board board = boardRepository.findByBoardId(boardId).get();


            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));
            Slice<MypageDTO.BoardAndMemberDTO> myBoardList = boardRepository.findByMember_MemberId(board.getMember().getMemberId(),pageRequest)
                    .map(MypageDTO.BoardAndMemberDTO::new);

            return response.success(myBoardList,"Success Member Info");

        }catch(MemberException e){
            throw new MemberException(com.example.newfieldpasser.exception.member.ErrorCode.SELECT_MEMBER_LIST);
        }

    }



    /*
    10분마다 양도시간이 지난 게시글들을 확인하여 블라인드 처리함
     */
    @Transactional
    @Scheduled(cron = "0 0/10 * * * *")
    public void deleteOverTime() {

        LocalDateTime nowDateTime = LocalDateTime.now();
        boardRepository.updateTimeOverPost(nowDateTime);
    }
}
