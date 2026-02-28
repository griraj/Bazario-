package com.bazario.controller;

import com.bazario.dto.request.StorefrontRegisterRequest;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.StorefrontResponse;
import com.bazario.dto.response.UserResponse;
import com.bazario.entity.User;
import com.bazario.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendors", description = "Vendor storefront management (US-03, US-07)")
public class VendorController {

    private final VendorService vendorService;

    /**
     * US-03 – Vendor registers their storefront.
     */
    @PostMapping("/storefront")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Register a vendor storefront")
    public StorefrontResponse registerStorefront(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody StorefrontRegisterRequest request) {
        return vendorService.registerStorefront(currentUser.getId(), request);
    }

    /**
     * US-07 – Admin views all registered vendors.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all registered vendors (admin only)")
    public PagedResponse<UserResponse> getAllVendors(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return vendorService.getAllVendors(page, size);
    }
}
