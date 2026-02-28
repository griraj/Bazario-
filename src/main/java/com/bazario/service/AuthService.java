package com.bazario.service;

import com.bazario.dto.request.LoginRequest;
import com.bazario.dto.request.RegisterRequest;
import com.bazario.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
