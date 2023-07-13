package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Board;
import static com.example.newfieldpasser.entity.QBoard.board;
import static com.example.newfieldpasser.entity.QMember.member;
import static com.example.newfieldpasser.entity.QCategory.category;
import static com.example.newfieldpasser.entity.QDistrict.district;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;

public class BoardRepositoryImpl extends QuerydslRepositorySupport implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BoardRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Board.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Slice<Board> findBySearchOption(Pageable pageable, String title, String categoryName,
                                           List<Integer> districtIds, LocalDateTime startTime, LocalDateTime endTime) {

        List<Board> boardList =
                queryFactory
                        .selectFrom(board)
                        .leftJoin(board.member, member)
                        .fetchJoin()
                        .leftJoin(board.category, category)
                        .fetchJoin()
                        .leftJoin(board.district, district)
                        .fetchJoin()
                        .where(eqTitle(title), eqCategoryName(categoryName), searchDateFilter(startTime, endTime), districtIdsFilter(districtIds))
                        .orderBy(board.registerDate.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize() + 1)
                        .fetch();

        boolean hasNext = false;
        if (boardList.size() > pageable.getPageSize()) {
            boardList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(boardList, pageable, hasNext);
    }

    private BooleanExpression eqTitle(String title) {
        if(title == null || title.isEmpty()) {
            return null;
        }
        return board.title.containsIgnoreCase(title);
    }

    private BooleanExpression eqCategoryName(String categoryName) {
        if(categoryName == null || categoryName.isEmpty()) {
            return null;
        }
        return board.category.categoryName.eq(categoryName);
    }

    // 날짜 필터
    private BooleanExpression searchDateFilter(LocalDateTime startTime, LocalDateTime endTime) {

        BooleanExpression betweenStartTime = board.startTime.between(startTime, endTime);
        BooleanExpression betweenEndTime = board.endTime.between(startTime, endTime);

        return Expressions.allOf(betweenStartTime, betweenEndTime);
    }

    //지역 리스트 필터
    private BooleanExpression districtIdsFilter(List<Integer> districtIds) {
        return districtIds != null ? Expressions.anyOf(districtIds.stream().map(this::districtIdFilter).toArray(BooleanExpression[]::new)) : null;
    }

    private BooleanExpression districtIdFilter(Integer districtId) {
        return board.district.districtId.eq(districtId);
    }

}
