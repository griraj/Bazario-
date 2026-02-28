package com.bazario.dto.response;

import com.bazario.entity.Storefront;
import com.bazario.entity.StorefrontStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class StorefrontResponse {

    private Long id;
    private Long vendorId;
    private String vendorEmail;
    private String name;
    private String description;
    private StorefrontStatus status;
    private Instant createdAt;

    public static StorefrontResponse from(Storefront sf) {
        return StorefrontResponse.builder()
                .id(sf.getId())
                .vendorId(sf.getVendor().getId())
                .vendorEmail(sf.getVendor().getEmail())
                .name(sf.getName())
                .description(sf.getDescription())
                .status(sf.getStatus())
                .createdAt(sf.getCreatedAt())
                .build();
    }
}
