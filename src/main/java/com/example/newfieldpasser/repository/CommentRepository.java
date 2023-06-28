package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Comment;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    Optional<Comment> findByCommentId(long commentId);

    Optional<Comment> deleteByCommentId(long commentId);

    Slice<Comment> findByBoard_BoardId(long boardId);
}
