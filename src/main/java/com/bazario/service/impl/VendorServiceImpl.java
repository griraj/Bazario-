package com.bazario.service.impl;

import com.bazario.dto.request.StorefrontRegisterRequest;
import com.bazario.dto.request.UpdateOrderStatusRequest;
import com.bazario.dto.response.OrderItemResponse;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.StorefrontResponse;
import com.bazario.dto.response.UserResponse;
import com.bazario.dto.response.VendorDashboardResponse;
import com.bazario.entity.OrderItem;
import com.bazario.entity.Role;
import com.bazario.entity.Storefront;
import com.bazario.entity.User;
import com.bazario.exception.ConflictException;
import com.bazario.exception.ForbiddenException;
import com.bazario.exception.ResourceNotFoundException;
import com.bazario.repository.OrderItemRepository;
import com.bazario.repository.StorefrontRepository;
import com.bazario.repository.UserRepository;
import com.bazario.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final UserRepository userRepository;
    private final StorefrontRepository storefrontRepository;
    private final OrderItemRepository orderItemRepository;

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

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderItemResponse> getVendorOrders(Long vendorId, Pageable pageable) {
        Storefront storefront = storefrontRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Storefront not registered"));

        Page<OrderItem> orderItems = orderItemRepository.findByStorefrontIdOrderByCreatedAtDesc(storefront.getId(),
                pageable);

        return PagedResponse.<OrderItemResponse>builder()
                .content(orderItems.getContent().stream().map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .storefrontId(item.getStorefront().getId())
                        .priceAtTime(item.getPriceAtTime())
                        .quantity(item.getQuantity())
                        .status(item.getStatus())
                        .createdAt(item.getCreatedAt())
                        .build()).toList())
                .pageNumber(orderItems.getNumber())
                .pageSize(orderItems.getSize())
                .totalElements(orderItems.getTotalElements())
                .totalPages(orderItems.getTotalPages())
                .last(orderItems.isLast())
                .build();
    }

    @Override
    @Transactional
    public OrderItemResponse updateOrderItemStatus(Long vendorId, Long orderItemId, UpdateOrderStatusRequest request) {
        Storefront storefront = storefrontRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Storefront not registered"));

        OrderItem orderItem = orderItemRepository.findByIdAndStorefrontId(orderItemId, storefront.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found or does not belong to vendor"));

        orderItem.setStatus(request.getStatus());
        orderItemRepository.save(orderItem);

        // Optional: Check if all items in order have same status and update parent
        // Order accordingly
        // We'll leave the parent Order status untouched for now or simply update the
        // item status
        // A distributed status management might require further updates to the parent
        // order.

        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .storefrontId(orderItem.getStorefront().getId())
                .priceAtTime(orderItem.getPriceAtTime())
                .quantity(orderItem.getQuantity())
                .status(orderItem.getStatus())
                .createdAt(orderItem.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VendorDashboardResponse getDashboardAnalytics(Long vendorId) {
        Storefront storefront = storefrontRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Storefront not registered"));

        Long totalOrders = orderItemRepository.countByStorefrontId(storefront.getId());
        if (totalOrders == null)
            totalOrders = 0L;

        Double totalRevenue = orderItemRepository.sumRevenueByStorefrontId(storefront.getId());
        if (totalRevenue == null)
            totalRevenue = 0.0;

        return VendorDashboardResponse.builder()
                .storefrontId(storefront.getId())
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .build();
    }
}
