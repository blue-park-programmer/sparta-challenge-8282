package com.sparta.spartachallenge8282.user.domain;

import com.sparta.spartachallenge8282.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 엔티티.
 *
 * <p>어드민(관리자)이 탈퇴 유저 목록을 조회할 수 있도록
 * 클래스 레벨의 SQL 제한(@SQLRestriction)은 사용하지 않으며,
 * 일반 비즈니스 쿼리 시 Repository단에서 soft delete 여부를 필터링한다.
 */
@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password; // 암호화된 비밀번호

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(nullable = false, length = 100)
    private String address; // 배달 주소

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.CUSTOMER; // 기본값 CUSTOMER

    @Column(columnDefinition = "TEXT")
    private String refreshToken; // 만료 시간 대비 TEXT 타입 지정

    @Builder
    public User(String email, String password, String nickname, String address, UserRole role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.address = address;
        this.role = (role != null) ? role : UserRole.CUSTOMER;
    }

    // ── 비즈니스 편의 메서드 ───────────────────────────────────────────────────

    /**
     * 내 정보 수정 (닉네임, 주소)
     */
    public void updateProfile(String nickname, String address) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        if (address != null && !address.isBlank()) {
            this.address = address;
        }
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String encodedPassword) {
        if (encodedPassword != null && !encodedPassword.isBlank()) {
            this.password = encodedPassword;
        }
    }

    /**
     * 리프레시 토큰 저장/갱신
     */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * 로그아웃 시 리프레시 토큰 비우기
     */
    public void clearRefreshToken() {
        this.refreshToken = null;
    }

    /**
     * 역할 변경 (MASTER/MANAGER 전용)
     */
    public void updateRole(UserRole newRole) {
        if (newRole != null) {
            this.role = newRole;
        }
    }
}
