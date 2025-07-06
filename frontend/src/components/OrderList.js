import React, { useState, useEffect } from 'react';
import axios from 'axios';

const OrderList = ({ onAddOrder }) => {
    const [orders, setOrders] = useState([]);

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = () => {
        axios.get('/api/orders')
            .then(response => {
                setOrders(response.data);
            })
            .catch(error => console.error('Error fetching orders:', error));
    };

    return (
        <div>
            <h2>Orders</h2>
            <button className="btn btn-primary mb-3" onClick={onAddOrder}>Create New Order</button>
            {orders.map(order => (
                <div key={order.id} className="card mb-3">
                    <div className="card-header">
                        Order ID: {order.id} - Customer: {order.customerName}
                    </div>
                    <div className="card-body">
                        <h5 className="card-title">Order Items:</h5>
                        <ul className="list-group">
                            {order.orderItems.map(item => (
                                <li key={item.id} className="list-group-item">
                                    Product: {item.product ? item.product.name : 'N/A'} - Quantity: {item.quantity}
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default OrderList;