package com.example.proyectofinal.Repository;

import com.example.proyectofinal.Entity.Cart_item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Cart_itemRepository extends CrudRepository<Cart_item, Long> {
}
