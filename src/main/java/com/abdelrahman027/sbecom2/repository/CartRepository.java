package com.abdelrahman027.sbecom2.repository;

import com.abdelrahman027.sbecom2.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart,Long> {


    @Query("SELECT c FROM Cart  c WHERE c.user.email = ?1")
    Cart findCartByUserEmail(String email);

    @Query("select c from Cart c where c.user.email=?1 and c.cartId=?2")
    Cart findCartByUserEmailAndCartId(String email,Long cartId);

    @Query("select c from Cart c join fetch c.cartItems ci join fetch ci.product p where  p.productId =?1")
    List<Cart> findCartByProductId(Long productId);


}
