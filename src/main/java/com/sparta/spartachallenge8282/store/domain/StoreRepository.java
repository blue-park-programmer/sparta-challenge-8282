package com.sparta.spartachallenge8282.store.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    Page<Store> findAllByOwner_Id(Long id, Pageable pageable);

    Optional<Store> findByIdAndOwner_Id(UUID storeId, Long aLong);
}
