package com.bazario.service;

import com.bazario.dto.response.OrderResponse;
import com.bazario.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse checkout(Long userId);

    PagedResponse<OrderResponse> getCustomerOrders(Long userId, Pageable pageable);

    OrderResponse getOrderStatus(Long userId, Long orderId);
}
