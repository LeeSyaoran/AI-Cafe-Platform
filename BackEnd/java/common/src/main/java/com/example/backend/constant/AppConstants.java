package com.example.backend.constant;

public final class AppConstants {

    // Order statuses
    public static final String ORDER_PENDING = "pending";
    public static final String ORDER_CONFIRMED = "confirmed";
    public static final String ORDER_PREPARING = "preparing";
    public static final String ORDER_READY = "ready";
    public static final String ORDER_COMPLETED = "completed";
    public static final String ORDER_CANCELLED = "cancelled";

    // Payment statuses
    public static final String PAYMENT_PENDING = "pending";
    public static final String PAYMENT_PROCESSING = "processing";
    public static final String PAYMENT_COMPLETED = "completed";
    public static final String PAYMENT_FAILED = "failed";
    public static final String PAYMENT_REFUNDED = "refunded";

    // Payment methods
    public static final String PAYMENT_VNPAY = "VNPAY";
    public static final String PAYMENT_MOMO = "MOMO";
    public static final String PAYMENT_ZALOPAY = "ZALOPAY";
    public static final String PAYMENT_CREDIT = "CREDIT";
    public static final String PAYMENT_COD = "COD";

    // Order types
    public static final String ORDER_DINE_IN = "dine_in";
    public static final String ORDER_TAKE_AWAY = "take_away";
    public static final String ORDER_DELIVERY = "delivery";

    // User roles
    public static final String ROLE_CUSTOMER = "customer";
    public static final String ROLE_STAFF = "staff";
    public static final String ROLE_MANAGER = "manager";
    public static final String ROLE_ADMIN = "admin";

    // User statuses
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    public static final String STATUS_SUSPENDED = "suspended";

    // Subscription statuses
    public static final String SUBSCRIPTION_ACTIVE = "active";
    public static final String SUBSCRIPTION_TRIAL = "trial";
    public static final String SUBSCRIPTION_PAUSED = "paused";
    public static final String SUBSCRIPTION_CANCELLED = "cancelled";
    public static final String SUBSCRIPTION_EXPIRED = "expired";

    // Loyalty tiers
    public static final String TIER_BRONZE = "bronze";
    public static final String TIER_SILVER = "silver";
    public static final String TIER_GOLD = "gold";
    public static final String TIER_PLATINUM = "platinum";

    // Billing cycles
    public static final String BILLING_MONTHLY = "monthly";
    public static final String BILLING_YEARLY = "yearly";

    // Currency
    public static final String CURRENCY_VND = "VND";
    public static final String CURRENCY_USD = "USD";

    // Pagination defaults
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Cache TTL (in seconds)
    public static final long CACHE_TTL_SHORT = 300; // 5 minutes
    public static final long CACHE_TTL_MEDIUM = 3600; // 1 hour
    public static final long CACHE_TTL_LONG = 86400; // 1 day

    private AppConstants() {}
}
