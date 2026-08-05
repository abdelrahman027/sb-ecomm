package com.abdelrahman027.sbecom2.controller;


import com.abdelrahman027.sbecom2.dto.CartDTO;
import com.abdelrahman027.sbecom2.dto.CategoryResponse;
import com.abdelrahman027.sbecom2.exception.ApiException;
import com.abdelrahman027.sbecom2.model.Cart;
import com.abdelrahman027.sbecom2.repository.CartRepository;
import com.abdelrahman027.sbecom2.service.CartService;
import com.abdelrahman027.sbecom2.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final AuthUtils authUtils;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,@PathVariable Integer quantity) {
        CartDTO cartDTO =  cartService.addProductToCart(productId,quantity);

        return  new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts() {

        List<CartDTO> cartDTOS = cartService.getAllCarts();

        return ResponseEntity.ok(cartDTOS);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById() {
        String email = authUtils.loggedInEmail();
        Cart cart = cartRepository.findCartByUserEmail(email);
        if (cart == null) {
            throw new ApiException("no cart is available start adding products first");
        }
        Long cartId= cart.getCartId();
        CartDTO cartDTO = cartService.getCart(email,cartId);
        return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
    }

    @PutMapping("cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId, @PathVariable String operation) {

        CartDTO cartDTO =cartService.updateProductQuantity(productId,operation .equalsIgnoreCase("delete")?-1:1);

        return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
    }

    @DeleteMapping("carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteItemFormCart(@PathVariable Long cartId, @PathVariable Long productId) {
        String Status =cartService.deleteProductFromCart(cartId,productId);
        return new ResponseEntity<>("Deleted",HttpStatus.OK);
    }
}
