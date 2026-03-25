package com.bazario.service;

import com.bazario.dto.request.StorefrontRegisterRequest;
import com.bazario.dto.request.UpdateOrderStatusRequest;
import com.bazario.dto.response.OrderItemResponse;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.StorefrontResponse;
import com.bazario.dto.response.UserResponse;
import com.bazario.dto.response.VendorDashboardResponse;
import org.springframework.data.domain.Pageable;

public interface VendorService {

    StorefrontResponse registerStorefront(Long vendorId, StorefrontRegisterRequest request);

    PagedResponse<UserResponse> getAllVendors(int page, int size);

    PagedResponse<OrderItemResponse> getVendorOrders(Long vendorId, Pageable pageable);

    OrderItemResponse updateOrderItemStatus(Long vendorId, Long orderItemId, UpdateOrderStatusRequest request);

    VendorDashboardResponse getDashboardAnalytics(Long vendorId);
}
