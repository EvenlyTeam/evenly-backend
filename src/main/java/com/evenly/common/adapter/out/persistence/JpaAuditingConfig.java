package com.evenly.common.adapter.out.persistence;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 설정.
 *
 * <p>Spring Data 기본 DateTimeProvider 는 {@code OffsetDateTime} 필드를 채우지 못하므로,
 * {@code OffsetDateTime} 을 직접 반환하는 provider 를 등록한다. ({@link BaseJpaEntity} 가 사용)
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
class JpaAuditingConfig {

    @Bean
    DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.<TemporalAccessor>of(OffsetDateTime.now());
    }
}
