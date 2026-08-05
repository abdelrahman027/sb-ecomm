package com.abdelrahman027.sbecom2.service;

import com.abdelrahman027.sbecom2.dto.CategoryDTO;
import com.abdelrahman027.sbecom2.dto.CategoryResponse;
import com.abdelrahman027.sbecom2.model.Category;

import java.util.List;

public interface CategoryService {

   CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy,String sortOrder);

   Category createCategory(CategoryDTO categoryDTO);

   String deleteCategory(Long id);

   Category updateCategory(CategoryDTO categoryDTO);

}
