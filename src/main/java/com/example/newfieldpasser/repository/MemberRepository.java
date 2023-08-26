package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByMemberId(String memberId);
    Optional<Member> findByMemberId(String memberId);

    Optional<Member> findByMemberNickName(String memberNickName);

    @Modifying
    void deleteByMemberId(String memberId);




}
