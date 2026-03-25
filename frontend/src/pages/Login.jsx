import React, { useState, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            const user = await login({ email, password });
            if (user.role === 'VENDOR') navigate('/dashboard');
            else navigate('/');
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to login');
        }
        setLoading(false);
    };

    return (
        <div className="page-wrapper" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <div className="glass-panel animate-fade-in" style={{ maxWidth: '450px', width: '100%', padding: '2.5rem' }}>
                <h2 style={{ textAlign: 'center', marginBottom: '0.5rem', color: 'var(--primary)' }}>Welcome Back</h2>
                <p style={{ textAlign: 'center', color: 'var(--text-secondary)', marginBottom: '2rem' }}>Log in to your Bazario account</p>

                {error && <div className="badge badge-danger" style={{ display: 'block', textAlign: 'center', padding: '0.75rem', marginBottom: '1.5rem', borderRadius: '8px' }}>{error}</div>}

                <form onSubmit={handleLogin}>
                    <div className="form-group">
                        <label>Email Address</label>
                        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required placeholder="john@example.com" />
                    </div>

                    <div className="form-group" style={{ marginBottom: '2rem' }}>
                        <label>Password</label>
                        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required placeholder="••••••••" />
                    </div>

                    <button type="submit" className="btn btn-primary" style={{ width: '100%' }} disabled={loading}>
                        {loading ? 'Authenticating...' : 'Secure Login'}
                    </button>
                </form>

                <div style={{ textAlign: 'center', marginTop: '2rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                    Don't have an account? <Link to="/register" style={{ fontWeight: '600' }}>Sign up</Link>
                </div>
            </div>
        </div>
    );
};

export default Login;
