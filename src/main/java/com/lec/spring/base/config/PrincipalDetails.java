package com.lec.spring.base.config;

import com.lec.spring.base.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;

    public User getUser(){
        return user;
    }

    // 일반 로그인 생성자
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // Oauth2 인증용 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        System.out.println("""
       UserDetails(user, oauth attributes) 생성:
           user: %s
           attributes: %s
       """.formatted(user, attributes));
        this.user = user;
        this.attributes = attributes;
    }

    // 사용자의 권한 정보를 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getAuthority()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    // oauth2
    private Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return null;
    }
}
