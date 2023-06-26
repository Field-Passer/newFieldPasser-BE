package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.entity.WishBoard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WishBoardDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class WishBoardReqDTO {
        private String memberId;
        private long boardId;

        public WishBoard toEntity(Member member, Board board) {
            return WishBoard.builder()
                    .member(member)
                    .board(board)
                    .build();
        }
    }
}
