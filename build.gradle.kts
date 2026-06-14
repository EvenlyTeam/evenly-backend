plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.4"
}

group = "com.evenly"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    implementation("me.paulschwarz:spring-dotenv:4.0.0")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.4.0")
    testImplementation("net.jqwik:jqwik:1.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

// 단위 테스트로 다룰 로직이 없는 보일러플레이트는 커버리지 측정에서 제외
// (앱 부트스트랩/설정/JPA 엔티티/보안 인프라 — 동작 검증은 통합·수동 테스트 영역).
val coverageExclusions = listOf(
    "**/EvenlyApplication.*",
    "**/*Config.*",
    "**/*JpaEntity.*",
    "**/JwtAuthenticationFilter.*",
    "**/RestAuthenticationEntryPoint.*",
)

fun JacocoReportBase.applyCoverageExclusions() {
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) { exclude(coverageExclusions) }
    }))
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
    applyCoverageExclusions()
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    applyCoverageExclusions()
    violationRules {
        rule {
            limit {
                minimum = "0.70".toBigDecimal()
            }
        }
        rule {
            element = "CLASS"
            includes = listOf(
                "com.evenly.*.application.service.*",
                "com.evenly.*.domain.*",
            )
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.register("verify") {
    description = "Runs all verification: formatting, unit tests (ArchUnit), and coverage"
    group = "verification"
    dependsOn("spotlessCheck", "test", "jacocoTestCoverageVerification")
}

spotless {
    java {
        importOrder()
        removeUnusedImports()
        palantirJavaFormat("2.50.0")
        formatAnnotations()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
