# AI Café Platform - API Design

## Base URL
```
Production: https://api.aicafe.vn/v1
Staging: https://api-staging.aicafe.vn/v1
Local: http://localhost:3000/v1
```

## Authentication
```
Authorization: Bearer <access_token>
X-Company-ID: <company_uuid>
X-Cafe-ID: <cafe_uuid> (optional, for cafe-scoped requests)
```

---

## Table of Contents

1. [Auth](#auth)
2. [Users](#users)
3. [Companies](#companies)
4. [Cafes](#cafes)
5. [Products](#products)
6. [Orders](#orders)
7. [Payments](#payments)
8. [Cart](#cart)
9. [AI Chat](#ai-chat)
10. [Credits](#credits)
11. [Customers](#customers)
12. [Loyalty](#loyalty)
13. [Reviews](#reviews)
14. [Inventory](#inventory)
15. [Promotions](#promotions)

---

## Auth

### POST /auth/register
Register a new user.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "phone": "+84909090001",
  "fullName": "Nguyễn Văn A",
  "companyCode": "AICA"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "uuid",
      "email": "user@example.com",
      "role": "customer",
      "status": "active"
    },
    "tokens": {
      "accessToken": "eyJ...",
      "refreshToken": "eyJ...",
      "expiresIn": 3600
    }
  }
}
```

### POST /auth/login
User login.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "deviceInfo": {
    "deviceType": "mobile",
    "deviceName": "iPhone 15 Pro"
  }
}
```

### POST /auth/refresh
Refresh access token.

### POST /auth/logout
Logout and invalidate tokens.

### POST /auth/forgot-password
Request password reset.

### POST /auth/reset-password
Reset password with token.

### POST /auth/verify-email
Verify email address.

### POST /auth/verify-phone
Verify phone number with OTP.

---

## Users

### GET /users/me
Get current user profile.

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "phone": "+84909090001",
    "role": "customer",
    "profile": {
      "fullName": "Nguyễn Văn A",
      "displayName": "VanA",
      "avatarUrl": "https://...",
      "dateOfBirth": "1995-01-15",
      "language": "vi",
      "timezone": "Asia/Ho_Chi_Minh"
    },
    "creditBalance": 1500,
    "loyaltyPoints": 250
  }
}
```

### PATCH /users/me
Update current user profile.

### PATCH /users/me/password
Change password.

### GET /users/me/devices
List logged-in devices.

### DELETE /users/me/devices/:deviceId
Remove a device session.

### GET /users/me/notifications
List user notifications.

### PATCH /users/me/notifications/:id/read
Mark notification as read.

---

## Companies

### GET /companies
List companies (admin only).

### GET /companies/:id
Get company details.

### PATCH /companies/:id
Update company.

### GET /companies/:id/cafes
List cafes under company.

### GET /companies/:id/analytics
Get company analytics.

---

## Cafes

### GET /cafes
List cafes with filters.

**Query Parameters:**
- `latitude` - User latitude
- `longitude` - User longitude
- `radius` - Search radius in km (default: 10)
- `isDeliveryEnabled` - Filter by delivery
- `isPickupEnabled` - Filter by pickup
- `isReservationEnabled` - Filter by reservation

**Response:**
```json
{
  "success": true,
  "data": {
    "cafes": [
      {
        "id": "uuid",
        "name": "AI Café Nguyễn Huệ",
        "slug": "aica-nguyen-hue",
        "address": "123 Nguyễn Huệ, Q1, HCM",
        "distance": 0.5,
        "openingHours": {...},
        "isOpen": true,
        "rating": 4.5,
        "reviewCount": 128
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 20,
      "total": 45,
      "totalPages": 3
    }
  }
}
```

### GET /cafes/:id
Get cafe details with full info.

### GET /cafes/:id/menu
Get cafe menu (categories + products).

### GET /cafes/:id/zones
Get seating zones.

### GET /cafes/:id/seats
Get available seats.

### POST /cafes/:id/reservations
Create a reservation.

### GET /cafes/:id/reservations
List reservations.

---

## Products

### GET /products
List products with filters.

**Query Parameters:**
- `categoryId` - Filter by category
- `cafeId` - Filter by cafe
- `search` - Search by name
- `isFeatured` - Featured items only
- `isBestSeller` - Best sellers only
- `tags` - Filter by tags (comma-separated)
- `minPrice` - Minimum price
- `maxPrice` - Maximum price

### GET /products/:id
Get product details with options, modifiers, allergens.

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "name": "Cappuccino",
    "nameVi": "Cappuccino",
    "price": 45000,
    "description": "Rich espresso with steamed milk...",
    "images": ["https://..."],
    "calories": 120,
    "preparationTime": 5,
    "category": {
      "id": "uuid",
      "name": "Coffee"
    },
    "options": [
      {
        "id": "uuid",
        "name": "Size",
        "type": "size",
        "required": true,
        "values": [
          {"id": "uuid", "name": "S", "priceAdjustment": -5000},
          {"id": "uuid", "name": "M", "priceAdjustment": 0, "isDefault": true},
          {"id": "uuid", "name": "L", "priceAdjustment": 8000}
        ]
      },
      {
        "id": "uuid",
        "name": "Sugar",
        "type": "sugar",
        "required": true,
        "values": [
          {"id": "uuid", "name": "No Sugar", "priceAdjustment": 0},
          {"id": "uuid", "name": "50%", "priceAdjustment": 0},
          {"id": "uuid", "name": "100%", "priceAdjustment": 0, "isDefault": true}
        ]
      }
    ],
    "modifiers": [
      {
        "id": "uuid",
        "name": "Extra Espresso Shot",
        "nameVi": "Thêm Shot Espresso",
        "price": 12000
      },
      {
        "id": "uuid",
        "name": "Whipped Cream",
        "nameVi": "Kem Whipped",
        "price": 5000
      }
    ],
    "allergens": [
      {"id": "uuid", "name": "Dairy", "icon": "🥛"},
      {"id": "uuid", "name": "Gluten", "icon": "🌾"}
    ],
    "nutritionalInfo": {
      "servingSize": 240,
      "calories": 120,
      "protein": 6,
      "carbohydrates": 12,
      "fat": 4
    }
  }
}
```

### GET /products/:id/availability
Check product availability at specific cafe/time.

### GET /products/:id/reviews
Get product reviews.

---

## Orders

### POST /orders
Create a new order.

**Request:**
```json
{
  "cafeId": "uuid",
  "orderType": "dine_in",
  "seatId": "uuid",
  "items": [
    {
      "productId": "uuid",
      "quantity": 2,
      "variantId": "uuid",
      "options": [
        {"optionId": "uuid", "valueId": "uuid"},
        {"optionId": "uuid", "valueId": "uuid"}
      ],
      "modifiers": [
        {"id": "uuid", "quantity": 1}
      ],
      "notes": "Less ice please"
    }
  ],
  "customerNote": "Birthday celebration",
  "promotionCode": "AICA20"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "data": {
    "order": {
      "id": "uuid",
      "orderNumber": "AICA-HCM-001-20240115-0001",
      "status": "pending",
      "kitchenStatus": "pending",
      "items": [...],
      "subtotal": 98000,
      "discountAmount": 19600,
      "taxAmount": 7840,
      "totalAmount": 86240,
      "estimatedReadyAt": "2024-01-15T10:30:00Z",
      "qrCode": "https://..."
    }
  }
}
```

### GET /orders
List orders for user/company.

**Query Parameters:**
- `status` - Filter by status
- `orderType` - dine_in, takeaway, delivery
- `cafeId` - Filter by cafe
- `fromDate` - Start date
- `toDate` - End date
- `page`, `limit` - Pagination

### GET /orders/:id
Get order details.

### PATCH /orders/:id/status
Update order status.

**Request:**
```json
{
  "status": "preparing",
  "note": "Started cooking"
}
```

### PATCH /orders/:id/cancel
Cancel order.

### POST /orders/:id/reorder
Reorder previous order.

---

## Payments

### POST /payments
Process payment for order.

**Request:**
```json
{
  "orderId": "uuid",
  "paymentMethod": "vnpay",
  "amount": 86240
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "payment": {
      "id": "uuid",
      "status": "pending",
      "checkoutUrl": "https://vnpay.vn/...",
      "qrCode": "..."
    }
  }
}
```

### POST /payments/callback
Payment provider callback (VNPay, Stripe).

### POST /payments/:id/refund
Process refund.

### GET /payments/:id
Get payment details.

---

## Cart

### GET /cart
Get current user's cart.

### POST /cart/items
Add item to cart.

**Request:**
```json
{
  "cafeId": "uuid",
  "productId": "uuid",
  "quantity": 1,
  "variantId": "uuid",
  "options": [
    {"optionId": "uuid", "valueId": "uuid"}
  ],
  "modifiers": [
    {"id": "uuid", "quantity": 1}
  ],
  "notes": "Extra hot"
}
```

### PATCH /cart/items/:id
Update cart item.

### DELETE /cart/items/:id
Remove item from cart.

### DELETE /cart
Clear cart.

### POST /cart/apply-promo
Apply promotion code.

### POST /cart/checkout
Convert cart to order.

---

## AI Chat

### GET /ai/models
List available AI models.

### POST /ai/sessions
Create new chat session.

**Request:**
```json
{
  "modelId": "gpt-4o",
  "title": "Coffee Recommendations",
  "sessionType": "chat"
}
```

### GET /ai/sessions
List user's chat sessions.

### GET /ai/sessions/:id
Get session with messages.

### PATCH /ai/sessions/:id
Update session (title, favorite, share).

### DELETE /ai/sessions/:id
Delete session.

### POST /ai/sessions/:id/messages
Send message to AI.

**Request:**
```json
{
  "content": "What coffee would you recommend for someone who likes sweet drinks?",
  "attachments": []
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "message": {
      "id": "uuid",
      "role": "user",
      "content": "What coffee would you recommend...",
      "createdAt": "2024-01-15T10:00:00Z"
    },
    "aiMessage": {
      "id": "uuid",
      "role": "assistant",
      "content": "Based on your preference for sweet drinks, I would recommend:\n\n1. **Caramel Latte** - A perfect blend of espresso with steamed milk and sweet caramel drizzle.\n\n2. **Vanilla Cappuccino** - ...",
      "createdAt": "2024-01-15T10:00:05Z",
      "usage": {
        "inputTokens": 45,
        "outputTokens": 256,
        "totalTokens": 301,
        "cost": 0.0023
      }
    },
    "session": {
      "messageCount": 2,
      "totalTokens": 602,
      "creditBalance": 1497.77
    }
  }
}
```

### POST /ai/sessions/:id/share
Generate share link for session.

---

## Credits

### GET /credits
Get current credit balance.

### GET /credits/transactions
List credit transactions.

### POST /credits/purchase
Purchase credits.

**Request:**
```json
{
  "planId": "uuid",
  "paymentMethod": "vnpay"
}
```

### GET /credits/plans
List credit purchase plans.

---

## Customers

### GET /customers
List customers (admin/staff).

### GET /customers/:id
Get customer profile with order history.

### PATCH /customers/:id
Update customer.

### GET /customers/:id/orders
Get customer's order history.

### GET /customers/:id/loyalty
Get customer's loyalty details.

---

## Loyalty

### GET /loyalty/tiers
List loyalty tiers.

### GET /loyalty/points
Get user's loyalty points.

### GET /loyalty/points/transactions
List loyalty points transactions.

### POST /loyalty/redeem
Redeem points for reward.

---

## Reviews

### POST /reviews
Create a review.

**Request:**
```json
{
  "cafeId": "uuid",
  "productId": "uuid",
  "orderId": "uuid",
  "rating": 5,
  "title": "Great coffee!",
  "content": "The cappuccino was amazing...",
  "images": ["https://..."]
}
```

### GET /reviews
List reviews with filters.

### PATCH /reviews/:id
Update review (owner only).

### DELETE /reviews/:id
Delete review (owner only).

---

## Inventory

### GET /ingredients
List ingredients.

### GET /ingredients/:id
Get ingredient details.

### GET /ingredients/:id/stock
Get stock levels at cafes.

### POST /ingredients/:id/adjust-stock
Adjust stock quantity.

### GET /suppliers
List suppliers.

### POST /suppliers
Create supplier.

### GET /purchase-orders
List purchase orders.

### POST /purchase-orders
Create purchase order.

### PATCH /purchase-orders/:id/status
Update PO status (approve, receive, complete).

---

## Promotions

### GET /promotions
List active promotions.

### GET /promotions/:id
Get promotion details.

### POST /promotions/validate
Validate promotion code.

---

## Webhooks

### POST /webhooks/orders
Order status change webhook.

### POST /webhooks/payments
Payment status webhook.

---

## Error Responses

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": [
      {"field": "email", "message": "Invalid email format"},
      {"field": "password", "message": "Must be at least 8 characters"}
    ]
  }
}
```

### Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| VALIDATION_ERROR | 400 | Invalid request data |
| UNAUTHORIZED | 401 | Not authenticated |
| FORBIDDEN | 403 | Insufficient permissions |
| NOT_FOUND | 404 | Resource not found |
| CONFLICT | 409 | Resource conflict |
| OUT_OF_STOCK | 422 | Product out of stock |
| INSUFFICIENT_CREDITS | 422 | Not enough credits |
| INTERNAL_ERROR | 500 | Server error |

---

## Rate Limits

| Endpoint | Limit |
|----------|-------|
| POST /auth/login | 10/min |
| POST /orders | 30/min |
| POST /ai/sessions/:id/messages | 20/min |
| GET /products | 100/min |
| All others | 1000/min |

---

## Pagination

All list endpoints support:

```
?page=1&limit=20
```

Response includes:
```json
{
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 150,
    "totalPages": 8,
    "hasNext": true,
    "hasPrev": false
  }
}
```
