package com.abdelrahman027.sbecom2.service;

import com.abdelrahman027.sbecom2.dto.ProductDTO;
import com.abdelrahman027.sbecom2.dto.ProductResponse;
import com.abdelrahman027.sbecom2.model.Category;
import com.abdelrahman027.sbecom2.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    ProductDTO addProduct(Long CategoryId, ProductDTO productDTO);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize , String sortBy , String sortOrder);

    ProductResponse getAllProductsByCategory(Long categoryId,Integer pageNumber, Integer pageSize , String sortBy , String sortOrder);

    ProductResponse getAllProductsByKeyword(String keyword,Integer pageNumber, Integer pageSize , String sortBy , String sortOrder);

    ProductDTO updateProduct(Long productId,ProductDTO productDTO);

    String deleteProduct(Long productId) throws IOException;

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
