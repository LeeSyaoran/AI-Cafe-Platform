package com.example.backend.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class IdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String orderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = RANDOM.nextInt(1000000);
        return "ORD" + date + String.format("%06d", random);
    }

    public static String invoiceNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        int random = RANDOM.nextInt(1000000);
        return "INV" + date + String.format("%06d", random);
    }

    public static String shortId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String verificationCode() {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }

    public static String formatDate(Instant instant) {
        if (instant == null) return null;
        return instant.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String formatDateTime(Instant instant) {
        if (instant == null) return null;
        return instant.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}