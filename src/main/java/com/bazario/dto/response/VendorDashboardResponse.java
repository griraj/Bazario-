package com.bazario.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorDashboardResponse {
    private Long storefrontId;
    private Long totalOrders;
    private Double totalRevenue;
}
