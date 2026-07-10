package com.sparta.spartachallenge8282.store.presentation.controller;

import com.sparta.spartachallenge8282.global.common.ApiResponse;
import com.sparta.spartachallenge8282.global.common.PageResponse;
import com.sparta.spartachallenge8282.global.security.UserDetailsImpl;
import com.sparta.spartachallenge8282.store.application.StoreService;
import com.sparta.spartachallenge8282.store.presentation.dto.request.StoreApplicationRequest;
import com.sparta.spartachallenge8282.store.presentation.dto.response.StoreApplicationDetailResponse;
import com.sparta.spartachallenge8282.store.presentation.dto.response.StoreApplicationListResponse;
import com.sparta.spartachallenge8282.store.presentation.dto.response.StoreApplicationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/store-applications")
@RequiredArgsConstructor
public class StoreApplicationController {
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<ApiResponse<StoreApplicationResponse>> createStore(
            @Valid @RequestBody StoreApplicationRequest request,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        StoreApplicationResponse response = storeService.createStore(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("가게 등록 완료", response));

    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<StoreApplicationListResponse>>> getStore(
            @AuthenticationPrincipal UserDetailsImpl user,
            @PageableDefault(size = 20) Pageable pageable
    ){
      PageResponse<StoreApplicationListResponse> response = storeService.getMyStoreApplications(user, pageable);
        return ResponseEntity.ok(ApiResponse.success("등록 현황 목록 조회 성공", response));
    }


    @GetMapping("/my/{storeId}")
    public ResponseEntity<ApiResponse<StoreApplicationDetailResponse>> getByStoreId(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal UserDetailsImpl user
    ){
        return ResponseEntity.ok(ApiResponse.success("등록 현황 상세 조회 성공", storeService.getMyStoreApplication(storeId,user)));
    }


}
