import React, { useContext, useState } from 'react';
import { CartContext } from '../context/CartContext';
import { Trash2, Plus, Minus, CreditCard } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const CartPage = () => {
    const { cart, updateQuantity, removeItem, clearCart, fetchCart } = useContext(CartContext);
    const [checkoutLoading, setCheckoutLoading] = useState(false);
    const navigate = useNavigate();

    const handleCheckout = async () => {
        setCheckoutLoading(true);
        try {
            // POST mapping for checkout in backend: /orders/checkout
            await api.post('/orders/checkout');
            await fetchCart(); // Re-sync empty cart
            navigate('/orders', { state: { message: "Order placed successfully!" } });
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to checkout');
        }
        setCheckoutLoading(false);
    };

    if (!cart) return <div className="page-wrapper"><div className="loader"></div></div>;

    return (
        <div className="page-wrapper container animate-fade-in">
            <h1 style={{ fontSize: '2.5rem', fontWeight: '700', color: 'var(--primary)', marginBottom: '2rem' }}>Your Cart</h1>

            {(!cart.items || cart.items.length === 0) ? (
                <div className="glass-panel" style={{ textAlign: 'center', padding: '4rem 2rem' }}>
                    <h3 style={{ color: 'var(--text-secondary)' }}>Your shopping cart is empty.</h3>
                    <button onClick={() => navigate('/')} className="btn btn-primary" style={{ marginTop: '1.5rem' }}>
                        Continue Shopping
                    </button>
                </div>
            ) : (
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 350px', gap: '2rem', alignItems: 'start' }}>

                    {/* Cart Items List */}
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                        {cart.items.map(item => (
                            <div key={item.id} className="glass-panel" style={{ display: 'flex', alignItems: 'center', padding: '1.5rem', gap: '1.5rem' }}>
                                <div style={{ width: '80px', height: '80px', background: 'rgba(0,0,0,0.3)', borderRadius: '8px', flexShrink: 0 }}></div>

                                <div style={{ flex: 1 }}>
                                    <h4 style={{ fontSize: '1.1rem', fontWeight: '600' }}>{item.productName}</h4>
                                    <div style={{ color: 'var(--primary)', fontWeight: '700', marginTop: '0.25rem' }}>${item.price.toFixed(2)}</div>
                                </div>

                                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', background: 'rgba(0,0,0,0.2)', padding: '0.5rem', borderRadius: '8px' }}>
                                    <button
                                        onClick={() => updateQuantity(item.id, item.quantity - 1)}
                                        style={{ background: 'none', border: 'none', color: 'var(--text-primary)', cursor: 'pointer', padding: '0.25rem' }}
                                        disabled={item.quantity <= 1}
                                    ><Minus size={16} /></button>
                                    <span style={{ fontWeight: '600', width: '20px', textAlign: 'center' }}>{item.quantity}</span>
                                    <button
                                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                                        style={{ background: 'none', border: 'none', color: 'var(--text-primary)', cursor: 'pointer', padding: '0.25rem' }}
                                    ><Plus size={16} /></button>
                                </div>

                                <div style={{ fontWeight: '700', minWidth: '80px', textAlign: 'right' }}>
                                    ${(item.price * item.quantity).toFixed(2)}
                                </div>

                                <button
                                    onClick={() => removeItem(item.id)}
                                    style={{ background: 'none', border: 'none', color: 'var(--danger)', cursor: 'pointer', padding: '0.5rem' }}
                                    title="Remove Item"
                                ><Trash2 size={20} /></button>
                            </div>
                        ))}

                        <button onClick={clearCart} className="btn btn-danger" style={{ alignSelf: 'flex-start', marginTop: '1rem' }}>
                            Clear Cart
                        </button>
                    </div>

                    {/* Order Summary Pane */}
                    <div className="glass-panel" style={{ padding: '2rem', position: 'sticky', top: '100px' }}>
                        <h3 style={{ fontSize: '1.25rem', marginBottom: '1.5rem', borderBottom: '1px solid var(--border)', paddingBottom: '0.75rem' }}>Order Summary</h3>

                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem', color: 'var(--text-secondary)' }}>
                            <span>Subtotal</span>
                            <span>${cart.totalAmount.toFixed(2)}</span>
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1.5rem', color: 'var(--text-secondary)' }}>
                            <span>Tax (Estimated)</span>
                            <span>$0.00</span>
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '2rem', fontSize: '1.25rem', fontWeight: '700', color: 'var(--primary)', borderTop: '1px solid var(--border)', paddingTop: '1rem' }}>
                            <span>Total</span>
                            <span>${cart.totalAmount.toFixed(2)}</span>
                        </div>

                        <button
                            onClick={handleCheckout}
                            className="btn btn-primary"
                            style={{ width: '100%', padding: '1rem', fontSize: '1.1rem' }}
                            disabled={checkoutLoading || cart.items.length === 0}
                        >
                            {checkoutLoading ? 'Processing...' : <><CreditCard size={20} /> Secure Checkout</>}
                        </button>
                    </div>

                </div>
            )}
        </div>
    );
};

export default CartPage;
