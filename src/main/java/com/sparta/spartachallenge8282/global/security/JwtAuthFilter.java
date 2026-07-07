package com.sparta.spartachallenge8282.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 인증 필터.
 * Authorization 헤더에서 Bearer 액세스 토큰을 추출·검증하고
 * SecurityContext에 인증 정보(UserDetailsImpl)를 설정한다.
 *
 * <p>토큰이 없거나 유효하지 않으면 SecurityContext를 설정하지 않고 다음 필터로 넘긴다.
 * 인증 필요 엔드포인트에서의 최종 거부는 AuthEntryPoint가 담당한다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = jwtProvider.resolveToken(bearerToken);

        if (token != null && jwtProvider.validateToken(token)) {
            setAuthentication(token);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token) {

        Long userId = jwtProvider.getUserIdFromToken(token);
        String username = jwtProvider.getEmailFromToken(token);
        String role = jwtProvider.getRoleFromToken(token);

        UserDetailsImpl userDetails = new UserDetailsImpl(userId, username, role);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
