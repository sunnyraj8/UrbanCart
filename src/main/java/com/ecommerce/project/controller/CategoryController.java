package com.ecommerce.project.controller;

import com.ecommerce.project.Configuration.AppConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {


    private CategoryService categorieservice;

    public CategoryController(CategoryService categorieservice) {
        this.categorieservice = categorieservice;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getCategories(@RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber, @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize, @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_CATEGORIES_BY,required = false) String sortBy, @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder) {
        return new ResponseEntity<>(categorieservice.getallcategories(pageNumber,pageSize,sortBy,sortOrder), HttpStatus.OK);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> createcategory(@Valid @RequestBody CategoryDTO categoryDTO){

        CategoryDTO savedcategoryDTO=categorieservice.createcategory(categoryDTO);
        return new  ResponseEntity<>(savedcategoryDTO,HttpStatus.CREATED);
    }
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deletecategory(@PathVariable Long categoryId){
            CategoryDTO deletecategory=categorieservice.deletecategory(categoryId);
            return ResponseEntity.status(HttpStatus.OK).body(deletecategory);

    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updatecategory(@PathVariable Long categoryId,@Valid @RequestBody CategoryDTO categoryDTO){
          CategoryDTO savedCategoryDTO=categorieservice.updatecategory(categoryId,categoryDTO);
          return new ResponseEntity<>(savedCategoryDTO,HttpStatus.OK);

    }
    //usage of @Request param
    @GetMapping("display")
    public ResponseEntity<String> echoMessage(@RequestParam(name = "message") String message ){
        return new ResponseEntity<>("Echoed message: "+message,HttpStatus.OK);
    }
    //
}
