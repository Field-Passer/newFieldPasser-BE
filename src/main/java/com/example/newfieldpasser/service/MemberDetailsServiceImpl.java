package com.example.newfieldpasser.service;

import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public MemberDetailsImpl loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member findMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find user with this memberId. -> " + memberId));

        if (findMember != null) {
            return new MemberDetailsImpl(findMember);
        }

        return null;
    }
}
