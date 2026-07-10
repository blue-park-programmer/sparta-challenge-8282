package com.sparta.spartachallenge8282.store.presentation.dto.response;

import com.sparta.spartachallenge8282.menu.presentation.dto.response.MenuResponse;
import com.sparta.spartachallenge8282.store.domain.Store;
import com.sparta.spartachallenge8282.store.domain.StoreStatus;

import java.util.UUID;

public record StoreApplicationResponse(
        UUID storeId,
        String storeName,
        StoreStatus storeStatus
) {
    public static StoreApplicationResponse from(Store store) {
        return new StoreApplicationResponse(
                store.getId(),
                store.getStoreName(),
                store.getStoreStatus()
        );
    }
}
