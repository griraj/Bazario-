package com.bazario.repository;

import com.bazario.entity.Storefront;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorefrontRepository extends JpaRepository<Storefront, Long> {

    Optional<Storefront> findByVendorId(Long vendorId);

    boolean existsByName(String name);

    boolean existsByVendorId(Long vendorId);
}
