package com.abdelrahman027.sbecom2.repository;

import com.abdelrahman027.sbecom2.model.Cart;
import com.abdelrahman027.sbecom2.model.CartItem;
import com.abdelrahman027.sbecom2.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    CartItem findCartItemByCartAndProduct(Cart cart, Product product);

    @Query("SELECT ci from CartItem ci where ci.cart.cartId = ?1 and ci.product.productId =?2")
    CartItem findCartItemByCartIdAndProductId(Long cartId, Long productId);

    @Modifying
    @Query("delete from CartItem ci where ci.product.productId =?1 and ci.cart.cartId = ?2")
    void deleteCartItemByProductIdAndCartID(Long productId,Long cartId);


}
