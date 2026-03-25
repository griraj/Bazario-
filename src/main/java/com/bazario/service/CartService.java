package com.bazario.service;

import com.bazario.dto.request.AddCartItemRequest;
import com.bazario.dto.request.UpdateCartItemRequest;
import com.bazario.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(Long userId);

    CartResponse addCartItem(Long userId, AddCartItemRequest request);

    CartResponse updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeCartItem(Long userId, Long cartItemId);

    void clearCart(Long userId);
}
