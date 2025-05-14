package com.example.proyectofinal;

import com.example.proyectofinal.Entity.User;
import com.example.proyectofinal.Repository.UserRepository;
import com.example.proyectofinal.ResponseRequest.RegisterRequest;
import com.example.proyectofinal.ResponseRequest.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@CrossOrigin(maxAge = 3600)
@RestController
public class EchoController {
    @Autowired
    UserRepository repositoryUser;
    @PostMapping("/auth/register")
    public ResponseEntity<?> registerClient(@RequestBody RegisterRequest registerRequest) {
        Optional<User> existingClient = repositoryUser.findByUsername(registerRequest.getUsername());

        if (existingClient.isEmpty()) {
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            repositoryUser.save(user);
            return ResponseEntity.status(HttpStatus.OK).body(new RegisterResponse("Cliente registrado exitosamente"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new RegisterResponse("El cliente con ese nombre de usuario ya existe"));
        }
    }
}
