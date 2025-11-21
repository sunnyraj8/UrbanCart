package com.ecommerce.project.controller;

import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/products")
    ResponseEntity<Optional<List>> showProducts(){
        Optional<List> productDTO=productService.showProducts();
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }
    @PostMapping("/admin/categories/{categoryId}/product")
    ResponseEntity<ProductDTO> addproduct(@RequestBody Product product,
                                          @PathVariable Long categoryId){
        ProductDTO productDTO= productService.addProduct(categoryId,product);
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

}
