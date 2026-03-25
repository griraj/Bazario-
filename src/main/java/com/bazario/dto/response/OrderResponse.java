package com.bazario.dto.response;

import com.bazario.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String trackingInfo;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrderItemResponse> items;
}
