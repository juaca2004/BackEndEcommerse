package com.example.proyectofinal.Repository;

import com.example.proyectofinal.Entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {


    @Query("SELECT u FROM User u WHERE u.username=:name AND u.password=:password")
    public Optional<User> searchByLogin(@Param("name") String name, @Param("password") String password);

    @Query("SELECT c FROM User c WHERE c.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

}

