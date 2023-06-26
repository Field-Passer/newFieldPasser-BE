package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.entity.WishBoard;
import com.example.newfieldpasser.parameter.TransactionStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class WishBoardResDTO {
        private long boardId;
        private String title;
        private String memberName;
        private String categoryName;
        private String districtName;
        private LocalDateTime registerDate;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String imageUrl;
        private TransactionStatus transactionStatus;
        private int price;
        private int viewCount;
        private int wishCount;

        @Builder
        public WishBoardResDTO(WishBoard wishBoard) {
            this.boardId = wishBoard.getBoard().getBoardId();
            this.title = wishBoard.getBoard().getTitle();
            this.memberName = wishBoard.getMember().getMemberName();
            this.categoryName = wishBoard.getBoard().getCategory().getCategoryName();
            this.districtName = wishBoard.getBoard().getDistrict().getDistrictName();
            this.registerDate = wishBoard.getBoard().getRegisterDate();
            this.startTime = wishBoard.getBoard().getStartTime();
            this.endTime = wishBoard.getBoard().getEndTime();
            this.imageUrl = wishBoard.getBoard().getImageUrl();
            this.transactionStatus = wishBoard.getBoard().getTransactionStatus();
            this.price = wishBoard.getBoard().getPrice();
            this.viewCount = wishBoard.getBoard().getViewCount();
            this.wishCount = wishBoard.getBoard().getWishCount();
        }
    }
}
