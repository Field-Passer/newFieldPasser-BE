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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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



    /*
        댓글 생성
     */
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

    /*
       댓글 수정
    */
    @Transactional
    public ResponseEntity<?> updateComment(long commentId, CommentDTO.commentUpdateDTO commentUpdateDTO){
        try{

            Comment comment = commentRepository.findByCommentId(commentId).get();


            comment.updateComment( commentUpdateDTO.getCommentContent());

            return response.success("Edit Comment Success");
        }catch (CommentException e){
            e.printStackTrace();
            throw new CommentException(ErrorCode.COMMENT_EDIT_FAIL);
        }
    }


     /*
        댓글 삭제
     */

    @Transactional
    public ResponseEntity<?> deleteComment(long commentId ){
        try{

            commentRepository.deleteByCommentId(commentId);
            return response.success("Comment Delete Success");
        }catch (CommentException e){
            e.printStackTrace();
            throw new CommentException(ErrorCode.COMMENT_DELETE_FAIL);
        }

    }

    /*
      댓글 조회 - 게시글별
     */
    public ResponseEntity<?> commentListInquiryByBoard(long boardId ,int page ){
        try{
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.ASC, "commentRegisterDate"));
            Slice<CommentDTO.commentResDTO> commentList= commentRepository.findByBoard_BoardId(boardId,pageRequest).map(CommentDTO.commentResDTO::new);

            return response.success(commentList,"Comment Inquiry Success");
        }catch (CommentException e){
            e.printStackTrace();
            throw new CommentException(ErrorCode.COMMENT_INQUIRY_DETAIL_FAIL);
        }
    }
}
