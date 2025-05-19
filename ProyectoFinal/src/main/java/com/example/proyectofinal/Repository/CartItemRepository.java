package com.example.proyectofinal.Repository;


import com.example.proyectofinal.Entity.Cart_item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends CrudRepository<Cart_item, Long> {

    @Query("SELECT c FROM Cart_item c WHERE c.product.id = :productId")
    List<Cart_item> findByProductId(Long productId);
}
