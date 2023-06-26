package com.example.newfieldpasser.service;

import com.example.newfieldpasser.dto.QuestionDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.exception.board.BoardException;
import com.example.newfieldpasser.exception.board.ErrorCode;
import com.example.newfieldpasser.repository.MemberRepository;
import com.example.newfieldpasser.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
}
