import React, { createContext, useState, useEffect } from 'react';
import api from '../services/api';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // Check auth status on mount
    useEffect(() => {
        const checkAuth = async () => {
            const token = localStorage.getItem('token');
            if (token) {
                try {
                    // Verify token by trying to fetch profile
                    const res = await api.get('/users/profile');
                    setUser(res.data);
                    // Ensure role is correctly synced locally
                    localStorage.setItem('role', res.data.role);
                } catch (error) {
                    console.error("Token verification failed", error);
                    logout();
                }
            }
            setLoading(false);
        };
        checkAuth();
    }, []);

    const login = async (credentials) => {
        const res = await api.post('/auth/login', credentials);
        const { token, type, ...userData } = res.data;

        // Spring Boot returns JWT token
        localStorage.setItem('token', token);
        localStorage.setItem('role', userData.role);
        setUser(userData);
        return userData;
    };

    const registerCustomer = async (data) => {
        await api.post('/auth/register/customer', data);
    };

    const registerVendor = async (data) => {
        await api.post('/auth/register/vendor', data);
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        setUser(null);
        window.location.href = '/login';
    };

    return (
        <AuthContext.Provider value={{ user, loading, login, registerCustomer, registerVendor, logout }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};
