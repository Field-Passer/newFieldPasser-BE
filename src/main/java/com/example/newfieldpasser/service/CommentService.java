package com.example.newfieldpasser.service;

import com.example.newfieldpasser.dto.CommentDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Comment;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.exception.comment.CommentException;
import com.example.newfieldpasser.exception.comment.ErrorCode;
import com.example.newfieldpasser.repository.BoardRepository;
import com.example.newfieldpasser.repository.CommentRepository;
import com.example.newfieldpasser.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final Response response;



    @Transactional
    public ResponseEntity<?> registerComment (Authentication authentication, CommentDTO.commentReqDTO commentReqDTO){
        try{
            Member member = memberRepository.findByMemberId(authentication.getName()).get();
            Board board = boardRepository.findByBoardId(commentReqDTO.getBoardId()).get();

            commentRepository.save(commentReqDTO.toEntity(member,board));
            return response.success("Comment registration Success!");

        }catch (Exception e){
            e.printStackTrace();
            log.error("Fail Register Comment");
            return response.fail("Fail register Comment");
        }

    }

    @Transactional
    public ResponseEntity<?> updateComment(long commentId,Authentication authentication, CommentDTO.commentUpdateDTO commentUpdateDTO){
        try{

            Comment comment = commentRepository.findByCommentId(commentId).get();
            Member member = memberRepository.findByMemberId(authentication.getName()).get();
            Board board = boardRepository.findByBoardId(commentUpdateDTO.getBoardId()).get();

            comment.updateComment(member,board, commentUpdateDTO.getCommentContent(),
                                    commentUpdateDTO.getCommentUpdateDate());

            return response.success("Edit Comment Success");
        }catch (CommentException e){
            e.printStackTrace();
            throw new CommentException(ErrorCode.COMMENT_EDIT_FAIL);
        }
    }

}
