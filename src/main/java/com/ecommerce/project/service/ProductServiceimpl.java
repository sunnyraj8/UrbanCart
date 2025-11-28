package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceimpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;
    @Value("{project.image}")
    private String path;
    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category  category= categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryID",categoryId));
        //Check if product already present or not.
        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("DefaultImg.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }else {
            throw new APIException("Product Already Exists...");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findAll(pageDetails);

        List<Product> products= pageProducts.getContent();

        List<ProductDTO> productDTOS= products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();

        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category  category= categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryID",categoryId));
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);

        List<Product> products= pageProducts.getContent();
        List<ProductDTO> productDTOS= products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        if (products.isEmpty()){
            throw new APIException(category.getCategoryName()+ " Category does not have any products...");
        }
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%',pageDetails);

        List<Product> products= pageProducts.getContent();
        List<ProductDTO> productDTOS= products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        if (products.isEmpty()){
            throw new APIException("Products not found with keyword: "+ keyword);
        }
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        //get the existing product from DB
        Product productFromDB= productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));
        Product product= modelMapper.map(productDTO,Product.class);
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

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //Get the product from DB
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId) );
        //Upload image to server
        //Get the file name of uploaded image
//        String path = "images/";
        String fileName = fileService.uploadImage(path,image);
        //Updating the new file name to the product
        productFromDb.setImage(fileName);

        //Save updated product
        Product updatedProduct= productRepository.save(productFromDb);
        //return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }




}
