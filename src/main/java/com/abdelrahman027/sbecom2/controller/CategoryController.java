package com.abdelrahman027.sbecom2.controller;


import com.abdelrahman027.sbecom2.config.AppConstants;
import com.abdelrahman027.sbecom2.dto.CategoryDTO;
import com.abdelrahman027.sbecom2.dto.CategoryResponse;
import com.abdelrahman027.sbecom2.model.Category;
import com.abdelrahman027.sbecom2.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
        System.out.println("inside controller");
    }


    @GetMapping("/api/public/categories")
     public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber" ,defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy",defaultValue = AppConstants.CATEGORY_SORT_BY) String sortBy,
            @RequestParam(value = "sortOrder",defaultValue = AppConstants.SORT_ORDER) String sortOrder
            ) {
        return new ResponseEntity<>(  categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder),HttpStatus.OK);
     }


    @PostMapping("/api/public/categories")
    public ResponseEntity<Category> createCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        return new ResponseEntity<Category>(categoryService.createCategory(categoryDTO),HttpStatus.CREATED);
    }

    @DeleteMapping ("/api/admin/categories/{category_id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long category_id) {
        try {
            return new ResponseEntity<>(categoryService.deleteCategory(category_id), HttpStatus.OK);
        } catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(),e.getStatusCode());

        }
    }

    @PutMapping("/api/public/categories")
    public ResponseEntity<?> updateCategory(@RequestBody CategoryDTO categoryDTO) {
        System.out.println("inside update");
        try {
            return new ResponseEntity<Category>(categoryService.updateCategory(categoryDTO),HttpStatus.OK);
        }
        catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        }

    }
}
