package com.bazario.repository;

import com.bazario.entity.OrderItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // For US-13 | Vendor Order Management (View incoming orders for a vendor's
    // storefront)
    Page<OrderItem> findByStorefrontIdOrderByCreatedAtDesc(Long storefrontId, Pageable pageable);

    // For US-14 | Vendor Dashboard – Sales Analytics
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.storefront.id = :storefrontId")
    Long countByStorefrontId(@Param("storefrontId") Long storefrontId);

    @Query("SELECT SUM(oi.priceAtTime * oi.quantity) FROM OrderItem oi WHERE oi.storefront.id = :storefrontId AND oi.status != 'CANCELLED'")
    Double sumRevenueByStorefrontId(@Param("storefrontId") Long storefrontId);

    Optional<OrderItem> findByIdAndStorefrontId(Long orderItemId, Long storefrontId);

    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi JOIN oi.order o WHERE o.user.id = :userId AND oi.product.id = :productId AND oi.status = 'DELIVERED'")
    boolean hasUserPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
