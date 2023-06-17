package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByMemberId(String memberId);
    Optional<Member> findByMemberId(String memberId);
}
