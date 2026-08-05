package com.abdelrahman027.sbecom2.service;

import com.abdelrahman027.sbecom2.dto.CartDTO;
import com.abdelrahman027.sbecom2.dto.ProductDTO;
import com.abdelrahman027.sbecom2.dto.ProductResponse;
import com.abdelrahman027.sbecom2.exception.ApiException;
import com.abdelrahman027.sbecom2.exception.ResourceNotFoundException;
import com.abdelrahman027.sbecom2.model.Cart;
import com.abdelrahman027.sbecom2.model.Category;
import com.abdelrahman027.sbecom2.model.Product;
import com.abdelrahman027.sbecom2.repository.CartRepository;
import com.abdelrahman027.sbecom2.repository.CategoryRepository;
import com.abdelrahman027.sbecom2.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final CartRepository cartRepository;
    private final CartService cartService;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", categoryId, "category ID"));
        productDTO.setImage("default.png");
        Product myProduct = productRepository.findProductByProductNameIgnoreCase(productDTO.getProductName());
        if (myProduct != null ) {
            throw new ApiException("product with this name already exist");
        }

        double specialPrice = productDTO.getPrice() - ((productDTO.getDiscount() * 0.01) * productDTO.getPrice());
        productDTO.setSpecialPrice(specialPrice);
        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> products = productRepository.findAll(pageDetails);
        List<Product> productList = products.getContent();

        if (productList.isEmpty()) throw new ApiException("no products yet");

        List<ProductDTO> productDTOS = products.stream().map((prod) -> modelMapper.map(prod, ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(products.getNumber());
        productResponse.setPageSize(products.getSize());
        productResponse.setTotalElements(products.getTotalElements());
        productResponse.setTotalPages(products.getTotalPages());
        productResponse.setLastPage(products.isLast());


        return productResponse;
    }

    @Override
    public ProductResponse getAllProductsByCategory(Long categoryId,Integer pageNumber, Integer pageSize , String sortBy , String sortOrder) {
        //Getting Category
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("category", categoryId, "categoryId"));
        //Getting Products by Category
        //sort logic
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        // start pagination
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findProductByCategoryOrderByPriceAsc(category,pageDetails);
        List<Product> products = productPage.getContent();
        //Instantiate new response class
        ProductResponse resp = new ProductResponse();
        // map products to products DTO
        resp.setPageNumber(productPage.getNumber());
        resp.setPageSize(productPage.getSize());
        resp.setTotalElements(productPage.getTotalElements());
        resp.setTotalPages(productPage.getTotalPages());
        resp.setLastPage(productPage.isLast());
        resp.setContent(products.stream().map(prod -> modelMapper.map(prod, ProductDTO.class)).toList());
        //returning response
        return resp;
    }

    @Override
    public ProductResponse getAllProductsByKeyword(String keyword,Integer pageNumber, Integer pageSize , String sortBy , String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageDetails =PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findProductByProductNameContainingIgnoreCase(keyword,pageDetails);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) throw new ApiException("no products with this name exists !");
        ProductResponse resp = new ProductResponse();
        resp.setPageNumber(productPage.getNumber());
        resp.setPageSize(productPage.getSize());
        resp.setTotalElements(productPage.getTotalElements());
        resp.setTotalPages(productPage.getTotalPages());
        resp.setLastPage(productPage.isLast());
        resp.setContent(products.stream().map(prod -> modelMapper.map(prod, ProductDTO.class)).toList());
        return resp;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", productId, "product id"));

        product.setProductName(productDTO.getProductName());
        product.setDiscount(productDTO.getDiscount());
        product.setPrice(productDTO.getPrice());
        product.setSpecialPrice(productDTO.getPrice());
        product.setImage(productDTO.getImage());
        product.setQuantity(productDTO.getQuantity());
//          Product updatedProduct = modelMapper.map(productDTO,Product.class);
        Product savedProduct = productRepository.save(product);
        List<Cart> carts = cartRepository.findCartByProductId(productId);

        List<CartDTO> cartDTOS = carts.stream().map(c -> {
            CartDTO cartDTO = modelMapper.map(c, CartDTO.class);
            List<ProductDTO> productDTOS = c.getCartItems().stream().map(ci -> modelMapper.map(ci.getProduct(), ProductDTO.class)).toList();

            return cartDTO;

        }).toList();

        cartDTOS.forEach(c->cartService.updateProductInCarts(c.getCartId(),productId));

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public String deleteProduct(Long productId) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("product", productId, "product id"));
        productRepository.delete(product);

        String path = "media/images/products/";
        fileService.deleteOldImage(product,path);
        List<Cart> carts = cartRepository.findCartByProductId(productId);
        carts.forEach(c->cartService.deleteProductFromCart(c.getCartId(),productId));

        return "product with id " + productId + " deleted successfully!";
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDb = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("product", productId, "Product id"));
        String path = "media/images/products/";
        fileService.deleteOldImage(productFromDb,path);
        String fileName = fileService.uploadImage(path, image);

        productFromDb.setImage(fileName);
        Product savedProduct = productRepository.save(productFromDb);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }


}
