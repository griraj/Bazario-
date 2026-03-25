import React, { useState, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Register = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        fullName: '',
        phoneNumber: '',
        address: '',
        role: 'CUSTOMER',
        storeName: '',
        storeDescription: ''
    });

    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { registerCustomer, registerVendor } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            if (formData.role === 'CUSTOMER') {
                await registerCustomer({
                    email: formData.email,
                    password: formData.password,
                    fullName: formData.fullName,
                    phoneNumber: formData.phoneNumber,
                    address: formData.address
                });
            } else {
                await registerVendor({
                    email: formData.email,
                    password: formData.password,
                    fullName: formData.fullName,
                    phoneNumber: formData.phoneNumber,
                    storeName: formData.storeName,
                    storeDescription: formData.storeDescription
                });
            }
            navigate('/login');
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed');
        }
        setLoading(false);
    };

    return (
        <div className="page-wrapper" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '5rem 2rem' }}>
            <div className="glass-panel animate-fade-in" style={{ maxWidth: '600px', width: '100%', padding: '2.5rem' }}>
                <h2 style={{ textAlign: 'center', marginBottom: '0.5rem', color: 'var(--primary)' }}>Create an Account</h2>
                <p style={{ textAlign: 'center', color: 'var(--text-secondary)', marginBottom: '2rem' }}>Join the Bazario premium marketplace</p>

                {error && <div className="badge badge-danger" style={{ display: 'block', textAlign: 'center', padding: '0.75rem', marginBottom: '1.5rem', borderRadius: '8px' }}>{error}</div>}

                <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem' }}>
                    <button
                        className={`btn ${formData.role === 'CUSTOMER' ? 'btn-primary' : 'btn-secondary'}`}
                        style={{ flex: 1 }}
                        onClick={() => setFormData({ ...formData, role: 'CUSTOMER' })}
                    >
                        I'm a Customer
                    </button>
                    <button
                        className={`btn ${formData.role === 'VENDOR' ? 'btn-primary' : 'btn-secondary'}`}
                        style={{ flex: 1 }}
                        onClick={() => setFormData({ ...formData, role: 'VENDOR' })}
                    >
                        I'm a Vendor
                    </button>
                </div>

                <form onSubmit={handleSubmit}>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
                        <div className="form-group" style={{ marginBottom: '0' }}>
                            <label>Full Name</label>
                            <input type="text" name="fullName" value={formData.fullName} onChange={handleInputChange} required placeholder="John Doe" />
                        </div>
                        <div className="form-group" style={{ marginBottom: '0' }}>
                            <label>Phone Number</label>
                            <input type="tel" name="phoneNumber" value={formData.phoneNumber} onChange={handleInputChange} required placeholder="+1 234 567 890" />
                        </div>
                    </div>

                    <div className="form-group" style={{ marginTop: '1.5rem' }}>
                        <label>Email Address</label>
                        <input type="email" name="email" value={formData.email} onChange={handleInputChange} required placeholder="john@example.com" />
                    </div>

                    <div className="form-group">
                        <label>Password</label>
                        <input type="password" name="password" value={formData.password} onChange={handleInputChange} required placeholder="••••••••" />
                    </div>

                    {formData.role === 'CUSTOMER' ? (
                        <div className="form-group">
                            <label>Shipping Address</label>
                            <textarea name="address" value={formData.address} onChange={handleInputChange} required placeholder="123 Main St, City, Country" rows="3" />
                        </div>
                    ) : (
                        <>
                            <div className="form-group">
                                <label>Store Name</label>
                                <input type="text" name="storeName" value={formData.storeName} onChange={handleInputChange} required placeholder="Premium Electronics" />
                            </div>
                            <div className="form-group">
                                <label>Store Description</label>
                                <textarea name="storeDescription" value={formData.storeDescription} onChange={handleInputChange} required placeholder="We sell the best electronics..." rows="3" />
                            </div>
                        </>
                    )}

                    <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} disabled={loading}>
                        {loading ? 'Creating account...' : 'Create Account'}
                    </button>
                </form>

                <div style={{ textAlign: 'center', marginTop: '2rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                    Already have an account? <Link to="/login" style={{ fontWeight: '600' }}>Login</Link>
                </div>
            </div>
        </div>
    );
};

export default Register;
