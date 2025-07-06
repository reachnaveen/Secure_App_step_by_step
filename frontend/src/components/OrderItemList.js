import React, { useState, useEffect } from 'react';
import axios from 'axios';

const OrderItemList = () => {
    const [orderItems, setOrderItems] = useState([]);

    useEffect(() => {
        axios.get('/api/order-items')
            .then(response => {
                setOrderItems(response.data);
            });
    }, []);

    return (
        <div>
            <h2>Order Items</h2>
            <table className="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Product</th>
                        <th>Quantity</th>
                    </tr>
                </thead>
                <tbody>
                    {orderItems.map(orderItem => (
                        <tr key={orderItem.id}>
                            <td>{orderItem.id}</td>
                            <td>{orderItem.product.name}</td>
                            <td>{orderItem.quantity}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default OrderItemList;
