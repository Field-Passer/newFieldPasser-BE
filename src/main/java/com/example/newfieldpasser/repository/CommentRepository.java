package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Comment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    Optional<Comment> findByCommentId(long commentId);

    Optional<Comment> findByParent( long parentId);

    Optional<Comment> deleteByCommentId(long commentId);

    Slice<Comment> findByBoard_BoardId(long boardId , PageRequest pageRequest);

    Slice<Comment> findByMember_MemberId(String memberId, PageRequest pageRequest);
}
