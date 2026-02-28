package com.bazario.service.impl;

import com.bazario.dto.request.StorefrontRegisterRequest;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.StorefrontResponse;
import com.bazario.dto.response.UserResponse;
import com.bazario.entity.Role;
import com.bazario.entity.Storefront;
import com.bazario.entity.User;
import com.bazario.exception.ConflictException;
import com.bazario.exception.ForbiddenException;
import com.bazario.exception.ResourceNotFoundException;
import com.bazario.repository.StorefrontRepository;
import com.bazario.repository.UserRepository;
import com.bazario.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final UserRepository userRepository;
    private final StorefrontRepository storefrontRepository;

    @Override
    @Transactional
    public StorefrontResponse registerStorefront(Long vendorId, StorefrontRegisterRequest request) {
        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", vendorId));

        if (vendor.getRole() != Role.ROLE_VENDOR) {
            throw new ForbiddenException("Only vendor accounts may register a storefront. " +
                    "Please contact support to upgrade your account.");
        }

        if (storefrontRepository.existsByVendorId(vendorId)) {
            throw new ConflictException("A storefront is already registered for this vendor account");
        }

        if (storefrontRepository.existsByName(request.getName())) {
            throw new ConflictException("Storefront name '" + request.getName() + "' is already taken");
        }

        Storefront storefront = Storefront.builder()
                .vendor(vendor)
                .name(request.getName().strip())
                .description(request.getDescription())
                .build();

        return StorefrontResponse.from(storefrontRepository.save(storefront));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllVendors(int page, int size) {
        Page<User> vendors = userRepository.findAllByRole(
                Role.ROLE_VENDOR,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        return PagedResponse.from(vendors.map(UserResponse::from));
    }
}
