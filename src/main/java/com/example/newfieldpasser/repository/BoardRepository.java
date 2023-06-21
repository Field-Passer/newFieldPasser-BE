package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
