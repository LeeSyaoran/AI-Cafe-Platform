package com.example.backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender() {
        return new MockJavaMailSender();
    }

    public static class MockJavaMailSender implements JavaMailSender {
        private final Session session = Session.getInstance(new Properties());

        @Override
        public MimeMessage createMimeMessage() {
            return new MockMimeMessage();
        }

        @Override
        public MimeMessage createMimeMessage(InputStream contentStream) {
            return new MockMimeMessage();
        }

        @Override
        public void send(MimeMessage mimeMessage) {
            // Mock - no-op
        }

        @Override
        public void send(MimeMessage... mimeMessages) {
            // Mock - no-op
        }

        @Override
        public void send(SimpleMailMessage... simpleMessages) {
            // Mock - no-op
        }

        @Override
        public void send(MimeMessagePreparator mimeMessagePreparator) {
            // Mock - no-op
        }

        @Override
        public void send(MimeMessagePreparator... mimeMessagePreparators) {
            // Mock - no-op
        }
    }

    public static class MockMimeMessage extends MimeMessage {
        public MockMimeMessage() {
            super((Session) null);
        }
    }
}
