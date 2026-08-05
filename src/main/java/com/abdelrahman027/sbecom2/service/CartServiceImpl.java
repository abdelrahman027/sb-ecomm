package com.abdelrahman027.sbecom2.service;


import com.abdelrahman027.sbecom2.dto.CartDTO;
import com.abdelrahman027.sbecom2.dto.ProductDTO;
import com.abdelrahman027.sbecom2.exception.ApiException;
import com.abdelrahman027.sbecom2.exception.ResourceNotFoundException;
import com.abdelrahman027.sbecom2.model.Cart;
import com.abdelrahman027.sbecom2.model.CartItem;
import com.abdelrahman027.sbecom2.model.Product;
import com.abdelrahman027.sbecom2.repository.CartItemRepository;
import com.abdelrahman027.sbecom2.repository.CartRepository;
import com.abdelrahman027.sbecom2.repository.ProductRepository;
import com.abdelrahman027.sbecom2.utils.AuthUtils;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final AuthUtils authUtils;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", productId, "product id"));
        CartItem cartItem =cartItemRepository.findCartItemByCartAndProduct(cart,product);

        if (cartItem != null) {
            throw new ApiException("product " +product.getProductName() +" already in the cart");
        }

        if (product.getQuantity() == 0) {
            throw new ApiException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new ApiException("the quantity is higher the stock of "+product.getProductName());
        }
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        cartItemRepository.save(newCartItem);
        product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() *quantity));
        Cart savedCart =  cartRepository.save(cart);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream =cartItems.stream().map(item->{
            ProductDTO map = modelMapper.map(item.getProduct(),ProductDTO.class);
                    map.setQuantity(item.getQuantity());
            return map;
        });

        CartDTO cartDto =modelMapper.map(savedCart,CartDTO.class);
        cartDto.setProducts(productDTOStream.toList());
        return cartDto;
    }

    @Override
    public Cart createCart() {
        Cart userCart = cartRepository.findCartByUserEmail(authUtils.loggedInEmail());
        if (userCart !=null ) return userCart;

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtils.loggedInUser());
        return cartRepository.save(cart);
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
         throw new ApiException("no carts found!");
        }

        return carts.stream().map(
                (cart)->{
                  CartDTO cartDTO=  modelMapper.map(cart,CartDTO.class);
                    List<ProductDTO> productDTOS = cart.getCartItems().stream().map(ct->
                            modelMapper.map(ct.getProduct(),ProductDTO.class)
                            ).toList();
                    cartDTO.setProducts(productDTOS);
                    return cartDTO;
                }).toList();
    }

    @Override
    public CartDTO getCart(String email, Long cartId) {
    Cart cart = cartRepository.findCartByUserEmailAndCartId(email,cartId);
        CartDTO savedCart = modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(c->c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> productDTOS = cart.getCartItems().stream().map(ct->modelMapper.map(ct.getProduct(),ProductDTO.class)).toList();
        savedCart.setProducts(productDTOS);
        return savedCart;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantity(Long productId, Integer quantity) {

        Cart userCart = cartRepository.findCartByUserEmail(authUtils.loggedInEmail());
        if (userCart == null) throw new ApiException("user has no cart yet start add!");
        Cart cart = cartRepository.findById(userCart.getCartId()).orElseThrow(()-> new ResourceNotFoundException("cart",userCart.getCartId(),"cart not found"));

        Product product = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("product", productId,"product id"));
        if (product.getQuantity() == 0) {
            throw new ApiException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new ApiException("the quantity is higher the stock of "+product.getProductName());
        }

        CartItem cartItem =cartItemRepository.findCartItemByCartIdAndProductId(userCart.getCartId(),productId);
        if (cartItem == null) throw new ApiException("there is no such item in the cart");
        int newQuantity =cartItem.getQuantity()+ quantity;
        if (newQuantity < 0) {
            throw new ApiException("the resulting quantity cannot be negative");
        }

        if (newQuantity == 0)  {deleteProductFromCart(productId,cart.getCartId());}
        else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }
        CartItem updatedItem =  cartItemRepository.save(cartItem);
       if (updatedItem.getQuantity() <= 0) {
           cartItemRepository.deleteById(updatedItem.getCartItemId());
       }

        CartDTO savedCart = modelMapper.map(userCart,CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream =  cartItems.stream().map(
                item->{
                    ProductDTO productDTO = modelMapper.map(item.getProduct(),ProductDTO.class);
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                }
        );
        savedCart.setProducts(productDTOStream.toList());
        return savedCart;
    }

    @Override
    @Transactional
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(()->new ResourceNotFoundException("cart",cartId,"cart id"));
        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId,productId);
        if (cartItem == null) throw new ApiException("item not found!");
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice()*cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartID(productId,cartId);

        return "deleted";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
    Cart cart = cartRepository.findById(cartId).orElseThrow(()->new ResourceNotFoundException("cart",cartId,"cart id"));
    Product product = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("product",productId,"product id"));
    CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId,productId);
    if (cartItem == null) throw new ApiException("cart item not found");
    double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice()*cartItem.getQuantity());
    cartItem.setProductPrice(product.getSpecialPrice());
    cart.setTotalPrice(cartPrice+
            (cartItem.getProductPrice()*cartItem.getQuantity()));

    CartItem cartItem1 =cartItemRepository.save(cartItem);
    }




}