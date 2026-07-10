package com.sparta.spartachallenge8282.store.presentation.dto.response;

import com.sparta.spartachallenge8282.store.domain.Store;
import com.sparta.spartachallenge8282.store.domain.StoreStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record StoreApplicationDetailResponse(
        UUID storeId,
        String ownerNickNAme,
        String ownerEmail,
        String categoryName,
        String regionName,
        String storeName,
        String storeTel,
        String storeImage,
        String address,
        Integer minOrderPrice,
        Integer deliveryFee,
        Integer freeDeliveryAmount,
        LocalTime openTime,
        LocalTime closeTime,
        StoreStatus storeStatus,
        LocalDateTime createdAt,
        LocalDateTime approvedAt,
        LocalDateTime rejectedAt,
        String rejectionReason

) {
    public static StoreApplicationDetailResponse from(Store store) {
        return new StoreApplicationDetailResponse(
                store.getId(),
                store.getOwner().getNickname(),
                store.getOwner().getEmail(),
                store.getCategory().getName(),
                store.getRegion().getName(),
                store.getStoreName(),
                store.getStoreTel(),
                store.getStoreImage(),
                store.getAddress(),
                store.getMinOrderPrice(),
                store.getDeliveryFee(),
                store.getFreeDeliveryAmount(),
                store.getOpenTime(),
                store.getCloseTime(),
                store.getStoreStatus(),

                store.getCreatedAt(),
                store.getApprovedAt(),
                store.getRejectedAt(),
                store.getRejectionReason()
        );
    }
}
