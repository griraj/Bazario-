import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { DollarSign, ShoppingBag, Plus } from 'lucide-react';

const Dashboard = () => {
    const [stats, setStats] = useState(null);
    const [vendorOrders, setVendorOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    // Product Form State
    const [showAddProduct, setShowAddProduct] = useState(false);
    const [productForm, setProductForm] = useState({ name: '', description: '', price: '', stockQuantity: '', categoryId: 1 });
    const [productLoading, setProductLoading] = useState(false);

    useEffect(() => {
        fetchDashboardData();
    }, []);

    const fetchDashboardData = async () => {
        setLoading(true);
        try {
            const statsRes = await api.get('/vendors/analytics');
            setStats(statsRes.data);

            const ordersRes = await api.get('/orders/vendor');
            setVendorOrders(ordersRes.data);
        } catch (err) {
            console.error("Failed to load dashboard data", err);
        }
        setLoading(false);
    };

    const handleAddProduct = async (e) => {
        e.preventDefault();
        setProductLoading(true);
        try {
            await api.post('/products', productForm);
            alert("Product added successfully!");
            setShowAddProduct(false);
            setProductForm({ name: '', description: '', price: '', stockQuantity: '', categoryId: 1 });
            // In a real app we'd fetch vendor's products here to update the list
        } catch (err) {
            alert("Failed to add product");
        }
        setProductLoading(false);
    };

    const updateOrderStatus = async (orderId, status) => {
        try {
            await api.put(`/orders/${orderId}/status`, null, { params: { status } });
            await fetchDashboardData();
        } catch (err) {
            alert("Failed to update status");
        }
    };

    if (loading) return <div className="page-wrapper"><div className="loader"></div></div>;

    return (
        <div className="page-wrapper container animate-fade-in">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '3rem' }}>
                <div>
                    <h1 style={{ fontSize: '2.5rem', fontWeight: '700', color: 'var(--primary)', marginBottom: '0.5rem' }}>Vendor Area</h1>
                    <p style={{ color: 'var(--text-secondary)' }}>Manage your digital storefront and fulfill orders.</p>
                </div>
                <button className="btn btn-primary" onClick={() => setShowAddProduct(!showAddProduct)}>
                    <Plus size={20} /> New Product
                </button>
            </div>

            {showAddProduct && (
                <div className="glass-panel animate-fade-in" style={{ padding: '2rem', marginBottom: '3rem', border: '1px solid var(--primary)' }}>
                    <h3 style={{ marginBottom: '1.5rem', color: 'var(--primary)' }}>Create New Listing</h3>
                    <form onSubmit={handleAddProduct} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
                        <div className="form-group" style={{ marginBottom: 0 }}>
                            <label>Product Name</label>
                            <input type="text" value={productForm.name} onChange={e => setProductForm({ ...productForm, name: e.target.value })} required />
                        </div>
                        <div className="form-group" style={{ marginBottom: 0 }}>
                            <label>Category ID (Numeric)</label>
                            <input type="number" value={productForm.categoryId} onChange={e => setProductForm({ ...productForm, categoryId: e.target.value })} required min="1" />
                        </div>
                        <div className="form-group" style={{ gridColumn: '1 / -1', marginBottom: 0 }}>
                            <label>Description</label>
                            <textarea value={productForm.description} onChange={e => setProductForm({ ...productForm, description: e.target.value })} required rows="3" />
                        </div>
                        <div className="form-group" style={{ marginBottom: 0 }}>
                            <label>Price ($)</label>
                            <input type="number" step="0.01" value={productForm.price} onChange={e => setProductForm({ ...productForm, price: e.target.value })} required min="0.01" />
                        </div>
                        <div className="form-group" style={{ marginBottom: 0 }}>
                            <label>Stock Quantity</label>
                            <input type="number" value={productForm.stockQuantity} onChange={e => setProductForm({ ...productForm, stockQuantity: e.target.value })} required min="1" />
                        </div>
                        <div style={{ gridColumn: '1 / -1', display: 'flex', justifyContent: 'flex-end', gap: '1rem', marginTop: '1rem' }}>
                            <button type="button" className="btn btn-secondary" onClick={() => setShowAddProduct(false)}>Cancel</button>
                            <button type="submit" className="btn btn-primary" disabled={productLoading}>{productLoading ? 'Saving...' : 'Publish Product'}</button>
                        </div>
                    </form>
                </div>
            )}

            {stats && (
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', marginBottom: '3rem' }}>
                    <div className="glass-panel" style={{ padding: '2rem', display: 'flex', alignItems: 'center', gap: '2rem' }}>
                        <div style={{ background: 'rgba(3, 218, 198, 0.15)', padding: '1.5rem', borderRadius: '50%', color: 'var(--success)' }}>
                            <DollarSign size={40} />
                        </div>
                        <div>
                            <div style={{ color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', fontSize: '0.9rem', marginBottom: '0.5rem' }}>Total Revenue</div>
                            <div style={{ fontSize: '2.5rem', fontWeight: '700', color: 'var(--text-primary)' }}>${stats.totalRevenue.toFixed(2)}</div>
                        </div>
                    </div>

                    <div className="glass-panel" style={{ padding: '2rem', display: 'flex', alignItems: 'center', gap: '2rem' }}>
                        <div style={{ background: 'rgba(102, 252, 241, 0.15)', padding: '1.5rem', borderRadius: '50%', color: 'var(--primary)' }}>
                            <ShoppingBag size={40} />
                        </div>
                        <div>
                            <div style={{ color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', fontSize: '0.9rem', marginBottom: '0.5rem' }}>Total Orders</div>
                            <div style={{ fontSize: '2.5rem', fontWeight: '700', color: 'var(--text-primary)' }}>{stats.totalOrders}</div>
                        </div>
                    </div>
                </div>
            )}

            <h2 style={{ fontSize: '1.75rem', fontWeight: '600', marginBottom: '1.5rem' }}>Incoming Orders</h2>
            {vendorOrders.length === 0 ? (
                <div className="glass-panel" style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-secondary)' }}>
                    No incoming orders to fulfill currently.
                </div>
            ) : (
                <div style={{ display: 'grid', gap: '1.5rem' }}>
                    {vendorOrders.map(order => (
                        <div key={order.id} className="glass-panel" style={{ padding: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
                            <div>
                                <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '0.25rem' }}>Order #{order.id}</div>
                                <div style={{ fontWeight: '600' }}>{new Date(order.orderDate).toLocaleString()}</div>
                            </div>

                            <div>
                                <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '0.25rem' }}>Contains</div>
                                <div>{order.items.length} items</div>
                            </div>

                            <div>
                                <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '0.25rem' }}>Status</div>
                                <span className="badge badge-warning">{order.status}</span>
                            </div>

                            <div style={{ display: 'flex', gap: '0.5rem' }}>
                                {order.status === 'PENDING' && (
                                    <button onClick={() => updateOrderStatus(order.id, 'PROCESSING')} className="btn btn-secondary" style={{ padding: '0.5rem 1rem' }}>Process</button>
                                )}
                                {order.status === 'PROCESSING' && (
                                    <button onClick={() => updateOrderStatus(order.id, 'SHIPPED')} className="btn btn-primary" style={{ padding: '0.5rem 1rem' }}>Ship</button>
                                )}
                                {order.status === 'SHIPPED' && (
                                    <button onClick={() => updateOrderStatus(order.id, 'DELIVERED')} className="btn btn-primary" style={{ padding: '0.5rem 1rem', background: 'var(--success)' }}>Mark Delivered</button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Dashboard;
