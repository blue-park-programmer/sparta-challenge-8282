package com.sparta.spartachallenge8282.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 설정.
 * BaseEntity의 @CreatedDate / @LastModifiedDate 자동 주입을 위해 필요하다.
 * @SpringBootApplication 클래스와 분리하여 슬라이스 테스트(@DataJpaTest) 시 충돌을 방지한다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
