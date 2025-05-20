package com.example.proyectofinal.ResponseRequest;

import com.example.proyectofinal.Entity.Cart_item;

import java.math.BigDecimal;

public class CartItemResponse {

    private Long id;
    private String name;
    private int quantity;
    private BigDecimal price;

    public CartItemResponse(Cart_item item) {
        this.id = item.getId();
        this.name = item.getProduct().getName();
        this.price = item.getProduct().getPrice();
        this.quantity = item.getQuantity();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    private String message;



    public CartItemResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
