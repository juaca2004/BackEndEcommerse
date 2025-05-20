package com.example.proyectofinal.Repository;


import com.example.proyectofinal.Entity.Cart_item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends CrudRepository<Cart_item, Long> {

    @Query("SELECT c FROM Cart_item c WHERE c.product.id = :productId")
    List<Cart_item> findByProductId(Long productId);

    @Query("SELECT ci FROM Cart_item ci WHERE ci.id = :itemId AND ci.cart.user.id = :userId")
    Optional<Cart_item> findByIdAndUserId(@Param("itemId") Long itemId, @Param("userId") Long userId);
}
