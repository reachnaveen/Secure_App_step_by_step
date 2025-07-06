import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import ProductList from './components/ProductList';
import ProductForm from './components/ProductForm';
import OrderList from './components/OrderList';
import OrderForm from './components/OrderForm';
import Navbar from './components/Navbar';

function App() {
    const [editingProduct, setEditingProduct] = useState(null);
    const [creatingOrder, setCreatingOrder] = useState(false);

    const handleEditProduct = (product) => {
        setEditingProduct(product);
    };

    const handleProductSave = () => {
        setEditingProduct(null);
        // You might want to refetch products here if ProductList doesn't do it automatically
    };

    const handleAddOrder = () => {
        setCreatingOrder(true);
    };

    const handleOrderSave = () => {
        setCreatingOrder(false);
        // You might want to refetch orders here if OrderList doesn't do it automatically
    };

    return (
        <Router>
            <Navbar />
            <div className="container mt-4">
                <Routes>
                    <Route path="/products" element={
                        <>
                            {editingProduct ? (
                                <ProductForm product={editingProduct} onSave={handleProductSave} onCancel={() => setEditingProduct(null)} />
                            ) : (
                                <ProductList onEditProduct={handleEditProduct} />
                            )}
                        </>
                    } />
                    <Route path="/orders" element={
                        <>
                            {creatingOrder ? (
                                <OrderForm onSave={handleOrderSave} onCancel={() => setCreatingOrder(false)} />
                            ) : (
                                <OrderList onAddOrder={handleAddOrder} />
                            )}
                        </>
                    } />
                    <Route path="/" element={
                        <div>
                            <h2>Welcome to the SecureApp Order Management System</h2>
                            <p>Use the navigation bar to manage products and orders.</p>
                            <Link to="/products" className="btn btn-primary me-2">View Products</Link>
                            <Link to="/orders" className="btn btn-secondary">View Orders</Link>
                        </div>
                    } />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
