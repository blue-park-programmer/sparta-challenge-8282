package com.sparta.spartachallenge8282.review.dto;


import com.sparta.spartachallenge8282.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 리뷰 리스트 응답 DTO
 * 리뷰 목록을 조회
 * */

public record ResReviewListItemDto (
        UUID reviewId,
        String userNickname,
        Integer rating,
        String content,
        String imageUrl,
        Object reply, // 임시 답글 객체
        LocalDateTime createdAt
) {



    public static ResReviewListItemDto from(Review review) {
        return new ResReviewListItemDto(
                review.getId(),
                null,
                review.getRating(),
                review.getContent(),
                review.getImageUrl(),
                null,
                review.getCreatedAt()
        );

    }

}
