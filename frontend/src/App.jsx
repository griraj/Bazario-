import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import CartPage from './pages/CartPage';
import Dashboard from './pages/Dashboard';
import OrderHistory from './pages/OrderHistory';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';

// Auth Guard Components
const ProtectedRoute = ({ children, requireRole }) => {
  // We'll implement this inside AuthContext later, mapping down
  // simple token checks
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');

  if (!token) return <Navigate to="/login" />;
  if (requireRole && role !== requireRole) return <Navigate to="/" />;

  return children;
};

const App = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <div className="app-container">
            <Navbar />
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />

              {/* Customer Routes */}
              <Route path="/cart" element={
                <ProtectedRoute requireRole="CUSTOMER">
                  <CartPage />
                </ProtectedRoute>
              } />
              <Route path="/orders" element={
                <ProtectedRoute requireRole="CUSTOMER">
                  <OrderHistory />
                </ProtectedRoute>
              } />

              {/* Vendor Routes */}
              <Route path="/dashboard" element={
                <ProtectedRoute requireRole="VENDOR">
                  <Dashboard />
                </ProtectedRoute>
              } />
            </Routes>
          </div>
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  );
};

export default App;
