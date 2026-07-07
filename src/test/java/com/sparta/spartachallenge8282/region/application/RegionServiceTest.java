package com.sparta.spartachallenge8282.region.application;

import com.sparta.spartachallenge8282.global.exception.CustomException;
import com.sparta.spartachallenge8282.global.exception.ErrorCode;
import com.sparta.spartachallenge8282.region.domain.Region;
import com.sparta.spartachallenge8282.region.domain.RegionRepository;
import com.sparta.spartachallenge8282.region.presentation.dto.request.RegionCreateRequest;
import com.sparta.spartachallenge8282.region.presentation.dto.response.RegionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionService regionService;

    @Test
    void 지역생성_성공하면_생성된_id를_반환한다() {
        // given
        RegionCreateRequest request = new RegionCreateRequest("광화문", 1, true, false);
        given(regionRepository.existsByNameAndDeletedAtIsNull("광화문")).willReturn(false);

        UUID generatedId = UUID.randomUUID();
        Region saved = mock(Region.class);                 // save가 돌려줄 가짜 엔티티
        given(saved.getId()).willReturn(generatedId);
        given(regionRepository.save(any(Region.class))).willReturn(saved);

        // when
        UUID result = regionService.createRegion(request);

        // then
        assertThat(result).isEqualTo(generatedId);
    }

    @Test
    void 지역생성_이름중복이면_DUPLICATE_REGION_NAME() {
        // given
        RegionCreateRequest request = new RegionCreateRequest("광화문", 1, true, false);
        given(regionRepository.existsByNameAndDeletedAtIsNull("광화문")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> regionService.createRegion(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_REGION_NAME);

        // 그리고 save가 안 불렸는지
        verify(regionRepository, never()).save(any());
    }

    @Test
    void 단건조회_성공하면_RegionResponse를_반환한다() {
        // given
        UUID id = UUID.randomUUID();
        Region region = Region.builder()
                .name("광화문").sortOrder(1).isActive(true).isServiceAvailable(false)
                .build();
        ReflectionTestUtils.setField(region, "id", id);   // 빌더로는 id를 못 넣어서 주입
        given(regionRepository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.of(region));

        // when
        RegionResponse result = regionService.getRegion(id);

        // then
        assertThat(result.regionId()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("광화문");
        assertThat(result.sortOrder()).isEqualTo(1);
        assertThat(result.isActive()).isTrue();
        assertThat(result.isServiceAvailable()).isFalse();
    }

    @Test
    void 단건조회_없는id는_REGION_NOT_FOUND를_던진다() {
        // given
        UUID id = UUID.randomUUID();
        given(regionRepository.findByIdAndDeletedAtIsNull(id))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> regionService.getRegion(id))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REGION_NOT_FOUND);
    }

}
