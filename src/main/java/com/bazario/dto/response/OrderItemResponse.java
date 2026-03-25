package com.bazario.dto.response;

import com.bazario.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long storefrontId;
    private BigDecimal priceAtTime;
    private Integer quantity;
    private OrderStatus status;
    private Instant createdAt;
}
