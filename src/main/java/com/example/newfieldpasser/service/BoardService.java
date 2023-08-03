package com.example.newfieldpasser.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.newfieldpasser.dto.BoardDTO;
import com.example.newfieldpasser.dto.DistrictDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Category;
import com.example.newfieldpasser.entity.District;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.exception.board.BoardException;
import com.example.newfieldpasser.exception.board.ErrorCode;
import com.example.newfieldpasser.repository.BoardRepository;
import com.example.newfieldpasser.repository.CategoryRepository;
import com.example.newfieldpasser.repository.DistrictRepository;
import com.example.newfieldpasser.repository.MemberRepository;
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
    private final AmazonS3 amazonS3;
    private final Response response;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /*
    게시글 등록
     */
    @Transactional
    public ResponseEntity<?> registerBoard(MultipartFile file, Authentication authentication, BoardDTO.boardReqDTO boardReqDTO) {
        try {
            String imageUrl = file.isEmpty() ? null : uploadPic(file);
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
            BoardDTO.boardDetailResDTO result = boardRepository.findByBoardId(boardId).map(BoardDTO.boardDetailResDTO::new).get();

            String loginMemberId = authentication != null ? authentication.getName() : "";

            if (loginMemberId.equals(result.getMemberId())) {
                result.setMyBoard(true);
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
    public ResponseEntity<?> editBoard(long boardId, MultipartFile file, BoardDTO.boardReqDTO boardReqDTO) {
        try {

            Board board = boardRepository.findByBoardId(boardId).get();
            String changedImageUrl = uploadPic(file);
            Category category = categoryRepository.findByCategoryName(boardReqDTO.getCategoryName()).get();
            District district = districtRepository.findByDistrictName(boardReqDTO.getDistrictName()).get();

            board.updatePost(category, district, changedImageUrl,
                    boardReqDTO.getTitle(), boardReqDTO.getContent(), boardReqDTO.getStartTime(),
                    boardReqDTO.getEndTime(), boardReqDTO.getTransactionStatus(), boardReqDTO.getPrice());

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository.findDefaultAll(pageRequest).map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository.findByCategory_CategoryId(categoryId, pageRequest).map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository.findByDistricts(districtIds, pageRequest).map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository.findByCategoryAndDistricts(categoryId, districtIds, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository.findByTitleContaining(title, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository.findByTitleContainingAndCategory_CategoryId(title, categoryId, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository.findByTitleAndDistricts(title, districtIds, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository.findByTitleAndCategoryAndDistricts(title, categoryId, districtIds, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository
                            .findByDateAndTitleAndCategoryAndDistricts(title, categoryId, districtIds, start, end, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository
                            .findByDateAndTitleAndCategory(title, categoryId, start, end, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository
                            .findByDateAndTitleAndDistricts(title, districtIds, start, end, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository
                            .findByDateAndTitle(title, start, end, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository
                            .findByDate(start, end, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository
                            .findByDateAndCategory(start, end, categoryId, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository
                            .findByDateAndDistrict(start, end, districtIds, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository
                            .findByDateAndCategoryAndDistrict(start, end, categoryId, districtIds, pageRequest)
                            .map(BoardDTO.boardResDTO::new);

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

            Slice<BoardDTO.boardResDTO> boardList =
                    boardRepository
                            .findBySearchOption(pageable, title, categoryName, districtNames, startTime, endTime)
                            .map(BoardDTO.boardResDTO::new);

            return response.success(boardList, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 리스트 조회 실패!");
            throw new BoardException(ErrorCode.BOARD_LIST_INQUIRY_FAIL);
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
