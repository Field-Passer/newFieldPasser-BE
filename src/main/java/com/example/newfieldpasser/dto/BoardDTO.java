package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Category;
import com.example.newfieldpasser.entity.District;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.parameter.TransactionStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class BoardDTO {

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class boardReqDTO {

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
}
