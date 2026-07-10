package com.sparta.spartachallenge8282.store.presentation.controller;

import com.sparta.spartachallenge8282.global.common.ApiResponse;
import com.sparta.spartachallenge8282.store.application.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 관리자의 가게 등록 관리
 */
@RestController
@RequestMapping("/api/v1/admin/store")
@RequiredArgsConstructor
public class AdminStoreController {
    private final StoreService storeService;


    //등록 신청된 가게 목록 조회
    @GetMapping
    public ResponseEntity<?> getStore() {
        return null;
    }

    //등록 신청한 가게의 상세 조회
    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStore(@PathVariable String storeId) {
        return null;
    }

    //신청한 가게의 등록을 승인
    @PatchMapping("/{storeId}/approve")
    public ResponseEntity<?> approveStore(@PathVariable String storeId) {
        return null;
    }

    //신청한 가게의 등록을 거절
    @PatchMapping("/{storeId}/reject")
    public ResponseEntity<?> rejectStore(@PathVariable String storeId) {
        return null;
    }


}
