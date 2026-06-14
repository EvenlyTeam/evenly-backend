package com.evenly.common.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 간단한 liveness 헬스 체크. 상세 진단(DB 등)은 actuator {@code /actuator/health} 를 사용한다.
 */
@Tag(name = "Health", description = "헬스 체크 API")
@RestController
class HealthController {

    private final String serviceName;

    HealthController(@Value("${spring.application.name}") String serviceName) {
        this.serviceName = serviceName;
    }

    @Operation(summary = "헬스 체크", description = "서비스 생존 여부를 반환한다.")
    @ApiResponse(responseCode = "200", description = "정상")
    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("UP", serviceName, OffsetDateTime.now());
    }
}
