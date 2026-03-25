import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { CartContext } from '../context/CartContext';
import { Store, ShoppingBag, UserCircle, LogOut, Package } from 'lucide-react';

const Navbar = () => {
    const { user, logout } = useContext(AuthContext);
    const { cartCount } = useContext(CartContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="container nav-container">
                <Link to="/" className="nav-logo">
                    <Store size={28} color="var(--primary)" />
                    Bazario
                </Link>

                <div className="nav-links">
                    <Link to="/" className="nav-link">Marketplace</Link>

                    {!user ? (
                        <>
                            <Link to="/login" className="btn btn-secondary">Login</Link>
                            <Link to="/register" className="btn btn-primary">Sign Up</Link>
                        </>
                    ) : (
                        <>
                            {user.role === 'CUSTOMER' && (
                                <>
                                    <Link to="/cart" className="nav-link" style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                                        <ShoppingBag size={20} />
                                        Cart
                                        {cartCount > 0 && <span className="badge badge-success" style={{ marginLeft: '4px' }}>{cartCount}</span>}
                                    </Link>
                                    <Link to="/orders" className="nav-link">Orders</Link>
                                </>
                            )}

                            {user.role === 'VENDOR' && (
                                <>
                                    <Link to="/dashboard" className="nav-link" style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                                        <Package size={20} /> Dashboard
                                    </Link>
                                </>
                            )}

                            <div style={{ display: 'flex', alignItems: 'center', gap: '15px', marginLeft: '1rem', paddingLeft: '1rem', borderLeft: '1px solid var(--border)' }}>
                                <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                                    <UserCircle size={18} style={{ verticalAlign: 'middle', marginRight: '5px' }} />
                                    {user.fullName}
                                </span>
                                <button onClick={handleLogout} className="btn btn-danger" style={{ padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}>
                                    <LogOut size={16} /> Logout
                                </button>
                            </div>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
