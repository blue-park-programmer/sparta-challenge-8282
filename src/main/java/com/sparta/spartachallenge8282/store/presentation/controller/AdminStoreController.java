package com.sparta.spartachallenge8282.store.presentation.controller;

import com.sparta.spartachallenge8282.global.common.ApiResponse;
import com.sparta.spartachallenge8282.global.common.PageResponse;
import com.sparta.spartachallenge8282.global.security.UserDetailsImpl;
import com.sparta.spartachallenge8282.store.application.StoreService;
import com.sparta.spartachallenge8282.store.domain.StoreApplicationStatus;
import com.sparta.spartachallenge8282.store.presentation.dto.request.StoreRejectRequest;
import com.sparta.spartachallenge8282.store.presentation.dto.response.AdminStoreApplicationDetailResponse;
import com.sparta.spartachallenge8282.store.presentation.dto.response.AdminStoreApplicationListResponse;
import com.sparta.spartachallenge8282.store.presentation.dto.response.StoreApplicationProcessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


/**
 * 관리자의 가게 등록 관리
 */
@RestController
@RequestMapping("/api/v1/admin/store-applications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_MASTER')")
public class AdminStoreController {
    private final StoreService storeService;




    /**
     * 가게 등록 신청 승인
     */
    @PatchMapping("/{applicationId}/approve")
    public ResponseEntity<ApiResponse<StoreApplicationProcessResponse>> approveStore(
            @PathVariable UUID applicationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
       StoreApplicationProcessResponse response = storeService.approveStore(applicationId, userDetails);

       return ResponseEntity.ok(ApiResponse.success("가게 등록 승인 성공", response));

    }

    /**
     * 가게 등록 신청 거절
     */
    @PatchMapping("/{applicationId}/reject")
    public ResponseEntity<ApiResponse<StoreApplicationProcessResponse>> rejectStore(
            @PathVariable UUID applicationId,
            @Valid @RequestBody StoreRejectRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        StoreApplicationProcessResponse response = storeService.rejectStore(applicationId, request, userDetails);
        return ResponseEntity.ok(ApiResponse.success("가게 등록 거절 성공", response));
    }


    /**
     * 가게 등록 신청 목록 조회
     *
     * status를 전달하지 않으면 전체 조회
     * status를 전달하면 해당 상태만 조회
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<PageResponse<AdminStoreApplicationListResponse>>> getStoreApplications(
            @RequestParam(required = false) StoreApplicationStatus status,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails

            ) {
        PageResponse<AdminStoreApplicationListResponse> responses =
                storeService.getAdminStoreApplications(
                        status,
                        pageable,
                        userDetails
                );

       return ResponseEntity.ok(ApiResponse.success("등록 신청된 가게 목록 조회 성공", responses));
    }


    /**
     * 등록된 가게 상세 조회
     */
    @GetMapping("/{applicationId}")
    public ResponseEntity<ApiResponse<AdminStoreApplicationDetailResponse>> getStoreApplication(
            @PathVariable UUID applicationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        AdminStoreApplicationDetailResponse response =
                storeService.getAdminStoreApplication(
                        applicationId,
                        userDetails
                );
        return ResponseEntity.ok(ApiResponse.success("등록 신청된 가게 상세 조회 성공", response));
    }

}
