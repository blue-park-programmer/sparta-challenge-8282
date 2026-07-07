package com.sparta.spartachallenge8282.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security UserDetails 구현체.
 * JWT 파싱 결과(userId, username, role)를 SecurityContext에 담아
 * 컨트롤러에서 @AuthenticationPrincipal로 꺼내 쓴다.
 *
 * <pre>
 * 컨트롤러 사용 예:
 *   @AuthenticationPrincipal UserDetailsImpl userDetails
 *   userDetails.getUserId()   → Long   (DB PK, AuditorAware 등 내부 사용)
 *   userDetails.getUsername() → String (JWT subject = username)
 *   userDetails.getRole()     → String (ex. "ROLE_CUSTOMER")
 * </pre>
 */
@Getter
public class UserDetailsImpl implements UserDetails {

    private final Long userId;
    private final String username;  // JWT subject (PRD 스펙: payload의 username)
    private final String role;

    public UserDetailsImpl(Long userId, String username, String role) {
        this.userId   = userId;
        this.username = username;
        this.role     = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return null; }  // JWT 방식이므로 사용 안 함
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
