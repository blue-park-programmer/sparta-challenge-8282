package com.sparta.spartachallenge8282.store.application;

import com.sparta.spartachallenge8282.category.domain.Category;
import com.sparta.spartachallenge8282.category.domain.CategoryRepository;
import com.sparta.spartachallenge8282.global.common.PageResponse;
import com.sparta.spartachallenge8282.global.exception.CustomException;
import com.sparta.spartachallenge8282.global.exception.ErrorCode;
import com.sparta.spartachallenge8282.global.security.UserDetailsImpl;
import com.sparta.spartachallenge8282.region.domain.Region;
import com.sparta.spartachallenge8282.region.domain.RegionRepository;
import com.sparta.spartachallenge8282.store.domain.*;
import com.sparta.spartachallenge8282.store.presentation.dto.request.StoreApplicationRequest;
import com.sparta.spartachallenge8282.store.presentation.dto.request.StoreRejectRequest;
import com.sparta.spartachallenge8282.store.presentation.dto.response.*;
import com.sparta.spartachallenge8282.user.entity.User;
import com.sparta.spartachallenge8282.user.entity.UserRole;
import com.sparta.spartachallenge8282.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StoreApplicationRepository storeApplicationRepository;

    //가게 등록 및 조회 절차
    /**
     * 가게 등록 신청
     *
     * CUSTOMER와 OWNER 모두 신청할 수 있다.
     */
    @Transactional
    public MyStoreApplicationCreateResponse createStoreApplication(
            StoreApplicationRequest request,
            UserDetailsImpl userDetails
    ) {
       validateStoreApplicationRole(userDetails);
       Category category = categoryRepository.findById(request.categoryId())
               .orElseThrow(() ->
                       new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
               );

       Region region = regionRepository.findById(request.regionId())
               .orElseThrow(() ->
                       new CustomException(ErrorCode.REGION_NOT_FOUND)
               );
       User applicantUser = userRepository.findById(userDetails.userId())
                .orElseThrow(() ->
                        new CustomException(ErrorCode.USER_NOT_FOUND)
                );


        StoreApplication application = StoreApplication.builder()
                .applicant(applicantUser)
                .category(category)
                .region(region)
                .storeName(request.storeName())
                .storeTel(request.storeTel())
                .storeImage(request.storeImage())
                .address(request.address())
                .minOrderPrice(request.minOrderPrice())
                .deliveryFee(request.deliveryFee())
                .freeDeliveryAmount(request.freeDeliveryAmount())
                .openTime(request.openTime())
                .closeTime(request.closeTime())
                .build();

        return MyStoreApplicationCreateResponse.from(storeApplicationRepository.save(application));
    }

    /**
     * 본인의 가게 등록 신청 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<MyStoreApplicationListResponse> getMyStoreApplications(
            UserDetailsImpl userDetails,
            Pageable pageable
    ) {
        return PageResponse.from(
                storeApplicationRepository.findAllByApplicant_Id(userDetails.userId(), pageable)
                        .map(MyStoreApplicationListResponse::from)
        );
    }

    /**
     * 본인의 가게 등록 신청 상세 조회
     */
    @Transactional(readOnly = true)
    public MyStoreApplicationDetailResponse getMyStoreApplication(
            UUID applicationId,
            UserDetailsImpl userDetails
    ) {
        return storeApplicationRepository
                .findByIdAndApplicant_Id(applicationId, userDetails.userId())
                .map(MyStoreApplicationDetailResponse::from)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.STORE_APPLICATION_NOT_FOUND)
                );
    }


    //관리자의 가게 조회 및 등록 승인/거절 절차 ===================

    /**
     * 관리자 가게 등록 신청 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminStoreApplicationListResponse> getAdminStoreApplications(StoreApplicationStatus status, Pageable pageable, UserDetailsImpl userDetails) {
        validateManagerRole(userDetails);
        Page<StoreApplication> applications;

        if(status == null){
            applications = storeApplicationRepository.findAll(pageable);
        }else{
            applications = storeApplicationRepository.findAllByStatus(status, pageable);
        }
        return PageResponse.from(applications.map(AdminStoreApplicationListResponse::from));
    }

    /**
     * 관리자 가게 등록 신청 상세 조회
     */
    @Transactional(readOnly = true)
    public AdminStoreApplicationDetailResponse getAdminStoreApplication(UUID applicationId, UserDetailsImpl userDetails) {
        validateManagerRole(userDetails);

        StoreApplication application = storeApplicationRepository.findById(applicationId)
                .orElseThrow(()->
                        new CustomException(ErrorCode.STORE_APPLICATION_NOT_FOUND));
        return AdminStoreApplicationDetailResponse.from(application);
    }


    /**
     * 가게 등록 신청 승인
     *
     * MANAGER 또는 MASTER만 가능하다.
     */
    @Transactional
    public StoreApplicationProcessResponse approveStore(UUID applicationId, UserDetailsImpl userDetails) {
        validateManagerRole(userDetails);

        StoreApplication application = storeApplicationRepository.findById(applicationId)
                .orElseThrow(()->
                        new CustomException(ErrorCode.STORE_APPLICATION_NOT_FOUND)
                );

        //승인 또는 거절은 PENDING 상태에서만 가능하게 한다.
        if (application.getStatus() != StoreApplicationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_STORE_APPLICATION_STATUS);
        }
        application.approve();

        Store savedStore = storeRepository.save(Store.from(application));

        application.getApplicant().promoteToOwner();
        return StoreApplicationProcessResponse.from(application, savedStore);
    }

    /**
     * 가게 등록 신청 거절
     *
     * MANAGER 또는 MASTER만 가능하다.
     * 거절 시 사용자 권한은 변경하지 않는다.
     */
    @Transactional
    public StoreApplicationProcessResponse rejectStore(UUID applicationId, StoreRejectRequest request, UserDetailsImpl userDetails) {
        validateManagerRole(userDetails);

        StoreApplication application = storeApplicationRepository.findById(applicationId)
                .orElseThrow(()->
                        new CustomException(ErrorCode.STORE_APPLICATION_NOT_FOUND)
                );
        //승인 또는 거절은 PENDING 상태에서만 가능하게 한다.
        if (application.getStatus() != StoreApplicationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_STORE_APPLICATION_STATUS);
        } else if (request.rejectionReason() == null || request.rejectionReason().isBlank()) {
            throw new CustomException(ErrorCode.REJECTION_REASON_REQUIRED);
        }

        application.reject(request.rejectionReason());
        return StoreApplicationProcessResponse.from(application);



    }


    //일반 사용자 및 비회원 사용자의 가게 조회 ==========================

    /**
     * 가게 목록 조회
     *
     */
    public PageResponse<UserStoreListResponse> getStores(Pageable pageable) {
        Page<Store> stores = storeRepository.findAll(pageable);
        return PageResponse.from(stores.map(UserStoreListResponse::from));
    }

    /**
     * 가게 상세 조회
     *
     */
    public UserStoreDetailResponse getStore(UUID storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                ()-> new CustomException(ErrorCode.STORE_NOT_FOUND)
                );
        return  UserStoreDetailResponse.from(store);
    }



    //권한 검증 ==============
    /**
     * 가게 등록 신청 권한 검사
     *
     * CUSTOMER와 OWNER 모두 신청 가능하다.
     */
    private void validateStoreApplicationRole(UserDetailsImpl userDetails) {
        boolean isCustomer = UserRole.CUSTOMER.getAuthority().equals(userDetails.role());
        boolean isOwner = UserRole.OWNER.getAuthority().equals(userDetails.role());
        if(!isCustomer && !isOwner){
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }



    /**
     * 관리자 승인·거절 권한 검사
     */
    private void validateManagerRole(UserDetailsImpl userDetails) {
        boolean isManager = UserRole.MANAGER.getAuthority().equals(userDetails.role());
        boolean isMaster = UserRole.MASTER.getAuthority().equals(userDetails.role());

        if(!isManager && !isMaster){
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }



}
