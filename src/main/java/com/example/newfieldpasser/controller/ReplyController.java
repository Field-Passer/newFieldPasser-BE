package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.ReplyDTO;
import com.example.newfieldpasser.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    /*
    댓글 등록
     */
    @PostMapping("/reply/write")
    public ResponseEntity<?> registerReply(Authentication authentication, @RequestBody ReplyDTO.replyReqDTO replyReqDTO){
        return replyService.registerReply(authentication,replyReqDTO);
    }
}
