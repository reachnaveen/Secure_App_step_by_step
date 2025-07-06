import React, { useState, useEffect } from 'react';
import axios from 'axios';

const OrderForm = ({ onSave, onCancel }) => {
    const [customerName, setCustomerName] = useState('');
    const [products, setProducts] = useState([]);
    const [selectedProduct, setSelectedProduct] = useState('');
    const [quantity, setQuantity] = useState(1);
    const [orderItems, setOrderItems] = useState([]);

    useEffect(() => {
        axios.get('/api/products')
            .then(response => {
                setProducts(response.data);
                if (response.data.length > 0) {
                    setSelectedProduct(response.data[0].id);
                }
            })
            .catch(error => console.error('Error fetching products:', error));
    }, []);

    const handleAddOrderItem = () => {
        const productToAdd = products.find(p => p.id === parseInt(selectedProduct));
        if (productToAdd && quantity > 0) {
            setOrderItems(prevItems => [
                ...prevItems,
                { product: productToAdd, quantity: quantity }
            ]);
            setQuantity(1); // Reset quantity
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const newOrder = {
            customerName,
            orderItems: orderItems.map(item => ({
                product: { id: item.product.id },
                quantity: item.quantity
            }))
        };

        axios.post('/api/orders', newOrder)
            .then(() => onSave())
            .catch(error => console.error('Error creating order:', error));
    };

    return (
        <div className="card p-4 mt-4">
            <h3>Create New Order</h3>
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label htmlFor="customerName" className="form-label">Customer Name</label>
                    <input type="text" className="form-control" id="customerName" value={customerName} onChange={(e) => setCustomerName(e.target.value)} required />
                </div>

                <div className="mb-3">
                    <label htmlFor="product" className="form-label">Select Product</label>
                    <select className="form-select" id="product" value={selectedProduct} onChange={(e) => setSelectedProduct(e.target.value)}>
                        {products.map(product => (
                            <option key={product.id} value={product.id}>{product.name} - ${product.price}</option>
                        ))}
                    </select>
                </div>
                <div className="mb-3">
                    <label htmlFor="quantity" className="form-label">Quantity</label>
                    <input type="number" className="form-control" id="quantity" value={quantity} onChange={(e) => setQuantity(parseInt(e.target.value))} min="1" />
                </div>
                <button type="button" className="btn btn-info mb-3" onClick={handleAddOrderItem}>Add Order Item</button>

                <h4>Current Order Items:</h4>
                <ul className="list-group mb-3">
                    {orderItems.length === 0 ? (
                        <li className="list-group-item">No items added yet.</li>
                    ) : (
                        orderItems.map((item, index) => (
                            <li key={index} className="list-group-item">
                                {item.product.name} - Quantity: {item.quantity}
                            </li>
                        ))
                    )}
                </ul>

                <button type="submit" className="btn btn-success me-2">Place Order</button>
                <button type="button" className="btn btn-secondary" onClick={onCancel}>Cancel</button>
            </form>
        </div>
    );
};

export default OrderForm;
