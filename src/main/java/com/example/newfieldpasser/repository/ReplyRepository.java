package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Reply;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply,Long> {


    Optional<Reply> findByReplyId(long replyId);

    Optional<Reply> deleteByReplyId(long replyId);

    Slice<Reply> findByComment_CommentId(long commentId, PageRequest pageRequest);
}
