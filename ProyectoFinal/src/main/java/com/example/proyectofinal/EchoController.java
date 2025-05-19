package com.example.proyectofinal;

import com.example.proyectofinal.Entity.*;
import com.example.proyectofinal.Repository.*;
import com.example.proyectofinal.ResponseRequest.RegisterRequest;
import com.example.proyectofinal.ResponseRequest.*;
import com.example.proyectofinal.ResponseRequest.RegisterResponse;
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
    private CartRepository cartRepository;


    @Autowired
    private CartItemRepository cartItemRepository;
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
                    .body(user);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterResponse("El cliente con ese nombre de usuario ya existe"));
        }
    }


    @PostMapping("/auth/login")
    public ResponseEntity<?> loginClient(@RequestBody LoginRequest loginRequest) {
        var userOptional = repositoryUser.searchByLogin(loginRequest.getUsername(), loginRequest.getPassword());
        if (userOptional.isPresent()) {
            System.out.println("ENTREEE");
            System.out.println(userOptional.get());
            return ResponseEntity.status(200).body(userOptional.get());
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

    @PostMapping("/cart/add/{userId}/{productId}")
    public ResponseEntity<?> addProductToCart(@PathVariable Long userId,@PathVariable Long productId) {
        Optional<User> userOptional = repositoryUser.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        } else {

            Optional<Product> productOptional = productRepository.findById(productId);

            if (productOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
            }else {

                User user = userOptional.get();
                Product product = productOptional.get();

                Cart cart = user.getCart();

                // Verificar si el producto ya está en el carrito
                boolean productExists = cart.getCartItems().stream()
                        .anyMatch(cartItem -> cartItem.getProduct().getId().equals(productId));

                if (productExists) {

                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new CartItemResponse("El producto ya está en el carrito"));

                } else {
                    System.out.println("llegue aqui");
                    Cart_item cartItem = new Cart_item(cart, product, 1);
                    cart.getCartItems().add(cartItem);
                    cartItemRepository.save(cartItem);
                    return ResponseEntity.status(HttpStatus.CREATED).body(new CartItemResponse("Producto agregado al carrito"));
                }
            }
        }

    }

    @GetMapping("/api/cart/{userId}")
    public ResponseEntity<?> getCartItems(@PathVariable Long userId) {

      try{

          Optional<Cart> cart = cartRepository.findCartByUserId(userId);
            if (cart.isEmpty()) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Carrito no encontrado");
            }else {

                CartResponse response = new CartResponse(cart.get().getCartItems());

                return ResponseEntity.ok(response);
            }


      } catch (Exception e) {
          throw new RuntimeException(e);
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
