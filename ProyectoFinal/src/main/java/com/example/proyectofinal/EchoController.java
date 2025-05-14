package com.example.proyectofinal;

import com.example.proyectofinal.Entity.Cart;
import com.example.proyectofinal.Entity.User;
import com.example.proyectofinal.Repository.Cart_itemRepository;
import com.example.proyectofinal.Repository.UserRepository;
import com.example.proyectofinal.ResponseRequest.RegisterRequest;
import com.example.proyectofinal.ResponseRequest.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(maxAge = 3600)
@RestController
public class EchoController {
    @Autowired
    UserRepository repositoryUser;

    @Autowired
    private Cart_itemRepository cartItemRepository;
    @PostMapping("/auth/register")
    public ResponseEntity<?> registerClient(@RequestBody RegisterRequest registerRequest) {
        Optional<User> existingClient = repositoryUser.findByUsername(registerRequest.getUsername());

        if (existingClient.isEmpty()) {
            // Crear el usuario
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());

            // Crear el carrito y asociarlo al usuario
            Cart cart = new Cart();
            cart.setUser(user);
            user.setCart(cart); // Bidireccional: aseguramos que ambos se conozcan

            // Guardar el usuario (gracias al cascade, Cart también se guarda)
            repositoryUser.save(user);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new RegisterResponse("Cliente registrado exitosamente"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterResponse("El cliente con ese nombre de usuario ya existe"));
        }
    }



    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long itemId) {
        return cartItemRepository.findById(itemId).map(item -> {
            // Eliminar el Cart_item
            cartItemRepository.delete(item);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

}
