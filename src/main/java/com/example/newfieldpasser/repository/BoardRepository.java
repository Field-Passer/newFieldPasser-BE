package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByBoardId(long boardId);
    @Modifying
    @Query("update Board b set b.viewCount = b.viewCount + 1 where b.boardId = :boardId")
    void updateViewCount(@Param("boardId") long boardId);

    @Modifying
    void deleteByBoardId(@Param("boardId") long boardId);
}
