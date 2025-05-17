package com.example.proyectofinal.Repository;

import com.example.proyectofinal.Entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
