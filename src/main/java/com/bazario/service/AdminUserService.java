package com.bazario.service;

import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
    PagedResponse<UserResponse> getAllUsers(Pageable pageable);

    void updateUserStatus(Long userId, boolean enabled);
}
