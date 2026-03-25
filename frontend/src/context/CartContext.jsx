import React, { createContext, useState, useCallback, useEffect } from 'react';
import api from '../services/api';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
    const [cart, setCart] = useState(null);
    const [cartCount, setCartCount] = useState(0);

    const fetchCart = useCallback(async () => {
        const token = localStorage.getItem('token');
        const role = localStorage.getItem('role');

        // Only customers have carts
        if (token && role === 'CUSTOMER') {
            try {
                const res = await api.get('/carts');
                setCart(res.data);

                // Sum total quantities for the badge
                const totalItems = res.data.items.reduce((acc, item) => acc + item.quantity, 0);
                setCartCount(totalItems);
            } catch (err) {
                if (err.response && err.response.status !== 404) {
                    console.error("Error fetching cart", err);
                } else {
                    setCart({ items: [], totalAmount: 0 });
                    setCartCount(0);
                }
            }
        }
    }, []);

    useEffect(() => {
        fetchCart();
    }, [fetchCart]);

    const addToCart = async (productId, quantity = 1) => {
        await api.post('/carts/items', { productId, quantity });
        await fetchCart();
    };

    const updateQuantity = async (itemId, quantity) => {
        await api.put(`/carts/items/${itemId}`, { quantity });
        await fetchCart();
    };

    const removeItem = async (itemId) => {
        await api.delete(`/carts/items/${itemId}`);
        await fetchCart();
    };

    const clearCart = async () => {
        await api.delete('/carts');
        await fetchCart();
    };

    return (
        <CartContext.Provider value={{ cart, cartCount, fetchCart, addToCart, updateQuantity, removeItem, clearCart }}>
            {children}
        </CartContext.Provider>
    );
};
