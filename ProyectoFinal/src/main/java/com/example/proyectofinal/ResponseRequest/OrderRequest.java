package com.example.proyectofinal.ResponseRequest;


public class OrderRequest {
    private ShippingInfo shipping;
    private String paymentMethod;

    // Getters y setters

    public ShippingInfo getShipping() {
        return shipping;
    }

    public void setShipping(ShippingInfo shipping) {
        this.shipping = shipping;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public static class ShippingInfo {
        private String fullName;
        private String address;
        private String city;

        // Getters y setters
        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }
}

