package com.example.newfieldpasser.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.newfieldpasser.dto.CommentDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Comment;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.exception.comment.CommentException;
import com.example.newfieldpasser.exception.comment.ErrorCode;
import com.example.newfieldpasser.exception.member.MemberException;
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

            Comment comment = commentReqDTO.toEntity(member,board);
            Comment parent;
            System.out.println("commentId"+commentReqDTO.getParentId());
            if(commentReqDTO.getParentId() != null){
                parent = commentRepository.findByCommentId(commentReqDTO.getParentId())
                        .orElseThrow(() -> new NotFoundException("Could not found comment id :" + commentReqDTO.getParentId()));
                comment.updateParent(parent);
            }

            commentRepository.save(comment);
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
            Comment comment = commentRepository.findCommentByIdWithParent(commentId)
                            .orElseThrow(() -> new NotFoundException("Coule not found comment id :" + commentId));

            if(comment.getChildren().size() != 0){
                comment.delete(true);
            }else{
                commentRepository.delete(getDeleteTableAncestorComment(comment));
            }
//            commentRepository.deleteByCommentId(commentId);
            return response.success("Comment Delete Success");
        }catch (CommentException e){
            e.printStackTrace();
            throw new CommentException(ErrorCode.COMMENT_DELETE_FAIL);
        }

    }

    private Comment getDeleteTableAncestorComment(Comment comment){
        Comment parent = comment.getParent();
        if(parent != null && parent.getChildren().size() == 1 && parent.getDeleteCheck()){
            return getDeleteTableAncestorComment(parent);
        }
        return comment;
    }

    /*
      댓글 조회 - 게시글별
     */
    public ResponseEntity<?> commentListInquiryByBoard(long boardId ,int page, Authentication authentication){
        try{
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.ASC, "commentRegisterDate"));
            Slice<CommentDTO.commentResDTO> commentList= commentRepository
                    .findByBoardId(pageRequest,boardId);

            String loginMemberId = authentication != null ? authentication.getName() : "";
            commentList.getContent().forEach(comment -> comment.setMyComment(comment.getMemberId().equals(loginMemberId)));

            return response.success(commentList,"Comment Inquiry Success");
        }catch (CommentException e){
            e.printStackTrace();
            throw new CommentException(ErrorCode.COMMENT_INQUIRY_DETAIL_FAIL);
        }
    }


    /*
    내 댓글 조회 - 멤버별
    */

    public ResponseEntity<?> commentListMember(Authentication authentication, int page){
        try{
            String memberId = authentication.getName();

            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "commentRegisterDate"));
            Slice<CommentDTO.commentResDTO> commentList = commentRepository.findByMember_MemberId(memberId,pageRequest).map(CommentDTO.commentResDTO :: new);

            if(commentList.isEmpty()){
                return response.success(commentList,"작성한 댓글이 없습니다.");
            }else{
                return response.success(commentList,"작성한 댓글을 조회 성공!");
            }
        }catch(CommentException e){
            e.printStackTrace();
            throw new CommentException(ErrorCode.MY_COMMENT_LIST_FAIL);

        }
    }

//    public ResponseEntity<?> replyCountByComment(long commentId ){
//        try{
//            Comment comment = commentRepository.findByCommentId(commentId).get();
//            return response.success(comment.getReplyCount(),"댓글 개수 조회 성공");
//
//        }catch (CommentException e){
//            e.printStackTrace();
//            return response.fail("댓글 개수 조회 실패");
//        }
//    }




}
