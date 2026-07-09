package com.sparta.spartachallenge8282.menu.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {

    /** 단건 조회/수정 시: 삭제되지 않은 메뉴만. */
    Optional<Menu> findByIdAndDeletedAtIsNull(UUID id);

    // 삭제 시에는 "없는 것"과 "이미 삭제된 것"을 구분하기 위해 JpaRepository 기본 findById(삭제 포함)를 그대로 쓴다.
    // 목록/검색(가게별 필터·키워드·페이징)은 feat/NN-menu-search 브랜치에서 추가한다.
}
