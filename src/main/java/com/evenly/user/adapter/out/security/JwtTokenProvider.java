package com.evenly.user.adapter.out.security;

import com.evenly.user.application.port.out.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class JwtTokenProvider implements TokenProvider {

    private static final String TYPE_CLAIM = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final long accessValidityMs;
    private final long refreshValidityMs;

    JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-ms}") long accessValidityMs,
            @Value("${jwt.refresh-token-validity-ms}") long refreshValidityMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessValidityMs = accessValidityMs;
        this.refreshValidityMs = refreshValidityMs;
    }

    @Override
    public String issueAccessToken(UUID userId) {
        return issue(userId, TYPE_ACCESS, accessValidityMs);
    }

    @Override
    public String issueRefreshToken(UUID userId) {
        return issue(userId, TYPE_REFRESH, refreshValidityMs);
    }

    @Override
    public UUID parseAccessUserId(String token) {
        return parse(token, TYPE_ACCESS);
    }

    @Override
    public UUID parseRefreshUserId(String token) {
        return parse(token, TYPE_REFRESH);
    }

    private String issue(UUID userId, String type, long validityMs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim(TYPE_CLAIM, type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(validityMs)))
                .signWith(key)
                .compact();
    }

    private UUID parse(String token, String expectedType) {
        var claims =
                Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        if (!expectedType.equals(claims.get(TYPE_CLAIM, String.class))) {
            throw new io.jsonwebtoken.JwtException("unexpected token type");
        }
        return UUID.fromString(claims.getSubject());
    }
}
