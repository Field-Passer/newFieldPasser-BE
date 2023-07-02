package com.example.newfieldpasser.entity;

import com.example.newfieldpasser.dto.AuthDTO;
import com.example.newfieldpasser.parameter.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "member")
@SQLDelete(sql = "UPDATE member SET member_delete = true WHERE member_id =?")
@Where(clause = "member_delete = false")
public class Member {
    @Id
    @Column(name = "member_id")
    private String memberId;

    @Column(name = "member_password")
    private String password;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "member_nickname")
    private String memberNickName;

    @Column(name = "member_phone")
    private String memberPhone;

    @Column(name = "member_delete")
    private Boolean memberDelete;

    @CreationTimestamp
    @Column(name = "SIGNUP_DATE", nullable = false)
    private LocalDateTime signUpDate;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member")
    private List<Board> boardList;

    @OneToMany(mappedBy = "member")
    private List<Comment> commentList;

    @OneToMany(mappedBy = "member")
    private List<Question> questionList;

    @OneToMany(mappedBy = "member")
    private List<Answer> answerList;

    @OneToMany(mappedBy = "member")
    private List<WishBoard> wishBoardList;

    @OneToMany(mappedBy = "member")
    private List<Reply> replyList;

    // == 생성 메서드 == //
    public static Member registerUser(AuthDTO.SignupDto signupDto) {
        Member member = new Member();

        member.memberId = signupDto.getMemberId();
        member.password = signupDto.getPassword();
        member.memberName = signupDto.getMemberName();
        member.memberNickName = signupDto.getMemberNickName();
        member.memberPhone = signupDto.getMemberPhone();
        member.memberDelete = false;
        member.role = Role.USER;

        return member;
    }

    public void updateMember( String memberName,
                             String memberNickName,String memberPhone){

        this.memberName = memberName;
        this.memberNickName = memberNickName;
        this.memberPhone = memberPhone;
    }

    public void editPassword(String password){
        this.password= password;
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void promoteAdmin() {
        this.role = Role.ADMIN;
    }

    public void changeUser() {
        this.role = Role.USER;
    }

    public Member updateName(Member member, String memberName) {
        member.memberName = memberName;

        return member;
    }
}
