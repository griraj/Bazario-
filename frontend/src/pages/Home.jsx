import React, { useState, useEffect, useContext } from 'react';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';
import { CartContext } from '../context/CartContext';
import { ShoppingCart } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const Home = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [addingToCart, setAddingToCart] = useState(null);
    const { user } = useContext(AuthContext);
    const { addToCart } = useContext(CartContext);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const res = await api.get('/products');
                setProducts(res.data.content); // Pageable response
            } catch (err) {
                console.error("Error fetching products", err);
            }
            setLoading(false);
        };
        fetchProducts();
    }, []);

    const handleAddToCart = async (productId) => {
        if (!user) {
            navigate('/login');
            return;
        }
        if (user.role !== 'CUSTOMER') {
            alert("Only Customers can add to cart.");
            return;
        }

        setAddingToCart(productId);
        try {
            await addToCart(productId, 1);
        } catch (err) {
            alert(err.response?.data?.message || "Failed to add to cart");
        }
        setAddingToCart(null);
    };

    if (loading) return <div className="page-wrapper" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}><div className="loader"></div></div>;

    return (
        <div className="page-wrapper container animate-fade-in">
            <div style={{ textAlign: 'center', marginBottom: '3rem' }}>
                <h1 style={{ fontSize: '3rem', fontWeight: '700', color: 'var(--primary)', marginBottom: '1rem' }}>Premium Discoveries</h1>
                <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem', maxWidth: '600px', margin: '0 auto' }}>
                    Explore the finest, hand-picked digital and physical commodities on the Bazario network.
                </p>
            </div>

            {products.length === 0 ? (
                <div style={{ textAlign: 'center', color: 'var(--text-secondary)', padding: '4rem 0' }}>
                    <h3>No products currently available.</h3>
                </div>
            ) : (
                <div className="grid-cards">
                    {products.map(product => (
                        <div key={product.id} className="glass-panel" style={{ display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
                            <div style={{ height: '220px', background: 'rgba(0,0,0,0.3)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                {/* Fallback elegant placeholder till we have images */}
                                <div style={{ fontSize: '4rem', opacity: '0.2' }}>Store</div>
                            </div>
                            <div style={{ padding: '1.5rem', flex: 1, display: 'flex', flexDirection: 'column' }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                                    <h3 style={{ fontSize: '1.25rem', fontWeight: '600' }}>{product.name}</h3>
                                    <span style={{ fontSize: '1.25rem', fontWeight: '700', color: 'var(--primary)' }}>${product.price.toFixed(2)}</span>
                                </div>
                                <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '1rem', flex: 1 }}>
                                    {product.description.length > 80 ? product.description.substring(0, 80) + '...' : product.description}
                                </p>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 'auto' }}>
                                    <span className={`badge ${product.stockQuantity > 0 ? 'badge-success' : 'badge-danger'}`}>
                                        {product.stockQuantity > 0 ? `${product.stockQuantity} In Stock` : 'Out of Stock'}
                                    </span>
                                    <button
                                        onClick={() => handleAddToCart(product.id)}
                                        className="btn btn-primary"
                                        disabled={product.stockQuantity === 0 || addingToCart === product.id}
                                        style={{ padding: '0.5rem 1rem' }}
                                    >
                                        {addingToCart === product.id ? 'Adding...' : <><ShoppingCart size={16} /> Add</>}
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Home;
