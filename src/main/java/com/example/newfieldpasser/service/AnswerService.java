package com.example.newfieldpasser.service;

import com.example.newfieldpasser.dto.AnswerDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.entity.Answer;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.entity.Question;
import com.example.newfieldpasser.exception.board.BoardException;
import com.example.newfieldpasser.exception.board.ErrorCode;
import com.example.newfieldpasser.parameter.QuestionProcess;
import com.example.newfieldpasser.parameter.Role;
import com.example.newfieldpasser.repository.AnswerRepository;
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
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final Response response;

    /*
    답변 등록
     */
    @Transactional
    public ResponseEntity<?> registerAnswer(Authentication authentication,
                                            long questionId,
                                            AnswerDTO.AnswerReqDTO answerReqDTO) {
        try {
            String adminId = authentication.getName();
            Member member = memberRepository.findByMemberId(adminId).get();
            Question question = questionRepository.findByQuestionId(questionId).get();

            // 답변 생성
            Answer answer = answerReqDTO.toEntity(member);

            if (question.getQuestionProcess() == QuestionProcess.BEFORE_ANSWER) { //해당 질문의 답변이 완료되지 않았을 경우에만 답변 등록
                answerRepository.save(answer);
            } else {
                log.error("이미 답변이 등록되었습니다.");
                throw new BoardException(ErrorCode.ALREADY_EXIST_ANSWER);
            }

            // 질문 테이블에 답변 외래키 입력
            question.registerAnswer(answer);
            // 답변 완료된 질문은 답변 완료 처리
            question.updateQuestionProcess();

            return response.success("Register Answer Success");

        } catch (BoardException e) {
            log.error("답변 등록 실패!");
            throw new BoardException(ErrorCode.REGISTER_ANSWER_FAIL);
        }

    }


}
