import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Package, Clock, CheckCircle, Truck, XCircle } from 'lucide-react';
import { useLocation } from 'react-router-dom';

const OrderHistory = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const location = useLocation();
    const message = location.state?.message;

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const res = await api.get('/orders/customer');
                setOrders(res.data);
            } catch (err) {
                console.error("Failed to fetch orders", err);
            }
            setLoading(false);
        };
        fetchOrders();
    }, []);

    const getStatusIcon = (status) => {
        switch (status) {
            case 'PENDING': return <Clock size={20} className="text-warning" />;
            case 'PROCESSING': return <Package size={20} className="text-info" />;
            case 'SHIPPED': return <Truck size={20} className="text-primary" />;
            case 'DELIVERED': return <CheckCircle size={20} className="text-success" />;
            case 'CANCELLED': return <XCircle size={20} className="text-danger" />;
            default: return <Package size={20} />;
        }
    };

    const getStatusBadge = (status) => {
        switch (status) {
            case 'PENDING': return 'badge-warning';
            case 'PROCESSING': return 'badge-info';
            case 'SHIPPED': return 'badge-primary';
            case 'DELIVERED': return 'badge-success';
            case 'CANCELLED': return 'badge-danger';
            default: return 'badge-info';
        }
    };

    if (loading) return <div className="page-wrapper"><div className="loader"></div></div>;

    return (
        <div className="page-wrapper container animate-fade-in">
            <h1 style={{ fontSize: '2.5rem', fontWeight: '700', color: 'var(--primary)', marginBottom: '1rem' }}>Order History</h1>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '3rem' }}>Track and review your past purchases on Bazario.</p>

            {message && (
                <div className="badge badge-success" style={{ padding: '1rem', fontSize: '1rem', marginBottom: '2rem', display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <CheckCircle /> {message}
                </div>
            )}

            {orders.length === 0 ? (
                <div className="glass-panel" style={{ textAlign: 'center', padding: '4rem 2rem' }}>
                    <Package size={48} style={{ opacity: 0.2, marginBottom: '1rem' }} />
                    <h3 style={{ color: 'var(--text-secondary)' }}>You haven't placed any orders yet.</h3>
                </div>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
                    {orders.map(order => (
                        <div key={order.id} className="glass-panel" style={{ padding: '0', overflow: 'hidden' }}>
                            <div style={{ background: 'rgba(255,255,255,0.02)', padding: '1.5rem', borderBottom: '1px solid var(--border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
                                <div>
                                    <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '0.25rem' }}>Order #{order.id}</div>
                                    <div style={{ fontWeight: '600' }}>Placed on {new Date(order.orderDate).toLocaleDateString()}</div>
                                </div>
                                <div>
                                    <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '0.25rem' }}>Total Amount</div>
                                    <div style={{ fontWeight: '700', color: 'var(--primary)' }}>${order.totalAmount.toFixed(2)}</div>
                                </div>
                                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                    {getStatusIcon(order.status)}
                                    <span className={`badge ${getStatusBadge(order.status)}`}>{order.status}</span>
                                </div>
                            </div>

                            <div style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                {order.items.map(item => (
                                    <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingBottom: '1rem', borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                                            <div style={{ width: '40px', height: '40px', background: 'rgba(0,0,0,0.3)', borderRadius: '4px' }}></div>
                                            <div>
                                                <div style={{ fontWeight: '600' }}>{item.productName}</div>
                                                <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Qty: {item.quantity} × ${item.price.toFixed(2)}</div>
                                            </div>
                                        </div>
                                        <div style={{ fontWeight: '600' }}>${(item.price * item.quantity).toFixed(2)}</div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default OrderHistory;
