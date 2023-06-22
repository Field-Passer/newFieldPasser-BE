package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CategoryDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CategoryResponseDTO {
        private int categoryId;
        private String categoryName;

        @Builder
        public CategoryResponseDTO(Category category) {
            this.categoryId = category.getCategoryId();
            this.categoryName = category.getCategoryName();
        }
    }
}
