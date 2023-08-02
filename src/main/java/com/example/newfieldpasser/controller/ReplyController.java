package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.ReplyDTO;
import com.example.newfieldpasser.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    /*
    답글 등록
     */
    @PostMapping("/reply/write")
    public ResponseEntity<?> registerReply(Authentication authentication, @RequestBody ReplyDTO.replyReqDTO replyReqDTO){
        return replyService.registerReply(authentication,replyReqDTO);
    }

     /*
    답글 수정
     */
    @PutMapping("/reply/edit/{replyId}")
    public ResponseEntity<?> updateReply(@PathVariable long replyId, @RequestBody ReplyDTO.replyUpdateDTO replyUpdateDTO){
        return replyService.updateReply(replyId,replyUpdateDTO);
    }

      /*
    답글 삭제
     */
    @DeleteMapping("/reply/delete/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable long replyId){
        return replyService.deleteReply(replyId);
    }

     /*
    답글 조회
     */
    @GetMapping("/reply-lookup/{commentId}/{page}")
    public ResponseEntity<?> replyListInquiryByComment(@PathVariable long commentId,
                                                       @PathVariable int page,
                                                       Authentication authentication){
        return replyService.replyListInquiryByComment(commentId,page,authentication);
    }
}
