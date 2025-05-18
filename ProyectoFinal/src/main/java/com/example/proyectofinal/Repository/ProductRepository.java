package com.example.proyectofinal.Repository;

import com.example.proyectofinal.Entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Product> findByName(@Param("name") String name);

    @Query("SELECT p FROM Product p " +
            "WHERE (:nombre IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (:descripcion IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :descripcion, '%'))) " +
            "AND (:stockMin IS NULL OR p.stock >= :stockMin) " +
            "AND (:precioMax IS NULL OR p.price <= :precioMax)")
    List<Product> listOfProducts(
            @Param("nombre") String nombre,
            @Param("descripcion") String descripcion,
            @Param("stockMin") Integer stockMin,
            @Param("precioMax") Double precioMax
    );

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
}
