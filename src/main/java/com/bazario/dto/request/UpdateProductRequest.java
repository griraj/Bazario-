package com.bazario.dto.request;

import com.bazario.entity.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {

    @Size(min = 1, max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @DecimalMin(value = "0.00", inclusive = true, message = "Price must be zero or positive")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity must be zero or positive")
    private Integer stockQuantity;

    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    private Long categoryId;

    private ProductStatus status;
}
