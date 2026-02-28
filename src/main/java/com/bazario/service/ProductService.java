package com.bazario.service;

import com.bazario.dto.request.CreateProductRequest;
import com.bazario.dto.request.UpdateProductRequest;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.ProductResponse;

public interface ProductService {

    ProductResponse createProduct(Long vendorId, CreateProductRequest request);

    ProductResponse updateProduct(Long vendorId, Long productId, UpdateProductRequest request);

    PagedResponse<ProductResponse> getProductsByCategory(Long categoryId, int page, int size);

    PagedResponse<ProductResponse> searchProducts(String name, int page, int size);

    ProductResponse getProductById(Long productId);
}
