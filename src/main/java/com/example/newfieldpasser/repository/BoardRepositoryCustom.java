package com.example.newfieldpasser.repository;


import com.example.newfieldpasser.entity.Board;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardRepositoryCustom {
    Slice<Board> findBySearchOption(Pageable pageable, String title, String categoryName, List<Integer> districtIds, LocalDateTime startTime, LocalDateTime endTime);
}
