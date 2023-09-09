package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>,CommentRepositoryCustom {


    Optional<Comment> findByCommentId(long commentId);

    Optional<Comment> deleteByCommentId(long commentId);

    Slice<Comment> findByBoard_BoardId(long boardId , PageRequest pageRequest);

    Slice<Comment> findByMember_MemberId(String memberId, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","board"})
    @Query("select c from Comment c where c.member.memberId = :memberId AND c.board.deleteCheck = false AND c.board.blind = false")
    Page<Comment> findPageByMember_MemberId(String memberId, PageRequest pageRequest);


}
