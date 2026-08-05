package com.abdelrahman027.sbecom2.repository;

import com.abdelrahman027.sbecom2.model.Category;
import com.abdelrahman027.sbecom2.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findProductByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);

    Page<Product> findProductByProductNameContainingIgnoreCase(String productName,Pageable pageDetails);

    Product findProductByProductNameIgnoreCase(String productName);
}
