package com.abdelrahman027.sbecom2.service;


import com.abdelrahman027.sbecom2.dto.CategoryDTO;
import com.abdelrahman027.sbecom2.dto.CategoryResponse;
import com.abdelrahman027.sbecom2.exception.ApiException;
import com.abdelrahman027.sbecom2.exception.ResourceNotFoundException;
import com.abdelrahman027.sbecom2.model.Category;
import com.abdelrahman027.sbecom2.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final  List<Category> categories = new ArrayList<>();

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;



    @Override
    public CategoryResponse getAllCategories(
        Integer pageNumber,Integer pageSize ,String sortBy,String sortOrder
    ) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() :Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();

        if (categories.isEmpty()) throw  new ApiException("not categories yet");
        List<CategoryDTO> categoryDTOS  = categories.stream().map(cat->modelMapper.map(cat,CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return  categoryResponse;
    }

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {

        Category savedCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if (savedCategory != null) {
            throw new ApiException("category with this name already found");
        }
        Category category = modelMapper.map(categoryDTO,Category.class);
        categoryRepository.save(category);
        return category;
    }

    @Override
    public String deleteCategory(Long id) {
        boolean isExist =categoryRepository.existsById(id);
        if (!isExist) {
            throw new ResourceNotFoundException("category",id,"category id");
        }
       categoryRepository.deleteById(id);
        return "deleted";
    }

    @Override
    public Category updateCategory(CategoryDTO categoryDTO) {
        Category fetchedCategory = categoryRepository.findById(categoryDTO.getCategoryId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"category not found"));
        fetchedCategory.setCategoryName(categoryDTO.getCategoryName());
        return categoryRepository.save(fetchedCategory);
    }
}
