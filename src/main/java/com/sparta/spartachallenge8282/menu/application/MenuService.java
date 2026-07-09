package com.sparta.spartachallenge8282.menu.application;

import com.sparta.spartachallenge8282.global.exception.CustomException;
import com.sparta.spartachallenge8282.global.exception.ErrorCode;
import com.sparta.spartachallenge8282.menu.domain.Menu;
import com.sparta.spartachallenge8282.menu.domain.MenuRepository;
import com.sparta.spartachallenge8282.menu.presentation.dto.request.MenuCreateRequest;
import com.sparta.spartachallenge8282.menu.presentation.dto.request.MenuUpdateRequest;
import com.sparta.spartachallenge8282.menu.presentation.dto.response.MenuResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 메뉴 비즈니스 로직.
 *
 * <p>조회는 클래스 기본 {@code @Transactional(readOnly = true)}, 쓰기 메서드만 {@code @Transactional} 로 오버라이드한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional
    public UUID createMenu(MenuCreateRequest request) {
        validatePrice(request.price());

        Menu menu = Menu.builder()
                .name(request.name())
                .storeId(request.storeId())
                .description(request.description())
                .price(request.price())
                .sortOrder(request.sortOrder())
                .status(request.status())
                .badge(request.badge())
                .isAiGenerated(request.isAiGenerated())
                .build();

        return menuRepository.save(menu).getId();
    }

    public MenuResponse getMenu(UUID id) {
        Menu menu = menuRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
        return MenuResponse.from(menu);
    }

    @Transactional
    public MenuResponse updateMenu(UUID id, MenuUpdateRequest request) {
        Menu menu = menuRepository.findByIdAndDeletedAtIsNull(id)   // 조회를 가장 먼저 — 없으면 NOT_FOUND
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

        validatePrice(request.price());

        // 부분 수정: 엔티티 비즈니스 메서드가 null 필드를 각각 skip 한다 (setter 금지)
        menu.updateInfo(request.name(), request.description(), request.price(), request.sortOrder());
        menu.changeStatus(request.status());
        menu.changeBadge(request.badge());

        return MenuResponse.from(menu);   // 변경감지로 flush 되므로 save 불필요
    }

    @Transactional
    public LocalDateTime deleteMenu(UUID id, Long userId) {
        Menu menu = menuRepository.findById(id)   // 이미 삭제된 것과 없는 것을 구분하려 삭제 포함 조회
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

        if (menu.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_DELETED_MENU);
        }

        // TODO: NO_MENU_PERMISSION(가게 소유자 확인) / STORE_NOT_FOUND — store·user 연동(auth 브랜치)에서 구현
        menu.softDelete(userId);
        return menu.getDeletedAt();
    }

    private void validatePrice(Integer price) {
        if (price != null && price < 0) {
            throw new CustomException(ErrorCode.INVALID_MENU_PRICE);
        }
    }
}
