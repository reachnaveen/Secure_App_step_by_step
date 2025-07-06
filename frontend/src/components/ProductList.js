import React, { useState, useEffect } from 'react';
import axios from 'axios';

const ProductList = ({ onEditProduct, onAddProduct }) => {
    const [products, setProducts] = useState([]);

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = () => {
        axios.get('/api/products')
            .then(response => {
                setProducts(response.data);
            })
            .catch(error => console.error('Error fetching products:', error));
    };

    const handleDelete = (id) => {
        axios.delete(`/api/products/${id}`)
            .then(() => {
                fetchProducts();
            })
            .catch(error => console.error('Error deleting product:', error));
    };

    return (
        <div>
            <h2>Products</h2>
            <button className="btn btn-primary mb-3" onClick={onAddProduct}>Add Product</button>
            <table className="table table-striped">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Price</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {products.map(product => (
                        <tr key={product.id}>
                            <td>{product.id}</td>
                            <td>{product.name}</td>
                            <td>{product.description}</td>
                            <td>{product.price}</td>
                            <td>
                                <button className="btn btn-warning btn-sm me-2" onClick={() => onEditProduct(product)}>Edit</button>
                                <button className="btn btn-danger btn-sm" onClick={() => handleDelete(product.id)}>Delete</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ProductList;
