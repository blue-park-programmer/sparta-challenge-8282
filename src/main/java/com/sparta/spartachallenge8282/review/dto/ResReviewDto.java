package com.sparta.spartachallenge8282.review.dto;

import com.sparta.spartachallenge8282.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 리뷰 데이터 응답 DTO
 * 리뷰에 필요한 데이터
 * */

public record ResReviewDto (

        UUID reviewId,
        UUID storeId,
        Integer rating,
        String content,
        String imageUrl,
        LocalDateTime createdAt
) {

    public static ResReviewDto from(Review review) {
        return new ResReviewDto(
                review.getId(),
                review.getStoreId(),
                review.getRating(),
                review.getContent(),
                review.getImageUrl(),
                review.getCreatedAt()
        );
    }
}
