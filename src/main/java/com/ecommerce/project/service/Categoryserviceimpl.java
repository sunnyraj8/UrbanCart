package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;



@Service
public class Categoryserviceimpl implements CategoryService{

//    private List<Category> categories = new ArrayList<>();
    public CategoryRepository categorieRepo;
    @Autowired
    public ModelMapper modelMapper;

    public Categoryserviceimpl(CategoryRepository categories) {
        this.categorieRepo = categories;

    }

//    private Long nextId=1L;
    @Override
    public CategoryResponse getallcategories(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByandOrder);
        Page<Category> categoryPage=categorieRepo.findAll(pageDetails);
        List<Category> categories=categoryPage.getContent();
        if (categories.isEmpty()) throw new APIException("Category doesn't exists.");
        List<CategoryDTO> categoryDTOS=categories.stream().map(category -> modelMapper.map(category,CategoryDTO.class)).toList();
        CategoryResponse categoryresponse= new CategoryResponse();
        categoryresponse.setContent(categoryDTOS);
        categoryresponse.setPageNumber(categoryPage.getNumber());
        categoryresponse.setPageSize(categoryPage.getSize());
        categoryresponse.setTotalElements(categoryPage.getTotalElements());
        categoryresponse.setTotalpages(categoryPage.getTotalPages());
        categoryresponse.setLastPage(categoryPage.isLast());
        return categoryresponse;
    }

    @Override
    public CategoryDTO createcategory(CategoryDTO categorieDTO) {
        Category category=modelMapper.map(categorieDTO,Category.class);
        Category categoryfromDB=categorieRepo.findByCategoryName(category.getCategoryName());
        if(categoryfromDB!=null){
            throw new APIException("Category name: "+category.getCategoryName()+" already exist");
        }
        Category savedCategory=categorieRepo.save(category);
        return modelMapper.map(savedCategory,CategoryDTO.class);
    }

    @Override
    public CategoryDTO deletecategory(Long categoryId) {
        Category category = categorieRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

        categorieRepo.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updatecategory(Long categoryId, CategoryDTO categoryDTO) {
        Category savedCategory = categorieRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categorieRepo.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);

    }

}
