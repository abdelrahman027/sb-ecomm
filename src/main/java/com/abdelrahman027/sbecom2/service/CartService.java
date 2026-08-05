package com.abdelrahman027.sbecom2.service;

import com.abdelrahman027.sbecom2.dto.CartDTO;
import com.abdelrahman027.sbecom2.model.Cart;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface CartService {
    CartDTO addProductToCart(Long ProductId, Integer quantity);

    Cart createCart();

    List<CartDTO> getAllCarts();

   CartDTO getCart(String email, Long cartId);

   @Transactional
   CartDTO updateProductQuantity(Long productId, Integer quantity);

   String deleteProductFromCart(Long cartId, Long productId);

   void updateProductInCarts(Long cartId,Long productId);
}
