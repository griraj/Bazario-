package com.bazario.service;

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
import com.bazario.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private StorefrontRepository storefrontRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private User vendor;
    private Storefront storefront;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        vendor = User.builder().id(1L).email("vendor@test.com")
                .role(Role.ROLE_VENDOR).firstName("V").lastName("V").build();

        storefront = Storefront.builder().id(10L).vendor(vendor).name("Test Store").build();

        category = Category.builder().id(5L).name("Electronics").slug("electronics").build();

        product = Product.builder()
                .id(100L)
                .storefront(storefront)
                .category(category)
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .stockQuantity(50)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("US-04: createProduct should return product when vendor has a storefront")
    void createProduct_ShouldReturnProduct_WhenVendorHasStorefront() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Laptop");
        request.setPrice(new BigDecimal("999.99"));
        request.setStockQuantity(50);
        request.setCategoryId(5L);

        when(storefrontRepository.findByVendorId(1L)).thenReturn(Optional.of(storefront));
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.createProduct(1L, request);

        assertThat(response.getName()).isEqualTo("Laptop");
        assertThat(response.getPrice()).isEqualByComparingTo("999.99");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("US-04: createProduct should throw ForbiddenException when vendor has no storefront")
    void createProduct_ShouldThrowForbidden_WhenNoStorefront() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Laptop");
        request.setPrice(new BigDecimal("999.99"));
        request.setCategoryId(5L);

        when(storefrontRepository.findByVendorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(1L, request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("US-05: getProductsByCategory should return paged results")
    void getProductsByCategory_ShouldReturnPagedResults() {
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);
        when(categoryRepository.existsById(5L)).thenReturn(true);
        when(productRepository.findAllByCategoryIdAndStatus(eq(5L), eq(ProductStatus.ACTIVE), any()))
                .thenReturn(productPage);

        PagedResponse<ProductResponse> response = productService.getProductsByCategory(5L, 0, 20);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("US-06: searchProducts should return matching products")
    void searchProducts_ShouldReturnMatchingProducts() {
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);
        when(productRepository.searchByName(eq("Laptop"), eq(ProductStatus.ACTIVE), any()))
                .thenReturn(productPage);

        PagedResponse<ProductResponse> response = productService.searchProducts("Laptop", 0, 20);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("US-08: updateProduct should update fields for owning vendor")
    void updateProduct_ShouldUpdateFields_WhenVendorOwnsProduct() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setPrice(new BigDecimal("799.99"));
        request.setStockQuantity(30);

        when(productRepository.findByIdAndStorefrontVendorId(100L, 1L))
                .thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.updateProduct(1L, 100L, request);

        verify(productRepository).save(argThat(p ->
                p.getPrice().compareTo(new BigDecimal("799.99")) == 0 &&
                p.getStockQuantity() == 30));
    }

    @Test
    @DisplayName("US-08: updateProduct should throw ResourceNotFoundException for another vendor's product")
    void updateProduct_ShouldThrowNotFound_WhenProductDoesNotBelongToVendor() {
        when(productRepository.findByIdAndStorefrontVendorId(100L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(99L, 100L, new UpdateProductRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
