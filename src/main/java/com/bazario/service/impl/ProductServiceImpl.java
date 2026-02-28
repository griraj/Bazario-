package com.bazario.service.impl;

import com.bazario.dto.request.CreateProductRequest;
import com.bazario.dto.request.UpdateProductRequest;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.ProductResponse;
import com.bazario.entity.*;
import com.bazario.exception.ForbiddenException;
import com.bazario.exception.ResourceNotFoundException;
import com.bazario.repository.CategoryRepository;
import com.bazario.repository.ProductRepository;
import com.bazario.repository.StorefrontRepository;
import com.bazario.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StorefrontRepository storefrontRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(Long vendorId, CreateProductRequest request) {
        Storefront storefront = storefrontRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ForbiddenException(
                        "You must register a storefront before listing products"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        Product product = Product.builder()
                .storefront(storefront)
                .category(category)
                .name(request.getName().strip())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .sku(request.getSku())
                .status(ProductStatus.ACTIVE)
                .build();

        return ProductResponse.from(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long vendorId, Long productId, UpdateProductRequest request) {
        Product product = productRepository.findByIdAndStorefrontVendorId(productId, vendorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found or you do not have permission to edit it"));

        if (request.getName() != null) {
            product.setName(request.getName().strip());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getSku() != null) {
            product.setSku(request.getSku());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
            product.setCategory(category);
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        return ProductResponse.from(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProductsByCategory(Long categoryId, int page, int size) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", categoryId);
        }

        Page<Product> products = productRepository.findAllByCategoryIdAndStatus(
                categoryId, ProductStatus.ACTIVE,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        return PagedResponse.from(products.map(ProductResponse::from));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(String name, int page, int size) {
        Page<Product> products = productRepository.searchByName(
                name.strip(), ProductStatus.ACTIVE,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        return PagedResponse.from(products.map(ProductResponse::from));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        return ProductResponse.from(product);
    }
}
