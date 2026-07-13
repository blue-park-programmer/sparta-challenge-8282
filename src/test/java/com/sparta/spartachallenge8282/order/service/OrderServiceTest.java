package com.sparta.spartachallenge8282.order.service;

import com.sparta.spartachallenge8282.global.exception.CustomException;
import com.sparta.spartachallenge8282.global.exception.ErrorCode;
import com.sparta.spartachallenge8282.menu.domain.Menu;
import com.sparta.spartachallenge8282.menu.domain.MenuBadge;
import com.sparta.spartachallenge8282.menu.domain.MenuRepository;
import com.sparta.spartachallenge8282.menu.domain.MenuStatus;
import com.sparta.spartachallenge8282.order.dto.request.OrderCreateRequestDto;
import com.sparta.spartachallenge8282.order.dto.request.OrderItemRequestDto;
import com.sparta.spartachallenge8282.order.entity.Order;
import com.sparta.spartachallenge8282.order.entity.OrderItem;
import com.sparta.spartachallenge8282.order.repository.OrderRepository;
import com.sparta.spartachallenge8282.order.repository.OrderStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Mock
    private MenuRepository menuRepository;

    private OrderService orderService;

    private Long customerId;
    private UUID storeId;
    private UUID menuId;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository,
                orderStatusHistoryRepository,
                menuRepository
        );

        customerId = 1L;
        storeId = UUID.randomUUID();
        menuId = UUID.randomUUID();
    }

    @Test
    @DisplayName("판매 중인 메뉴를 이용해 주문을 생성할 수 있다")
    void createOrder_success() {
        Menu menu = createMenu(
                menuId,
                storeId,
                "테스트 불고기버거",
                8000,
                MenuStatus.ON_SALE,
                false
        );

        OrderCreateRequestDto request = createOrderRequest(
                storeId,
                menuId,
                2
        );

        when(menuRepository.findByIdAndDeletedAtIsNull(menuId))
                .thenReturn(Optional.of(menu));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(customerId, request);

        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        assertThat(savedOrder.getUserId())
                .isEqualTo(customerId);

        assertThat(savedOrder.getStoreId())
                .isEqualTo(storeId);

        assertThat(savedOrder.getMenuTotalPrice())
                .isEqualTo(16000);

        assertThat(savedOrder.getDeliveryFee())
                .isEqualTo(3000);

        assertThat(savedOrder.getDiscountAmount())
                .isZero();

        assertThat(savedOrder.getTotalPrice())
                .isEqualTo(19000);

        assertThat(savedOrder.getOrderItems())
                .hasSize(1);

        OrderItem savedOrderItem =
                savedOrder.getOrderItems().get(0);

        assertThat(savedOrderItem.getMenuId())
                .isEqualTo(menuId);

        assertThat(savedOrderItem.getMenuName())
                .isEqualTo("테스트 불고기버거");

        assertThat(savedOrderItem.getMenuPrice())
                .isEqualTo(8000);

        assertThat(savedOrderItem.getQuantity())
                .isEqualTo(2);

        assertThat(savedOrderItem.getTotalPrice())
                .isEqualTo(16000);

        assertThat(savedOrderItem.getOrder())
                .isSameAs(savedOrder);
    }

    @Test
    @DisplayName("존재하지 않는 메뉴로 주문하면 실패한다")
    void createOrder_fail_menuNotFound() {
        OrderCreateRequestDto request = createOrderRequest(
                storeId,
                menuId,
                1
        );

        when(menuRepository.findByIdAndDeletedAtIsNull(menuId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.createOrder(customerId, request)
        )
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException =
                            (CustomException) exception;

                    assertThat(customException.getErrorCode())
                            .isEqualTo(ErrorCode.MENU_NOT_FOUND);
                });

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    @DisplayName("다른 가게의 메뉴로 주문하면 실패한다")
    void createOrder_fail_menuStoreMismatch() {
        UUID otherStoreId = UUID.randomUUID();

        Menu menu = createMenu(
                menuId,
                otherStoreId,
                "다른 가게 메뉴",
                9000,
                MenuStatus.ON_SALE,
                false
        );

        OrderCreateRequestDto request = createOrderRequest(
                storeId,
                menuId,
                1
        );

        when(menuRepository.findByIdAndDeletedAtIsNull(menuId))
                .thenReturn(Optional.of(menu));

        assertThatThrownBy(() ->
                orderService.createOrder(customerId, request)
        )
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException =
                            (CustomException) exception;

                    assertThat(customException.getErrorCode())
                            .isEqualTo(ErrorCode.MENU_STORE_MISMATCH);
                });

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    @DisplayName("숨김 처리된 메뉴로 주문하면 실패한다")
    void createOrder_fail_hiddenMenu() {
        Menu menu = createMenu(
                menuId,
                storeId,
                "숨김 테스트 메뉴",
                7000,
                MenuStatus.ON_SALE,
                true
        );

        OrderCreateRequestDto request = createOrderRequest(
                storeId,
                menuId,
                1
        );

        when(menuRepository.findByIdAndDeletedAtIsNull(menuId))
                .thenReturn(Optional.of(menu));

        assertThatThrownBy(() ->
                orderService.createOrder(customerId, request)
        )
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException =
                            (CustomException) exception;

                    assertThat(customException.getErrorCode())
                            .isEqualTo(
                                    ErrorCode.HIDDEN_MENU_NOT_ORDERABLE
                            );
                });

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    @DisplayName("품절된 메뉴로 주문하면 실패한다")
    void createOrder_fail_soldOutMenu() {
        Menu menu = createMenu(
                menuId,
                storeId,
                "품절 테스트 메뉴",
                10000,
                MenuStatus.SOLD_OUT,
                false
        );

        OrderCreateRequestDto request = createOrderRequest(
                storeId,
                menuId,
                1
        );

        when(menuRepository.findByIdAndDeletedAtIsNull(menuId))
                .thenReturn(Optional.of(menu));

        assertThatThrownBy(() ->
                orderService.createOrder(customerId, request)
        )
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException =
                            (CustomException) exception;

                    assertThat(customException.getErrorCode())
                            .isEqualTo(ErrorCode.MENU_NOT_ORDERABLE);
                });

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    private OrderCreateRequestDto createOrderRequest(
            UUID requestStoreId,
            UUID requestMenuId,
            int quantity
    ) {
        OrderItemRequestDto orderItem =
                new OrderItemRequestDto(
                        requestMenuId,
                        quantity,
                        List.of()
                );

        return new OrderCreateRequestDto(
                requestStoreId,
                "서울특별시 종로구 세종대로 175",
                "101동 1001호",
                "문 앞에 놓아주세요.",
                List.of(orderItem)
        );
    }

    private Menu createMenu(
            UUID id,
            UUID menuStoreId,
            String name,
            int price,
            MenuStatus status,
            boolean hidden
    ) {
        Menu menu = Menu.builder()
                .storeId(menuStoreId)
                .name(name)
                .description("주문 테스트 메뉴")
                .price(price)
                .sortOrder(1)
                .status(status)
                .badge(MenuBadge.NONE)
                .isHidden(hidden)
                .isAiGenerated(false)
                .build();

        ReflectionTestUtils.setField(
                menu,
                "id",
                id
        );

        return menu;
    }
}