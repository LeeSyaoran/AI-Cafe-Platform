package com.example.backend.service;

public interface SmsService {

    void send(String phone, String message);

    void sendOtp(String phone, String otp);

    void sendOrderStatus(String phone, String orderNumber, String status);
}
