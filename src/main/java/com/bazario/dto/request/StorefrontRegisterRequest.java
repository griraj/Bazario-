package com.bazario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StorefrontRegisterRequest {

    @NotBlank(message = "Storefront name is required")
    @Size(min = 3, max = 255, message = "Storefront name must be between 3 and 255 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
}
