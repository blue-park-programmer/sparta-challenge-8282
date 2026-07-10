package com.sparta.spartachallenge8282.store.application;

import com.sparta.spartachallenge8282.category.domain.Category;
import com.sparta.spartachallenge8282.category.domain.CategoryRepository;
import com.sparta.spartachallenge8282.global.common.PageResponse;
import com.sparta.spartachallenge8282.global.exception.CustomException;
import com.sparta.spartachallenge8282.global.exception.ErrorCode;
import com.sparta.spartachallenge8282.global.security.UserDetailsImpl;
import com.sparta.spartachallenge8282.region.domain.Region;
import com.sparta.spartachallenge8282.region.domain.RegionRepository;
import com.sparta.spartachallenge8282.store.domain.Store;
import com.sparta.spartachallenge8282.store.domain.StoreRepository;
import com.sparta.spartachallenge8282.store.presentation.dto.request.StoreApplicationRequest;
import com.sparta.spartachallenge8282.store.presentation.dto.response.StoreApplicationDetailResponse;
import com.sparta.spartachallenge8282.store.presentation.dto.response.StoreApplicationListResponse;
import com.sparta.spartachallenge8282.store.presentation.dto.response.StoreApplicationResponse;
import com.sparta.spartachallenge8282.user.entity.User;
import com.sparta.spartachallenge8282.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public StoreApplicationResponse createStore(
            StoreApplicationRequest request,
            UserDetailsImpl userDetails
    ) {

       Category category = categoryRepository.findById(request.categoryId())
               .orElseThrow(() ->
                       new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
               );

       Region region = regionRepository.findById(request.regionId())
               .orElseThrow(() ->
                       new CustomException(ErrorCode.REGION_NOT_FOUND)
               );
       User user = userRepository.findById(userDetails.userId())
                .orElseThrow(() ->
                        new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Store store = Store.builder()
                .owner(user)
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

        return StoreApplicationResponse.from(storeRepository.save(store));
    }

    @Transactional(readOnly = true)
    public PageResponse<StoreApplicationListResponse> getMyStoreApplications(
            UserDetailsImpl userDetails,
            Pageable pageable
    ) {
        return PageResponse.from(
                storeRepository
                        .findAllByOwner_Id(userDetails.userId(), pageable)
                        .map(StoreApplicationListResponse::from)
        );
    }

    @Transactional(readOnly = true)
    public StoreApplicationDetailResponse getMyStoreApplication(
            UUID storeId,
            UserDetailsImpl userDetails
    ) {
        return storeRepository
                .findByIdAndOwner_Id(storeId, userDetails.userId())
                .map(StoreApplicationDetailResponse::from)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.STORE_NOT_FOUND)
                );
    }
}
