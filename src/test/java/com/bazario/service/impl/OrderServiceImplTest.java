package com.bazario.service.impl;

import com.bazario.dto.response.OrderResponse;
import com.bazario.entity.Cart;
import com.bazario.entity.CartItem;
import com.bazario.entity.Order;
import com.bazario.entity.Product;
import com.bazario.entity.Storefront;
import com.bazario.entity.User;
import com.bazario.exception.ConflictException;
import com.bazario.repository.CartRepository;
import com.bazario.repository.OrderItemRepository;
import com.bazario.repository.OrderRepository;
import com.bazario.repository.ProductRepository;
import com.bazario.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User mockUser;
    private Cart mockCart;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        mockProduct = new Product();
        mockProduct.setId(10L);
        mockProduct.setPrice(new BigDecimal("20.00"));
        mockProduct.setStockQuantity(50);

        Storefront sf = new Storefront();
        sf.setId(100L);
        mockProduct.setStorefront(sf);

        CartItem item = new CartItem();
        item.setId(200L);
        item.setProduct(mockProduct);
        item.setQuantity(2);

        mockCart = new Cart();
        mockCart.setId(20L);
        mockCart.setUser(mockUser);
        mockCart.setItems(Collections.singletonList(item));
        item.setCart(mockCart);
    }

    @Test
    void testCheckoutCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(mockCart));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(500L);
            return o;
        });

        OrderResponse response = orderService.checkout(1L);

        assertNotNull(response);
        assertEquals(500L, response.getId());
        assertEquals(new BigDecimal("40.00"), response.getTotalAmount());
        assertEquals(48, mockProduct.getStockQuantity());
        
        verify(productRepository).save(mockProduct);
        verify(cartService).clearCart(1L);
    }

    @Test
    void testCheckoutCart_InsufficientStock() {
        mockProduct.setStockQuantity(1);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(mockCart));

        assertThrows(ConflictException.class, () -> orderService.checkout(1L));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCheckoutCart_EmptyCart() {
        mockCart.setItems(Collections.emptyList());
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(mockCart));

        assertThrows(ConflictException.class, () -> orderService.checkout(1L));
    }
}
