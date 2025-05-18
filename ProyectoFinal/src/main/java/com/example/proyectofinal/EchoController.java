package com.example.proyectofinal;

import com.example.proyectofinal.Entity.Cart;
import com.example.proyectofinal.Entity.Product;
import com.example.proyectofinal.Entity.User;
import com.example.proyectofinal.Entity.Order;
import com.example.proyectofinal.Repository.Cart_itemRepository;
import com.example.proyectofinal.Repository.OrderRepository;
import com.example.proyectofinal.Repository.ProductRepository;
import com.example.proyectofinal.Repository.UserRepository;
import com.example.proyectofinal.ResponseRequest.RegisterRequest;
import com.example.proyectofinal.ResponseRequest.*;
import com.example.proyectofinal.ResponseRequest.RegisterResponse;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(maxAge = 3600)
@RestController
public class EchoController {
    @Autowired
    UserRepository repositoryUser;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;


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


    @PostMapping("/auth/login")
    public ResponseEntity<?> loginClient(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = repositoryUser.searchByLogin(loginRequest.getUsername(), loginRequest.getPassword());

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(new LoginResponse("Inicio de sesión exitoso"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Credenciales incorrectas"));
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

    @GetMapping("/api/products")
    public ResponseEntity<?> getProducts() {

        List<Product> products = (List<Product>) productRepository.findAll();

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay productos disponibles");
        }else {
            return ResponseEntity.ok(products);
        }

    }






    @PostMapping("/api/order/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        Optional<User> userOptional = repositoryUser.findById(1L); // Usuario fijo para pruebas

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        User user = userOptional.get();


        Order order = new Order();
        order.setUser(user);
        order.setCity(orderRequest.getShipping().getCity());
        order.setAddress(orderRequest.getShipping().getAddress());
        order.setFullName(orderRequest.getShipping().getFullName());
        order.setTypePayment(orderRequest.getPaymentMethod());
        order.setTotal(BigDecimal.valueOf(100)); // Puedes calcularlo dinámicamente con el carrito
        order.setStatus("Exitosa");

        orderRepository.save(order);

        return ResponseEntity.status(HttpStatus.CREATED).body("Orden creada exitosamente");
    }

}
