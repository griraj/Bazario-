package com.bazario.controller;

import com.bazario.dto.request.CreateProductRequest;
import com.bazario.dto.request.UpdateProductRequest;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.ProductResponse;
import com.bazario.entity.User;
import com.bazario.service.ProductService;
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
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management (US-04, US-05, US-06, US-08)")
public class ProductController {

    private final ProductService productService;

    /**
     * US-04 – Vendor adds a new product listing.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Create a new product listing")
    public ProductResponse createProduct(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(currentUser.getId(), request);
    }

    /**
     * US-05 – Browse products by category.
     */
    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "Browse products by category")
    public PagedResponse<ProductResponse> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return productService.getProductsByCategory(categoryId, page, size);
    }

    /**
     * US-06 – Search products by name.
     */
    @GetMapping("/search")
    @Operation(summary = "Search products by name")
    public PagedResponse<ProductResponse> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return productService.searchProducts(q, page, size);
    }

    /**
     * Get a single product by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ProductResponse getById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    /**
     * US-08 – Vendor edits their product details.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Update a product listing (vendor only)")
    public ProductResponse updateProduct(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return productService.updateProduct(currentUser.getId(), id, request);
    }
}
