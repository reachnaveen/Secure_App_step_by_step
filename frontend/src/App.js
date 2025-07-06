import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import ProductList from './components/ProductList';
import OrderList from './components/OrderList';
import OrderItemList from './components/OrderItemList';

function App() {
    return (
        <div className="container">
            <h1>Order Management</h1>
            <ProductList />
            <OrderList />
            <OrderItemList />
        </div>
    );
}

export default App;