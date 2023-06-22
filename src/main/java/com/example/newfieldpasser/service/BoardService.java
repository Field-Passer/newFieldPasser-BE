package com.example.newfieldpasser.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.newfieldpasser.dto.BoardDTO;
import com.example.newfieldpasser.dto.Response;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            String imageUrl = uploadPic(file);
            Member member = memberRepository.findByMemberId(authentication.getName()).get();
            Category category = categoryRepository.findByCategoryName(boardReqDTO.getCategoryName()).get();
            District district = districtRepository.findByDistrictName(boardReqDTO.getDistrictName()).get();

            boardRepository.save(boardReqDTO.toEntity(member, category, district, imageUrl));

            return response.success("Board Registration Success!");

        } catch (IOException e) {
            log.error("Fail Upload file!");
            return response.fail("Fail Upload file!");
        } catch (Exception e) {
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
    public ResponseEntity<?> boardInquiryDetail(long boardId) {
        try {
            BoardDTO.boardResDTO result = boardRepository.findByBoardId(boardId).map(BoardDTO.boardResDTO::new).get();

            return response.success(result, "Board Inquiry Success!");

        } catch (BoardException e) {
            log.error("게시글 상세조회 실패!");
            e.printStackTrace();
            throw new BoardException(ErrorCode.BOARD_INQUIRY_DETAIL_FAIL);
        }
    }

}
