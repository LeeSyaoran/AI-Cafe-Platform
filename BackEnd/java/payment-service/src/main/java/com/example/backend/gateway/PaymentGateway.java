package com.example.backend.gateway;

import com.example.backend.entity.Payment;

/**
 * Payment gateway interface for different payment providers.
 */
public interface PaymentGateway {

    /**
     * Create payment URL for the given payment.
     * @param payment the payment entity
     * @return payment URL to redirect user
     */
    String createPaymentUrl(Payment payment);

    /**
     * Verify callback from payment provider and update payment status.
     * @param callbackData callback data from provider
     * @return updated payment entity
     */
    Payment verifyCallback(String callbackData);
}
