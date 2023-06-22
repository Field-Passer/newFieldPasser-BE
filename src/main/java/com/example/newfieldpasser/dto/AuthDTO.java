package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Member;
import lombok.*;

public class AuthDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginDto {
        private String memberId;
        private String password;

        @Builder
        public LoginDto(String memberId, String password) {
            this.memberId = memberId;
            this.password = password;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignupDto {
        private String memberId;
        private String password;
        private String memberName;
        private String memberNickName;
        private String memberPhone;


        @Builder
        public SignupDto(String memberId, String password, String memberName, String memberNickName, String memberPhone) {
            this.memberId = memberId;
            this.password = password;
            this.memberName = memberName;
            this.memberNickName = memberNickName;
            this.memberPhone = memberPhone;
        }
    }

d


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TokenDto {
        private String accessToken;
        private String refreshToken;

        public TokenDto(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
