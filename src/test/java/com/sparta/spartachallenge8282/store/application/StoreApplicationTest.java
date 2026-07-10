package com.sparta.spartachallenge8282.store.application;

import com.sparta.spartachallenge8282.category.domain.Category;
import com.sparta.spartachallenge8282.category.domain.CategoryRepository;
import com.sparta.spartachallenge8282.global.exception.CustomException;
import com.sparta.spartachallenge8282.global.exception.ErrorCode;
import com.sparta.spartachallenge8282.global.security.UserDetailsImpl;
import com.sparta.spartachallenge8282.region.domain.Region;
import com.sparta.spartachallenge8282.region.domain.RegionRepository;
import com.sparta.spartachallenge8282.store.domain.Store;
import com.sparta.spartachallenge8282.store.domain.StoreRepository;
import com.sparta.spartachallenge8282.store.presentation.dto.request.StoreApplicationRequest;
import com.sparta.spartachallenge8282.store.presentation.dto.response.StoreApplicationResponse;
import com.sparta.spartachallenge8282.user.entity.User;
import com.sparta.spartachallenge8282.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    private StoreService storeService;

    @BeforeEach
    void setUp() {
        storeService = new StoreService(
                storeRepository,
                regionRepository,
                categoryRepository,
                userRepository
        );
    }

    @Test
    @DisplayName("가게 등록 신청에 성공한다")
    void createStore_success() {
        // given
        UUID categoryId = UUID.randomUUID();
        UUID regionId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Long userId = 1L;

        UserDetailsImpl userDetails =
                new UserDetailsImpl(userId, "user@test.com", "ROLE_USER");

        StoreApplicationRequest request = new StoreApplicationRequest(
                categoryId,
                regionId,
                "테스트 치킨집",
                "02-1234-5678",
                "https://image.com/store.jpg",
                "서울특별시 강남구",
                15000,
                3000,
                30000,
                LocalTime.of(10, 0),
                LocalTime.of(22, 0)
        );

        Category category = mock(Category.class);
        Region region = mock(Region.class);
        User user = mock(User.class);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.of(region));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        /*
         * StoreApplicationResponse.from()에서 Store의 getter를 사용하므로
         * 실제 Store 객체를 반환하게 설정한다.
         */
        when(storeRepository.save(any(Store.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        StoreApplicationResponse response =
                storeService.createStore(request, userDetails);

        // then
        ArgumentCaptor<Store> storeCaptor =
                ArgumentCaptor.forClass(Store.class);

        verify(storeRepository).save(storeCaptor.capture());

        Store savedStore = storeCaptor.getValue();

        assertThat(savedStore.getOwner()).isEqualTo(user);
        assertThat(savedStore.getCategory()).isEqualTo(category);
        assertThat(savedStore.getRegion()).isEqualTo(region);
        assertThat(savedStore.getStoreName()).isEqualTo("테스트 치킨집");
        assertThat(savedStore.getStoreTel()).isEqualTo("02-1234-5678");
        assertThat(savedStore.getStoreImage())
                .isEqualTo("https://image.com/store.jpg");
        assertThat(savedStore.getAddress())
                .isEqualTo("서울특별시 강남구");
        assertThat(savedStore.getMinOrderPrice()).isEqualTo(15000);
        assertThat(savedStore.getDeliveryFee()).isEqualTo(3000);
        assertThat(savedStore.getFreeDeliveryAmount()).isEqualTo(30000);
        assertThat(savedStore.getOpenTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(savedStore.getCloseTime()).isEqualTo(LocalTime.of(22, 0));

        assertThat(response).isNotNull();

        verify(categoryRepository).findById(categoryId);
        verify(regionRepository).findById(regionId);
        verify(userRepository).findById(userId);
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리이면 가게 등록에 실패한다")
    void createStore_categoryNotFound() {
        // given
        UUID categoryId = UUID.randomUUID();
        UUID regionId = UUID.randomUUID();
        Long userId = 1L;

        UserDetailsImpl userDetails =
                new UserDetailsImpl(userId, "owner@test.com", "ROLE_OWNER");

        StoreApplicationRequest request = new StoreApplicationRequest(
                categoryId,
                regionId,
                "테스트 치킨집",
                "02-1234-5678",
                null,
                "서울특별시 강남구",
                15000,
                3000,
                30000,
                LocalTime.of(10, 0),
                LocalTime.of(22, 0)
        );

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                storeService.createStore(request, userDetails)
        )
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException =
                            (CustomException) exception;

                    assertThat(customException.getErrorCode())
                            .isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);
                });

        verify(categoryRepository).findById(categoryId);

        verifyNoInteractions(
                regionRepository,
                userRepository,
                storeRepository
        );
    }

    @Test
    @DisplayName("존재하지 않는 지역이면 가게 등록에 실패한다")
    void createStore_regionNotFound() {
        // given
        UUID categoryId = UUID.randomUUID();
        UUID regionId = UUID.randomUUID();
        Long userId = 1L;

        UserDetailsImpl userDetails =
                new UserDetailsImpl(userId, "owner@test.com", "ROLE_OWNER");

        StoreApplicationRequest request = new StoreApplicationRequest(
                categoryId,
                regionId,
                "테스트 치킨집",
                "02-1234-5678",
                null,
                "서울특별시 강남구",
                15000,
                3000,
                30000,
                LocalTime.of(10, 0),
                LocalTime.of(22, 0)
        );

        Category category = mock(Category.class);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                storeService.createStore(request, userDetails)
        )
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException =
                            (CustomException) exception;

                    assertThat(customException.getErrorCode())
                            .isEqualTo(ErrorCode.REGION_NOT_FOUND);
                });

        verify(categoryRepository).findById(categoryId);
        verify(regionRepository).findById(regionId);

        verifyNoInteractions(
                userRepository,
                storeRepository
        );
    }

    @Test
    @DisplayName("존재하지 않는 사용자이면 가게 등록에 실패한다")
    void createStore_userNotFound() {
        // given
        UUID categoryId = UUID.randomUUID();
        UUID regionId = UUID.randomUUID();
        Long userId = 1L;

        UserDetailsImpl userDetails =
                new UserDetailsImpl(userId, "owner@test.com", "ROLE_OWNER");

        StoreApplicationRequest request = new StoreApplicationRequest(
                categoryId,
                regionId,
                "테스트 치킨집",
                "02-1234-5678",
                null,
                "서울특별시 강남구",
                15000,
                3000,
                30000,
                LocalTime.of(10, 0),
                LocalTime.of(22, 0)
        );

        Category category = mock(Category.class);
        Region region = mock(Region.class);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.of(region));

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                storeService.createStore(request, userDetails)
        )
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException =
                            (CustomException) exception;

                    assertThat(customException.getErrorCode())
                            .isEqualTo(ErrorCode.USER_NOT_FOUND);
                });

        verify(categoryRepository).findById(categoryId);
        verify(regionRepository).findById(regionId);
        verify(userRepository).findById(userId);

        verifyNoInteractions(storeRepository);
    }
}