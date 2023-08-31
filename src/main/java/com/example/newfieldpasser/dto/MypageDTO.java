package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.parameter.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class MypageDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberInfo{
        private String memberId;
        private String memberName;
        private String memberNickName;
        private String memberPhone;
        private String role;

        public MemberInfo(Member member){
            this.memberId = member.getMemberId();
            this.memberName = member.getMemberName();
            this.memberNickName = member.getMemberNickName();
            this.memberPhone = member.getMemberPhone();
            this.role = member.getRole().getTitle();
        }
    }


    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateDTO{
        private String memberId;
        private String memberName;
        private String memberNickName;
        private String memberPhone;

        public UpdateDTO(Member member) {
            this.memberId = member.getMemberId();
            this.memberName = member.getMemberName();
            this.memberNickName = member.getMemberNickName();
            this.memberPhone = member.getMemberPhone();
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class updatePassword{
        private String password;

        public updatePassword(Member member){
            this.password=member.getPassword();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BoardAndMemberDTO{
        private String memberId;
        private String memberName;
        private String memberNickName;
        private String memberPhone;
        private String role;
        private String title;
        private long boardId;


        public BoardAndMemberDTO( Board board, Member member){
            this.memberId = member.getMemberId();
            this.memberName = member.getMemberName();
            this.memberNickName = member.getMemberNickName();
            this.memberPhone = member.getMemberPhone();
            this.role = member.getRole().getTitle();
            this.title = board.getTitle();
            this.boardId = board.getBoardId();



        }
    }
}
