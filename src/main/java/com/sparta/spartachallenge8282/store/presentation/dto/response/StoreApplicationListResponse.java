package com.sparta.spartachallenge8282.store.presentation.dto.response;

import com.sparta.spartachallenge8282.store.application.StoreService;
import com.sparta.spartachallenge8282.store.domain.Store;
import com.sparta.spartachallenge8282.store.domain.StoreStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoreApplicationListResponse(
        UUID storeId,
        String storeName,
        String storeImage,
        String categoryName,
        StoreStatus storeStatus,
        LocalDateTime appliedAt

) {
    public static StoreApplicationListResponse from(Store store) {
        return new StoreApplicationListResponse(
                store.getId(),
                store.getStoreName(),
                store.getStoreImage(),
                store.getCategory().getName(),
                store.getStoreStatus(),
                store.getCreatedAt()
        );
    }

}
