package com.example.newfieldpasser.service;


import com.example.newfieldpasser.dto.ReplyDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.entity.Comment;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.entity.Reply;
import com.example.newfieldpasser.exception.reply.ErrorCode;
import com.example.newfieldpasser.exception.reply.ReplyException;
import com.example.newfieldpasser.repository.CommentRepository;
import com.example.newfieldpasser.repository.MemberRepository;
import com.example.newfieldpasser.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final Response response;

    /*
     댓글 생성
     */

    @Transactional
    public ResponseEntity<?> registerReply(Authentication authentication, ReplyDTO .replyReqDTO replyReqDTO){
        try{
            Member member = memberRepository.findByMemberId(authentication.getName()).get();
            Comment comment = commentRepository.findByCommentId(replyReqDTO.getCommentId()).get();

            replyRepository.save(replyReqDTO.toEntity(member,comment));
            return response.success("Reply registration Success");

        }catch (Exception e){
            e.printStackTrace();
            return response.fail("Fail register Reply");
        }
    }

     /*
    답글 수정
     */

    @Transactional
    public ResponseEntity<?> updateReply(long replyId, ReplyDTO.replyUpdateDTO replyUpdateDTO){
        try{
            Reply reply = replyRepository.findByReplyId(replyId).get();

            reply.updateReply(replyUpdateDTO.getReplyContent());

            return response.success("Edit Reply Success");
        }catch (ReplyException e) {
            e.printStackTrace();
            throw new ReplyException(ErrorCode.Reply_EDIT_FAIL);
        }
    }
}
