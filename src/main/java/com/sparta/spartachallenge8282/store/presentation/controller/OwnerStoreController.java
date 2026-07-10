package com.sparta.spartachallenge8282.store.presentation.controller;

import com.sparta.spartachallenge8282.global.common.ApiResponse;
import com.sparta.spartachallenge8282.store.application.StoreService;
import com.sparta.spartachallenge8282.store.presentation.dto.response.StoreApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vi/owner/store")
@RequiredArgsConstructor
public class OwnerStoreController {
    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<ApiResponse<StoreApplicationResponse>> getAllStores(){
        return null;
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<ApiResponse<StoreApplicationResponse>> getStoreByStoreId(
            @PathVariable Long ownerId
    ){
        return null;
    }
}
