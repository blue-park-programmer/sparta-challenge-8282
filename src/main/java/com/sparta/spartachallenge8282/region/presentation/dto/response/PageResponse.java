package com.sparta.spartachallenge8282.region.presentation.dto.response;

public record PageResponse<T>(
        java.util.List<T> content,
        int page, int size,
        long totalElements, int totalPages,
        boolean hasNext
) {
    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> p) {
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(),
                p.getTotalElements(), p.getTotalPages(), p.hasNext());
    }
}
