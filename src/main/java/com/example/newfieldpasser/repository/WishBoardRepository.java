package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.WishBoard;
import com.example.newfieldpasser.parameter.WishBoardId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishBoardRepository extends JpaRepository<WishBoard, WishBoardId> {

    boolean existsByMember_MemberIdAndBoard_BoardId(String memberId, long boardId);

    @EntityGraph(attributePaths = {"member","board"})
    @Query("select wb from WishBoard wb where wb.member.memberId = :memberId AND wb.board.deleteCheck = false AND wb.board.blind = false")
    Slice<WishBoard> findByMemberId(@Param("memberId") String memberId, PageRequest pageRequest);

    void deleteByBoard_BoardIdAndMember_MemberId(long boardId, String memberId);

    @EntityGraph(attributePaths = {"member","board"})
    @Query("select wb from WishBoard wb where wb.member.memberId = :memberId AND wb.board.deleteCheck = false AND wb.board.blind = false")
    Page<WishBoard> findPageByMember_MemberId(String memberId, PageRequest pageRequest);
}
