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
import java.util.List;
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
    @Query("select b from Board b " +
            "where b.district.districtId IN (:districtIds)")
    Slice<Board> findByDistricts(@Param("districtIds") List<Integer> districtIds, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.category.categoryId =:categoryId " +
            "and b.district.districtId IN (:districtIds)")
    Slice<Board> findByCategoryAndDistricts(@Param("categoryId") int categoryId, @Param("districtIds") List<Integer> districtIds, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    Slice<Board> findByTitleContaining(String title, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    Slice<Board> findByTitleContainingAndCategory_CategoryId(String title, int categoryId, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.title like %:title% " +
            "and b.district.districtId IN (:districtIds)")
    Slice<Board> findByTitleAndDistricts(@Param("title") String title, @Param("districtIds") List<Integer> districtIds, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.title like %:title% " +
            "and b.category.categoryId = :categoryId " +
            "and b.district.districtId IN (:districtIds)")
    Slice<Board> findByTitleAndCategoryAndDistricts(@Param("title") String title,
                                                    @Param("categoryId") int categoryId,
                                                    @Param("districtIds") List<Integer> districtIds,
                                                    PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.title like %:title% " +
            "and b.category.categoryId = :categoryId " +
            "and b.district.districtId IN (:districtIds) " +
            "and b.startTime between :startTime and :endTime " +
            "and b.endTime between :startTime and :endTime")
    Slice<Board> findByDateAndTitleAndCategoryAndDistricts(@Param("title") String title, @Param("categoryId") int categoryId,
                                                          @Param("districtIds") List<Integer> districtIds, @Param("startTime") LocalDateTime startTime,
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
            "and b.district.districtId IN (:districtIds) " +
            "and b.startTime between :startTime and :endTime " +
            "and b.endTime between :startTime and :endTime")
    Slice<Board> findByDateAndTitleAndDistricts(@Param("title") String title, @Param("districtIds") List<Integer> districtIds,
                                               @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
                                               PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.title like %:title% " +
            "and b.startTime between :startTime and :endTime " +
            "and b.endTime between :startTime and :endTime")
    Slice<Board> findByDateAndTitle(@Param("title") String title, @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.startTime between :startTime and :endTime " +
            "and b.endTime between :startTime and :endTime")
    Slice<Board> findByDate(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.startTime between :startTime and :endTime " +
            "and b.endTime between :startTime and :endTime " +
            "and b.category.categoryId = :categoryId")
    Slice<Board> findByDateAndCategory(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
                                       @Param("categoryId") int categoryId, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.startTime between :startTime and :endTime " +
            "and b.endTime between :startTime and :endTime " +
            "and b.district.districtId IN (:districtIds)")
    Slice<Board> findByDateAndDistrict(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
                                       @Param("districtIds") List<Integer> districtIds, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"member","category","district"})
    @Query("select b from Board b " +
            "where b.startTime between :startTime and :endTime " +
            "and b.endTime between :startTime and :endTime " +
            "and b.category.categoryId = :categoryId " +
            "and b.district.districtId IN (:districtIds)")
    Slice<Board> findByDateAndCategoryAndDistrict(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
                                                  @Param("categoryId") int categoryId, @Param("districtIds") List<Integer> districtIds,
                                                  PageRequest pageRequest);

    @Modifying
    @Query("update Board b set b.wishCount = b.wishCount + 1 where b.boardId = :boardId")
    void updateWishCount(@Param("boardId") long boardId);

    @Modifying
    @Query("update Board b set b.wishCount = b.wishCount - 1 where b.boardId = :boardId")
    void minusWishCount(@Param("boardId") long boardId);

    Slice<Board> findByMember_MemberId(String memberId,PageRequest pageRequest);
}
