package com.example.proyectofinal.Repository;

import com.example.proyectofinal.Entity.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends CrudRepository<Client,Long> {


    @Query("SELECT u FROM users u WHERE u.username=:name AND u.password=:password")
    public Optional<Client> searchByLogin(@Param("name") String name,@Param("password") String password);

    @Query("SELECT c FROM users c WHERE c.username = :username")
    Optional<Client> findByUsername(@Param("username") String username);

}

