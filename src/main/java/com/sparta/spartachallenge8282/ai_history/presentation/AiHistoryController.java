package com.sparta.spartachallenge8282.ai_history.presentation;

import com.sparta.spartachallenge8282.ai_history.presentation.dto.request.AiHistoryCreateRequestDto;
import com.sparta.spartachallenge8282.ai_history.presentation.dto.response.AiHistoryItemResponseDto;
import com.sparta.spartachallenge8282.ai_history.presentation.dto.response.AiHistoryResultResponseDto;
import com.sparta.spartachallenge8282.ai_history.application.AiHistoryService;
import com.sparta.spartachallenge8282.global.common.ApiResponse;
import com.sparta.spartachallenge8282.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * AI 메뉴 설명 생성/조회 API.
 *
 * 생성(POST)과 조회(GET)만 있고 수정/삭제는 없다 - AiHistory가
 * 로그성 이력이라 한 번 생성되면 변경하지 않기 때문이다.
 * 생성된 설명을 실제 메뉴에 반영하는 것은 별도의 Menu 도메인 API
 * (PATCH /menus/{id}/ai-description)에서 처리한다.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AiHistoryController {

    private final AiHistoryService aiHistoryService;

    /**
     * AI로 메뉴 설명을 생성한다.
     *
     * <p>자동/수동 모드 구분은 컨트롤러가 아닌 Service의 buildPrompt()에서
     * 처리한다 - 요청 body의 prompt 필드 유무로 내부에서 자동 분기되므로
     * 엔드포인트는 단일하다.
     *
     * <p>Gemini 호출이 실패해도 HTTP 200으로 응답한다
     * (응답 body의 isSuccess=false로 실패 여부를 확인).
     */

    @PostMapping("/ai/menu-description")
    public ResponseEntity<ApiResponse<AiHistoryResultResponseDto>> createAiHistory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AiHistoryCreateRequestDto requestDto) {
        AiHistoryResultResponseDto response = aiHistoryService.createAiHistory(requestDto, userDetails.userId());

    return ResponseEntity.ok(ApiResponse.success("AI 요청이 처리되었습니다.",response));
    }

    /**
     * 특정 메뉴의 AI 생성 이력을 최신순으로 조회한다.
     * 기본 페이지 크기 10, 생성일 내림차순 정렬.
     */
    @GetMapping("/menus/{menuId}/ai-histories")
    public ResponseEntity<ApiResponse<List<AiHistoryItemResponseDto>>> getAiHistories(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID menuId,
            @PageableDefault(size = 10, sort = "createdAt" , direction = Sort.Direction.DESC)Pageable pageable
            ) {
        List<AiHistoryItemResponseDto> responses = aiHistoryService.getAiHistories(menuId, pageable);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", responses));
    }
}
