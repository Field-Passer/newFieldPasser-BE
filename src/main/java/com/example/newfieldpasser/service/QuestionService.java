package com.example.newfieldpasser.service;

import com.example.newfieldpasser.dto.QuestionDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.entity.Question;
import com.example.newfieldpasser.exception.board.BoardException;
import com.example.newfieldpasser.exception.board.ErrorCode;
import com.example.newfieldpasser.repository.MemberRepository;
import com.example.newfieldpasser.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final Response response;

    /*
    문의글 등록
     */
    @Transactional
    public ResponseEntity<?> registerQuestion(Authentication authentication, QuestionDTO.QuestionReqDTO questionReqDTO) {
        try {
            String memberId = authentication.getName();
            Member member = memberRepository.findByMemberId(memberId).get();

            questionRepository.save(questionReqDTO.toEntity(member));

            return response.success("Register Question Success!");

        } catch (BoardException e) {
            log.error("문의글 등록 실패!");
            throw new BoardException(ErrorCode.REGISTER_QUESTION_FAIL);
        }
    }

    /*
    문의글 리스트 조회
     */
    public ResponseEntity<?> questionInquiry(int page, Authentication authentication) {
        try {
            String memberId = authentication.getName();
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "questionRegisterDate"));

            Slice<QuestionDTO.QuestionResDTO> result = questionRepository.findByMember_MemberId(memberId, pageRequest).map(QuestionDTO.QuestionResDTO::new);

            return response.success(result, "Question Inquiry Success!");

        } catch (BoardException e) {
            log.error("문의글 조회 실패!");
            throw new BoardException(ErrorCode.QUESTION_LIST_INQUIRY_FAIL);
        }
    }

    /*
    문의글 상세조회
     */
    public ResponseEntity<?> questionDetail(long questionId) {
        try {
            QuestionDTO.QuestionResDTO result = questionRepository.findByQuestionId(questionId).map(QuestionDTO.QuestionResDTO::new).get();

            return response.success(result, "Question Detail Inquiry Success!");

        } catch (BoardException e) {
            log.error("문의글 조회 실패!");
            throw new BoardException(ErrorCode.QUESTION_LIST_INQUIRY_FAIL);
        }
    }

    /*
    문의글 수정
     */
    @Transactional
    public ResponseEntity<?> editQuestion(long questionId, QuestionDTO.QuestionReqDTO questionReqDTO) {
        try {
            Question findQuestion = questionRepository.findByQuestionId(questionId).get();

            findQuestion.updateQuestion(questionReqDTO.getQuestionTitle(), questionReqDTO.getQuestionContent(),
                                        questionReqDTO.getQuestionCategory(), questionReqDTO.getQuestionProcess());

            return response.success("Edit Question Success!");

        } catch (BoardException e) {
            log.error("문의글 수정 실패!");
            throw new BoardException(ErrorCode.QUESTION_EDIT_FAIL);
        }
    }

    /*
    문의글 삭제
     */
    @Transactional
    public ResponseEntity<?> deleteQuestion(long questionId) {
        try {
            questionRepository.deleteByQuestionId(questionId);

            return response.success("Delete Question Success!");

        } catch (BoardException e) {
            log.error("문의글 삭제 실패!");
            throw new BoardException(ErrorCode.QUESTION_DELETE_FAIL);
        }
    }

    public ResponseEntity<?> inquiryAllQuestion(int page) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "questionRegisterDate"));
            Slice<QuestionDTO.QuestionResDTO> result = questionRepository.findDefaultAll(pageRequest).map(QuestionDTO.QuestionResDTO::new);

            return response.success(result, "All Question List Inquiry Success!");

        } catch (BoardException e) {
            log.error("문의글 조회 실패!");
            throw new BoardException(ErrorCode.QUESTION_LIST_INQUIRY_FAIL);
        }
    }

}
