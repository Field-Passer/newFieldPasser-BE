package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Category;
import com.example.newfieldpasser.entity.District;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.parameter.Role;
import com.example.newfieldpasser.parameter.TransactionStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class BoardDTO {

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BoardReqDTO {

        private String categoryName;
        private String districtName;
        private String title;
        private String content;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime startTime;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime endTime;
        private TransactionStatus transactionStatus;
        private int price;

        public Board toEntity(Member member, Category category, District district, String imageUrl) {
            return Board.builder()
                    .member(member)
                    .category(category)
                    .district(district)
                    .title(title)
                    .content(content)
                    .startTime(startTime)
                    .endTime(endTime)
                    .imageUrl(imageUrl)
                    .transactionStatus(transactionStatus)
                    .price(price)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BoardEditReqDTO {

        private String categoryName;
        private String districtName;
        private String title;
        private String content;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime startTime;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime endTime;
        private TransactionStatus transactionStatus;
        private int price;
        private boolean imageUrlDel;

        public Board toEntity(Member member, Category category, District district, String imageUrl) {
            return Board.builder()
                    .member(member)
                    .category(category)
                    .district(district)
                    .title(title)
                    .content(content)
                    .startTime(startTime)
                    .endTime(endTime)
                    .imageUrl(imageUrl)
                    .transactionStatus(transactionStatus)
                    .price(price)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BoardResDTO {
        private long boardId;
        private String memberId;
        private String memberName;
        private String memberNickName;
        private Role memberRole;
        private String categoryName;
        private String districtName;
        private String title;
        private String content;
        private LocalDateTime registerDate;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String imageUrl;
        private TransactionStatus transactionStatus;
        private int price;
        private String phone;
        private int viewCount;
        private int wishCount;
        private boolean blind;
        private boolean deleteCheck;

        @Builder
        public BoardResDTO(Board board) {
            this.boardId = board.getBoardId();
            this.memberId = board.getMember().getMemberId();
            this.memberName = board.getMember().getMemberName();
            this.memberNickName = board.getMember().getMemberNickName();
            this.memberRole = board.getMember().getRole();
            this.categoryName = board.getCategory().getCategoryName();
            this.districtName = board.getDistrict().getDistrictName();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.registerDate = board.getRegisterDate();
            this.startTime = board.getStartTime();
            this.endTime = board.getEndTime();
            this.imageUrl = board.getImageUrl();
            this.transactionStatus = board.getTransactionStatus();
            this.price = board.getPrice();
            this.phone = board.getMember().getMemberPhone();
            this.viewCount = board.getViewCount();
            this.wishCount = board.getWishCount();
            this.blind = board.isBlind();
            this.deleteCheck = board.isDeleteCheck();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BoardDetailResDTO {
        private long boardId;
        private String memberId;
        private String memberName;
        private String memberNickName;
        private String categoryName;
        private String districtName;

        private String title;
        private String content;
        private LocalDateTime registerDate;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String imageUrl;
        private TransactionStatus transactionStatus;
        private int price;
        private String phone;
        private int viewCount;
        private int wishCount;
        private boolean blind;
        private boolean deleteCheck;
        private boolean myBoard;
        private boolean likeBoard;

        @Builder
        public BoardDetailResDTO(Board board) {
            this.boardId = board.getBoardId();
            this.memberId = board.getMember().getMemberId();
            this.memberName = board.getMember().getMemberName();
            this.memberNickName = board.getMember().getMemberNickName();
            this.categoryName = board.getCategory().getCategoryName();
            this.districtName = board.getDistrict().getDistrictName();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.registerDate = board.getRegisterDate();
            this.startTime = board.getStartTime();
            this.endTime = board.getEndTime();
            this.imageUrl = board.getImageUrl();
            this.transactionStatus = board.getTransactionStatus();
            this.price = board.getPrice();
            this.phone = board.getMember().getMemberPhone();
            this.viewCount = board.getViewCount();
            this.wishCount = board.getWishCount();
            this.blind = board.isBlind();
            this.deleteCheck = board.isDeleteCheck();
            this.myBoard = false;
            this.likeBoard = false;
        }
    }


}
