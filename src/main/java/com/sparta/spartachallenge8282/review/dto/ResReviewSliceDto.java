package com.sparta.spartachallenge8282.review.dto;

import com.sparta.spartachallenge8282.review.entity.Review;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Collectors;
/**
 * 리뷰 슬라이스 응답 DTO.
 * 가게별 리뷰 목록을 페이징(Slice) 처리한 응답 데이터.
 */

public record ResReviewSliceDto(
        List<ResReviewListItemDto> content,
        boolean hasNext
) {

    public static ResReviewSliceDto from(Slice<Review> slice) {
        List<ResReviewListItemDto> content = slice.getContent().stream()
                .map(ResReviewListItemDto::from)
                .collect(Collectors.toList());

        return new ResReviewSliceDto(
                content,
                slice.hasNext()
        );
    }
}
