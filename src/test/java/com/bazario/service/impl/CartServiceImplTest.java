package com.bazario.service.impl;

import com.bazario.dto.request.AddCartItemRequest;
import com.bazario.dto.request.UpdateCartItemRequest;
import com.bazario.dto.response.CartResponse;
import com.bazario.entity.Cart;
import com.bazario.entity.CartItem;
import com.bazario.entity.Product;
import com.bazario.entity.User;
import com.bazario.exception.ResourceNotFoundException;
import com.bazario.repository.CartItemRepository;
import com.bazario.repository.CartRepository;
import com.bazario.repository.ProductRepository;
import com.bazario.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User mockUser;
    private Product mockProduct;
    private Cart mockCart;
    private AddCartItemRequest addRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        mockProduct = new Product();
        mockProduct.setId(10L);
        mockProduct.setPrice(new BigDecimal("99.99"));
        mockProduct.setStockQuantity(10);

        mockCart = new Cart();
        mockCart.setId(20L);
        mockCart.setUser(mockUser);
        mockCart.setItems(new ArrayList<>());

        addRequest = new AddCartItemRequest();
        addRequest.setProductId(10L);
        addRequest.setQuantity(2);
    }

    @Test
    void testAddToCart_NewCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(10L)).thenReturn(Optional.of(mockProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        CartResponse response = cartService.addCartItem(1L, addRequest);

        assertNotNull(response);
        verify(cartRepository, atLeastOnce()).save(any(Cart.class));
    }

    @Test
    void testAddToCart_ProductNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(mockCart));
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.addCartItem(1L, addRequest));
    }

    @Test
    void testUpdateCartItem() {
        CartItem existingItem = new CartItem();
        existingItem.setId(30L);
        existingItem.setProduct(mockProduct);
        existingItem.setQuantity(1);
        existingItem.setCart(mockCart);
        mockCart.getItems().add(existingItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(mockCart));
        when(cartItemRepository.findById(30L)).thenReturn(Optional.of(existingItem));

        UpdateCartItemRequest updateRequest = new UpdateCartItemRequest();
        updateRequest.setQuantity(5);

        CartResponse response = cartService.updateCartItem(1L, 30L, updateRequest);

        assertEquals(5, existingItem.getQuantity());
        verify(cartItemRepository).save(existingItem);
    }
}
