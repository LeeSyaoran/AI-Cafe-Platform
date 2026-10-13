package com.example.backend.service;

public interface EmailService {

    void send(String to, String subject, String body);

    void sendOrderConfirmation(String email, String orderNumber, String orderDetails, double total);

    void sendOrderReady(String email, String orderNumber);

    void sendPaymentSuccess(String email, String orderNumber, double amount);

    void sendWelcome(String email, String name);
}
