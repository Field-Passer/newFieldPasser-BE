package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.District;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DistrictDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DistrictResponseDTO {
        private int districtId;
        private String districtName;

        @Builder
        public DistrictResponseDTO(District district) {
            this.districtId = district.getDistrictId();
            this.districtName = district.getDistrictName();
        }
    }
}
