package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@aicafe.vn");

            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendOrderConfirmation(String email, String orderNumber, String orderDetails, double total) {
        String subject = "Xác nhận đơn hàng #" + orderNumber;
        String body = String.format("""
            Xin chào!

            Cảm ơn bạn đã đặt hàng tại AI Café.

            Mã đơn hàng: %s
            Chi tiết: %s
            Tổng cộng: %,.0f VND

            Chúng tôi sẽ thông báo cho bạn khi đơn hàng được xác nhận và chuẩn bị xong.

            Trân trọng,
            AI Café Team
            """, orderNumber, orderDetails, total);

        sendEmail(email, subject, body);
    }

    @Async
    public void sendOrderReady(String email, String orderNumber) {
        String subject = "Đơn hàng #" + orderNumber + " đã sẵn sàng!";
        String body = String.format("""
            Xin chào!

            Đơn hàng #%s của bạn đã được chuẩn bị xong và sẵn sàng để nhận.

            Cảm ơn bạn đã sử dụng dịch vụ của AI Café!

            Trân trọng,
            AI Café Team
            """, orderNumber);

        sendEmail(email, subject, body);
    }

    @Async
    public void sendPaymentSuccess(String email, String orderNumber, double amount) {
        String subject = "Thanh toán thành công - Đơn hàng #" + orderNumber;
        String body = String.format("""
            Xin chào!

            Thanh toán cho đơn hàng #%s đã được xử lý thành công.

            Số tiền: %,.0f VND

            Trân trọng,
            AI Café Team
            """, orderNumber, amount);

        sendEmail(email, subject, body);
    }

    @Async
    public void sendWelcomeEmail(String email, String name) {
        String subject = "Chào mừng đến với AI Café!";
        String body = String.format("""
            Xin chào %s!

            Cảm ơn bạn đã đăng ký tài khoản tại AI Café - nền tảng đặt đồ uống thông minh đầu tiên tại Việt Nam.

            Với AI Café, bạn có thể:
            • Đặt đồ uống yêu thích một cách nhanh chóng
            • Nhận gợi ý cá nhân hóa từ AI
            • Tích lũy điểm thưởng và đổi quà hấp dẫn

            Chúc bạn có những trải nghiệm tuyệt vời!

            Trân trọng,
            AI Café Team
            """, name != null ? name : "bạn");

        sendEmail(email, subject, body);
    }
}
