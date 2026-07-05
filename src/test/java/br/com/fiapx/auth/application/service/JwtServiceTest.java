package br.com.fiapx.auth.application.service;

import br.com.fiapx.auth.domain.model.User;
import br.com.fiapx.auth.domain.model.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("dGVzdHNlY3JldGtleWZvcnVuaXR0ZXN0c29ubHkxMjM0NTY3OA==", 86400000L);
        testUser = new User(UUID.randomUUID(), "test@test.com", "testuser",
                "hashedpassword", UserRole.USER, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtService.generateToken(testUser);
        assertThat(token).isNotBlank();
    }

    @Test
    void validateAndExtractClaims_shouldExtractCorrectUserId() {
        String token = jwtService.generateToken(testUser);
        UUID extracted = jwtService.extractUserId(token);
        assertThat(extracted).isEqualTo(testUser.id());
    }

    @Test
    void validateAndExtractClaims_shouldExtractEmail() {
        String token = jwtService.generateToken(testUser);
        var claims = jwtService.validateAndExtractClaims(token);
        assertThat(claims.get("email", String.class)).isEqualTo(testUser.email());
    }

    @Test
    void validateAndExtractClaims_shouldExtractRole() {
        String token = jwtService.generateToken(testUser);
        var claims = jwtService.validateAndExtractClaims(token);
        assertThat(claims.get("role", String.class)).isEqualTo(UserRole.USER.name());
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(testUser);
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        assertThat(jwtService.isTokenValid("invalid.token.here")).isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() {
        JwtService shortLivedService = new JwtService(
                "dGVzdHNlY3JldGtleWZvcnVuaXR0ZXN0c29ubHkxMjM0NTY3OA==", -1000L);
        String token = shortLivedService.generateToken(testUser);
        assertThat(shortLivedService.isTokenValid(token)).isFalse();
    }

    @Test
    void validateAndExtractClaims_shouldThrowForExpiredToken() {
        JwtService shortLivedService = new JwtService(
                "dGVzdHNlY3JldGtleWZvcnVuaXR0ZXN0c29ubHkxMjM0NTY3OA==", -1000L);
        String token = shortLivedService.generateToken(testUser);
        assertThatThrownBy(() -> shortLivedService.validateAndExtractClaims(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
