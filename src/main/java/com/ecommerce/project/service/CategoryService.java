package com.ecommerce.project.service;

import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;

public interface CategoryService {
     CategoryResponse getallcategories(int pageNumber, int pageSize, String sortBy, String sortOrder);
     CategoryDTO createcategory(CategoryDTO  categorieDTO);
     CategoryDTO deletecategory(Long categoryId);
     CategoryDTO updatecategory(Long categoryId, CategoryDTO updcategoryDTO);
}
