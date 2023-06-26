package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.WishBoard;
import com.example.newfieldpasser.parameter.WishBoardId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishBoardRepository extends JpaRepository<WishBoard, WishBoardId> {

    boolean existsByMember_MemberIdAndBoard_BoardId(String memberId, long boardId);
}
