package com.example.proyectofinal.ResponseRequest;

import com.example.proyectofinal.Entity.Cart_item;

import java.util.List;

public class CartResponse {

    public List<CartItemResponse> items;

    public CartResponse(List<Cart_item> items) {
        this.items = items.stream()
                .map(CartItemResponse::new)
                .toList();
    }
}
