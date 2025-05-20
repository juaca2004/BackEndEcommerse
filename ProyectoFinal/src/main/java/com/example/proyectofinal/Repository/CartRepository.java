package com.example.proyectofinal.Repository;

import com.example.proyectofinal.Entity.Cart;

import com.example.proyectofinal.Entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository  extends CrudRepository<Cart, Long> {


    @Query ("SELECT c FROM Cart c WHERE c.user.id = :userId")
    Optional<Cart> findCartByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Cart c WHERE c.user = :user")
    Optional<Cart> findByUser(@Param("user") User user);
}
