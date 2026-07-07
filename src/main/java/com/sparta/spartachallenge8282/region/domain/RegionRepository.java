package com.sparta.spartachallenge8282.region.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {
    boolean existsByNameAndDeletedAtIsNull(String name); // 이름 중복 검사
    Optional<Region> findByIdAndDeletedAtIsNull(UUID id); // 지역 단건 조회
}
