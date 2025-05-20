package com.example.proyectofinal.Repository;

import com.example.proyectofinal.Entity.Cart_item;
import com.example.proyectofinal.Entity.Order_item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends CrudRepository<Order_item, Long> {

}
