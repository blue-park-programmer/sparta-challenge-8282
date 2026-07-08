package com.sparta.spartachallenge8282.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
/**
 * 리뷰 응답 DTO
 * 리뷰 응답시 리뷰 아이디를 반환 -> API 응답 확인용
 * */

public record ResReviewResultDto (
       UUID reviewId
) {
    public static ResReviewResultDto from(UUID reviewId) {
        return new ResReviewResultDto(reviewId);
    }
}
