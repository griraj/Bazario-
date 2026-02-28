package com.bazario.repository;

import com.bazario.entity.Product;
import com.bazario.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByCategoryIdAndStatus(Long categoryId, ProductStatus status, Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.status = :status
              AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    Page<Product> searchByName(@Param("name") String name,
                               @Param("status") ProductStatus status,
                               Pageable pageable);

    Page<Product> findAllByStorefrontId(Long storefrontId, Pageable pageable);

    Optional<Product> findByIdAndStorefrontVendorId(Long productId, Long vendorId);
}
