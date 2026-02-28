package com.bazario.security;

import com.bazario.entity.Role;
import com.bazario.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "TestSecretKeyForJWTSigningThatIsAtLeast256BitsLongEnoughHere",
                3_600_000L // 1 hour
        );

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.ROLE_CUSTOMER)
                .passwordHash("hashed")
                .firstName("Test")
                .lastName("User")
                .build();
    }

    @Test
    @DisplayName("generateToken should produce a non-blank token")
    void generateToken_ShouldReturnNonBlankToken() {
        String token = jwtService.generateToken(user);
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("extractUsername should return user email from token")
    void extractUsername_ShouldReturnEmail() {
        String token = jwtService.generateToken(user);
        assertThat(jwtService.extractUsername(token)).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("isTokenValid should return true for a freshly generated token")
    void isTokenValid_ShouldReturnTrue_ForFreshToken() {
        String token = jwtService.generateToken(user);
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid should return false for a tampered token")
    void isTokenValid_ShouldReturnFalse_ForTamperedToken() {
        String token = jwtService.generateToken(user) + "tampered";
        assertThat(jwtService.isTokenValid(token, user)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid should return false for an expired token")
    void isTokenValid_ShouldReturnFalse_ForExpiredToken() {
        JwtService shortLivedJwtService = new JwtService(
                "TestSecretKeyForJWTSigningThatIsAtLeast256BitsLongEnoughHere",
                -1L // expired immediately
        );
        String token = shortLivedJwtService.generateToken(user);
        assertThat(shortLivedJwtService.isTokenValid(token, user)).isFalse();
    }
}
