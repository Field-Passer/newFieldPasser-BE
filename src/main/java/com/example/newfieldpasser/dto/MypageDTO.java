package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MypageDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberInfo{
        private String memberId;
        private String memberName;
        private String memberNickName;
        private String memberPhone;

        public MemberInfo(Member member){
            this.memberId = member.getMemberId();
            this.memberName = member.getMemberName();
            this.memberNickName = member.getMemberNickName();
            this.memberPhone = member.getMemberPhone();
        }
    }
}
