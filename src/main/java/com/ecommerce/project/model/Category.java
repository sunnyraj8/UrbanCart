package com.ecommerce.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity(name = "Categories")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    @NotBlank
    @Size(min = 4,message = "Category name should be greater than or equal to 4")
    private String categoryName;

//    public Long getCategoryid() {
//        return categoryid;
//    }
//
//    public void setCategoryid(Long categoryid) {
//        this.categoryid = categoryid;
//    }
//
//    public String getCategoryName() {
//        return categoryName;
//    }
//
//    public void setCategoryName(String categoryName) {
//        this.categoryName = categoryName;
//    }

//    public Category(Long categoryid, String categoryName) {
//        this.categoryid = categoryid;
//        this.categoryName = categoryName;
//    }

//    public Category() {
//    }
}
