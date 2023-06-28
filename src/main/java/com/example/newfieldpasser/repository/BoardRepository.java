package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Member;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByBoardId(long boardId);
    @Modifying
    @Query("update Board b set b.viewCount = b.viewCount + 1 where b.boardId = :boardId")
    void updateViewCount(@Param("boardId") long boardId);

    @Modifying
    void deleteByBoardId(@Param("boardId") long boardId);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b")
    Slice<Board> findDefaultAll(PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    Slice<Board> findByCategory_CategoryId(int categoryId, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    Slice<Board> findByDistrict_DistrictId(int districtId, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    Slice<Board> findByCategory_CategoryIdAndDistrict_DistrictId(int categoryId, int districtId, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    Slice<Board> findByTitleContaining(String title, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    Slice<Board> findByTitleContainingAndCategory_CategoryId(String title, int categoryId, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    Slice<Board> findByTitleContainingAndDistrict_DistrictId(String title, int districtId, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    Slice<Board> findByTitleContainingAndCategory_CategoryIdAndDistrict_DistrictId(String title,
                                                                                   int categoryId,
                                                                                   int districtId,
                                                                                   PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.title like %:title% " +
            "and b.category.categoryId = :categoryId " +
            "and b.district.districtId = :districtId " +
            "and b.startTime between :startTime and :endTime " +
            "and b.endTime between :startTime and :endTime")
    Slice<Board> findByDateAndTitleAndCategoryAndDistrict(@Param("title") String title, @Param("categoryId") int categoryId,
                                                          @Param("districtId") int districtId, @Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.title like %:title% " +
            "and b.category.categoryId = :categoryId " +
            "and b.startTime between :startTime and :endTime " +
            "and b.endTime between :startTime and :endTime")
    Slice<Board> findByDateAndTitleAndCategory(@Param("title") String title, @Param("categoryId") int categoryId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.title like %:title% " +
            "and b.district.districtId = :districtId " +
            "and b.startTime between :startTime and :endTime " +
            "and b.endTime between :startTime and :endTime")
    Slice<Board> findByDateAndTitleAndDistrict(@Param("title") String title, @Param("districtId") int districtId,
                                               @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
                                               PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.title like %:title% " +
            "and b.startTime between :startTime and :endTime " +
            "and b.endTime between :startTime and :endTime")
    Slice<Board> findByDateAndTitle(@Param("title") String title, @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime, PageRequest pageRequest);

    @Modifying
    @Query("update Board b set b.wishCount = b.wishCount + 1 where b.boardId = :boardId")
    void updateWishCount(@Param("boardId") long boardId);

    @Modifying
    @Query("update Board b set b.wishCount = b.wishCount - 1 where b.boardId = :boardId")
    void minusWishCount(@Param("boardId") long boardId);

    Slice<Board> findByMember_MemberId(String memberId);
}
