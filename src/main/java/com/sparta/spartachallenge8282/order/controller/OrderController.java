package com.sparta.spartachallenge8282.order.controller;

import com.sparta.spartachallenge8282.order.dto.request.OrderCreateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    /*
     * 주문 생성 API
     * POST /api/v1/orders
     */
    @PostMapping
    public String createOrder(@Valid @RequestBody OrderCreateRequestDto request) {

        // 1단계에서는 DB 저장하지 않고 요청이 정상적으로 들어오는지만 확인
        return "주문 생성 요청 수신 완료";
    }

}
