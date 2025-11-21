package com.ecommerce.project.service;

import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, Product product);

    Optional<List> showProducts();
}
