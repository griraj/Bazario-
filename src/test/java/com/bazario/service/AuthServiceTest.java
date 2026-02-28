package com.bazario.service;

import com.bazario.dto.request.LoginRequest;
import com.bazario.dto.request.RegisterRequest;
import com.bazario.dto.response.AuthResponse;
import com.bazario.entity.Role;
import com.bazario.entity.User;
import com.bazario.exception.ConflictException;
import com.bazario.repository.UserRepository;
import com.bazario.security.JwtService;
import com.bazario.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("jane.doe@example.com");
        registerRequest.setPassword("SecureP@ss1");

        savedUser = User.builder()
                .id(1L)
                .email("jane.doe@example.com")
                .passwordHash("hashed")
                .firstName("Jane")
                .lastName("Doe")
                .role(Role.ROLE_CUSTOMER)
                .build();
    }

    @Test
    @DisplayName("US-01: register should return token when email is unique")
    void register_ShouldReturnAuthResponse_WhenEmailIsUnique() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("jwt.token.here");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getAccessToken()).isEqualTo("jwt.token.here");
        assertThat(response.getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(response.getRole()).isEqualTo(Role.ROLE_CUSTOMER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("US-01: register should throw ConflictException when email already exists")
    void register_ShouldThrowConflict_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("email already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("US-02: login should return token for valid credentials")
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("jane.doe@example.com");
        loginRequest.setPassword("SecureP@ss1");

        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(savedUser));
        when(jwtService.generateToken(savedUser)).thenReturn("jwt.token.here");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getAccessToken()).isEqualTo("jwt.token.here");
        assertThat(response.getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    @DisplayName("US-02: login should throw BadCredentialsException for wrong password")
    void login_ShouldThrowBadCredentials_WhenPasswordIsWrong() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("jane.doe@example.com");
        loginRequest.setPassword("WrongPassword");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }
}
