package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceimpl implements ProductService{
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public ProductDTO addProduct(Long categoryId, Product product) {
        Category  category= categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryID",categoryId));
        product.setImage("DefaultImg.png");
        product.setCategory(category);
        double specialPrice=product.getPrice()-((product.getDiscount()*0.01)*product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct=productRepository.save(product);

        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products= productRepository.findAll();
        List<ProductDTO> productDTOS= products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category  category= categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryID",categoryId));
        List<Product> products= productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDTO> productDTOS= products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchByKeyword(String keyword) {
        List<Product> products= productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');
        List<ProductDTO> productDTOS= products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, Product product) {
        //get the existing product from DB
        Product productFromDB= productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));
        //update the product info with user shared
        productFromDB.setProductName(product.getProductName());
        productFromDB.setDescription(product.getDescription());
        productFromDB.setQuantity(product.getQuantity());
        productFromDB.setPrice(product.getPrice());
        productFromDB.setDiscount(product.getDiscount());
        double sp=product.getPrice()-((product.getDiscount()*0.01)*product.getPrice());
        productFromDB.setSpecialPrice(sp);
        //productFromDB.setSpecialPrice(product.getSpecialPrice());
        //double specialPrice=product.getPrice()-((product.getDiscount()*0.01)*product.getPrice());

        //Save to database
        Product savedproduct = productRepository.save(productFromDB);
        return modelMapper.map(savedproduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product= productRepository.findById(productId)
                        .orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));
        productRepository.deleteById(productId);
        return modelMapper.map(product,ProductDTO.class);
    }


}
