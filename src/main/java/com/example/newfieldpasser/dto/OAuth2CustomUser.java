package com.example.newfieldpasser.dto;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.List;
@AllArgsConstructor
public class OAuth2CustomUser implements OAuth2User, Serializable {

    private String registrationId;
    private Map<String, Object> attributes;
    private List<GrantedAuthority> authorities;
    private String memberId;

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return this.registrationId;
    }

    public String getMemberId() {
        return this.memberId;
    }
}
