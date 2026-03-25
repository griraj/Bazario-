package com.bazario.controller;

import com.bazario.dto.request.StorefrontRegisterRequest;
import com.bazario.dto.request.UpdateOrderStatusRequest;
import com.bazario.dto.response.OrderItemResponse;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.StorefrontResponse;
import com.bazario.dto.response.UserResponse;
import com.bazario.dto.response.VendorDashboardResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

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

    /**
     * US-13 – Vendor views incoming orders.
     */
    @GetMapping("/orders")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Get all orders for vendor's storefront")
    public PagedResponse<OrderItemResponse> getVendorOrders(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return vendorService.getVendorOrders(currentUser.getId(), pageable);
    }

    /**
     * US-13 – Vendor updates order status.
     */
    @PutMapping("/orders/{orderItemId}/status")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Update status of an order item")
    public OrderItemResponse updateOrderItemStatus(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long orderItemId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return vendorService.updateOrderItemStatus(currentUser.getId(), orderItemId, request);
    }

    /**
     * US-14 – Vendor Dashboard Analytics.
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Get vendor dashboard analytics")
    public VendorDashboardResponse getDashboardAnalytics(@AuthenticationPrincipal User currentUser) {
        return vendorService.getDashboardAnalytics(currentUser.getId());
    }
}
