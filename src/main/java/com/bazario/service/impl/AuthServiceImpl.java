package com.bazario.service.impl;

import com.bazario.dto.request.LoginRequest;
import com.bazario.dto.request.RegisterRequest;
import com.bazario.dto.response.AuthResponse;
import com.bazario.entity.Role;
import com.bazario.entity.User;
import com.bazario.exception.ConflictException;
import com.bazario.repository.UserRepository;
import com.bazario.security.JwtService;
import com.bazario.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("An account with this email already exists");
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase().strip())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName().strip())
                .lastName(request.getLastName().strip())
                .role(Role.ROLE_CUSTOMER)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return AuthResponse.of(token, user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException automatically if invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        String token = jwtService.generateToken(user);
        return AuthResponse.of(token, user);
    }
}
