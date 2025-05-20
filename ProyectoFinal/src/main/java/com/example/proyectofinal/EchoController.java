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

    @Autowired
    private  OrderItemRepository orderItemRepository;

    @PostMapping("/api/auth/register")
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


    @PostMapping("/api/auth/login")
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




    @DeleteMapping("/api/remove/{userId}/{itemId}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long userId, @PathVariable Long itemId) {
        return cartItemRepository.findByIdAndUserId(itemId, userId).map(item -> {
            cartItemRepository.delete(item);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("el producto no fue encontrado en el carrito"));
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

    @PostMapping("/api/cart/add/{userId}/{productId}")
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


    @PostMapping("/api/order/create/{userId}")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest,@PathVariable Long userId) {
        // Obtener usuario
        User user = repositoryUser.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener carrito del usuario
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        List<Cart_item> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("El carrito está vacío");
        }

        // Calcular total
        BigDecimal total = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println(total);

        // Crear orden
        Order order = new Order(user, total, orderRequest.getShipping().getCity(), orderRequest.getShipping().getAddress(), orderRequest.getShipping().getFullName(), orderRequest.getPaymentMethod(), "paid");
        order = orderRepository.save(order);

        // Procesar cada ítem del carrito
        for (Cart_item cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            BigDecimal price = cartItem.getProduct().getPrice();
            if (product.getStock() < quantity) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }

            // Actualizar stock
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            // Guardar ítem de la orden
            Order_item orderItem = new Order_item(order,product,quantity,price);
            orderItemRepository.save(orderItem);
        }

        // Vaciar el carrito
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return ResponseEntity.ok(order);
    }


}
