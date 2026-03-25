package com.bazario.controller;

import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.UserResponse;
import com.bazario.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin Users", description = "Endpoints for admin to manage users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminUserService.getAllUsers(pageable));
    }

    @PutMapping("/{userId}/activate")
    @Operation(summary = "Activate user account (Admin only)")
    public ResponseEntity<Void> activateUser(@PathVariable Long userId) {
        adminUserService.updateUserStatus(userId, true);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/deactivate")
    @Operation(summary = "Deactivate user account (Admin only)")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        adminUserService.updateUserStatus(userId, false);
        return ResponseEntity.noContent().build();
    }
}
