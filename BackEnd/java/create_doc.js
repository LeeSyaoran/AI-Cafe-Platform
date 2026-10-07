const { Document, Packer, Paragraph, TextRun, HeadingLevel } = require('docx');
const fs = require('fs');

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      // Title
      new Paragraph({
        children: [new TextRun({ text: "AI CAFE PLATFORM - API DOCUMENTATION", bold: true, size: 48 })],
        heading: HeadingLevel.TITLE,
        alignment: "center"
      }),
      new Paragraph({ text: "" }),
      new Paragraph({ text: "Base URL: https://api-staging.aicafe.vn/api" }),
      new Paragraph({ text: "Production: https://api.aicafe.vn/api" }),
      new Paragraph({ text: "Local: http://localhost:3000/api" }),
      new Paragraph({ text: "================================================================================" }),

      // 1. AUTHENTICATION
      new Paragraph({ children: [new TextRun({ text: "1. AUTHENTICATION", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/auth/send-otp", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Send OTP to phone or email" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"email": "test@example.com", "phone": "0909123456", "type": "register"}', font: "Courier New" })], border: { top: { style: "single", size: 1, color: "808080" }, bottom: { style: "single", size: 1, color: "808080" }, left: { style: "single", size: 1, color: "808080" }, right: { style: "single", size: 1, color: "808080" } } }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/auth/verify-otp", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Verify OTP and get tokens" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"email": "test@example.com", "phone": "0909123456", "otp": "123456"}', font: "Courier New" })] }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/auth/register", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Register new user account" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"email": "test@example.com", "password": "password123", "phone": "0909123456"}', font: "Courier New" })] }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/auth/login", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Login with email and password" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"email": "test@example.com", "password": "password123"}', font: "Courier New" })] }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/auth/refresh", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Refresh access token" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"refreshToken": "{{refreshToken}}"}', font: "Courier New" })] }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/auth/logout", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Logout and invalidate token" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"refreshToken": "{{refreshToken}}"}', font: "Courier New" })] }),

      new Paragraph({ text: "================================================================================" }),

      // 2. USER PROFILE
      new Paragraph({ children: [new TextRun({ text: "2. USER PROFILE", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/me", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get current user profile" }),
      new Paragraph({ text: "Headers: Authorization: Bearer {{accessToken}}, X-User-ID: {{userId}}" }),

      new Paragraph({ children: [new TextRun({ text: "PUT /v1/me", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Update current user profile" }),
      new Paragraph({ text: "Headers: Authorization: Bearer {{accessToken}}, X-User-ID: {{userId}}" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"fullName": "Nguyen Van Test", "displayName": "TestUser", "dateOfBirth": "1990-01-15", "gender": "male"}', font: "Courier New" })] }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/users/{id}", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get user by ID" }),
      new Paragraph({ text: "Headers: Authorization: Bearer {{accessToken}}" }),

      new Paragraph({ text: "================================================================================" }),

      // 3. CART
      new Paragraph({ children: [new TextRun({ text: "3. CART", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/cart", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get user cart for a cafe" }),
      new Paragraph({ text: "Headers: Authorization: Bearer {{accessToken}}, X-User-ID: {{userId}}" }),
      new Paragraph({ text: "Query: cafeId=UUID" }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/cart/items", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Add item to cart" }),
      new Paragraph({ text: "Headers: Authorization: Bearer {{accessToken}}, X-User-ID: {{userId}}" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"cafeId": "REPLACE_WITH_CAFE_ID", "productId": "REPLACE_WITH_PRODUCT_ID", "quantity": 1, "options": {}, "modifiers": [], "notes": ""}', font: "Courier New" })] }),

      new Paragraph({ children: [new TextRun({ text: "PUT /v1/cart/items/{id}", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Update cart item" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"quantity": 2, "notes": "Extra ice"}', font: "Courier New" })] }),

      new Paragraph({ children: [new TextRun({ text: "DELETE /v1/cart/items/{id}", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Remove item from cart" }),

      new Paragraph({ children: [new TextRun({ text: "DELETE /v1/cart", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Clear all items from cart" }),
      new Paragraph({ text: "Query: cafeId=UUID" }),

      new Paragraph({ text: "================================================================================" }),

      // 4. ORDERS
      new Paragraph({ children: [new TextRun({ text: "4. ORDERS", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/orders", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Create new order from cart" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"companyId": "REPLACE_WITH_COMPANY_ID", "cafeId": "REPLACE_WITH_CAFE_ID", "orderType": "pickup", "customerNote": "", "promoCode": ""}', font: "Courier New" })] }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/orders", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get user order history" }),
      new Paragraph({ text: "Query: page=0, size=20" }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/orders/{id}", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get order by ID" }),

      new Paragraph({ children: [new TextRun({ text: "PUT /v1/orders/{id}/confirm", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Staff confirms order" }),

      new Paragraph({ children: [new TextRun({ text: "PUT /v1/orders/{id}/preparing", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Staff starts preparing order" }),

      new Paragraph({ children: [new TextRun({ text: "PUT /v1/orders/{id}/ready", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Staff marks order as ready" }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/orders/{id}/cancel", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Cancel order" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"reason": "Customer requested cancellation"}', font: "Courier New" })] }),

      new Paragraph({ text: "================================================================================" }),

      // 5. PAYMENTS
      new Paragraph({ children: [new TextRun({ text: "5. PAYMENTS", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/payments/methods", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get available payment methods" }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/payments", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Create payment for order" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"orderId": "REPLACE_WITH_ORDER_ID", "companyId": "REPLACE_WITH_COMPANY_ID", "amount": 50000, "paymentMethod": "CREDIT", "returnUrl": "https://aicafe.vn/payment/return"}', font: "Courier New" })] }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/payments/{id}", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get payment by ID" }),

      new Paragraph({ text: "================================================================================" }),

      // 6. LOYALTY
      new Paragraph({ children: [new TextRun({ text: "6. LOYALTY & REWARDS", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/loyalty/account", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get user loyalty account info" }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/loyalty/rewards", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get available rewards" }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/loyalty/redeem", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Redeem loyalty points" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"rewardId": "REPLACE_WITH_REWARD_ID", "orderId": "REPLACE_WITH_ORDER_ID"}', font: "Courier New" })] }),

      new Paragraph({ text: "================================================================================" }),

      // 7. SUBSCRIPTIONS
      new Paragraph({ children: [new TextRun({ text: "7. SUBSCRIPTIONS", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/subscriptions/plans", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get available membership plans" }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/subscriptions", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Subscribe to membership plan" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"planId": "REPLACE_WITH_PLAN_ID", "billingCycle": "monthly", "paymentMethod": "CREDIT"}', font: "Courier New" })] }),

      new Paragraph({ text: "================================================================================" }),

      // 8. NOTIFICATIONS
      new Paragraph({ children: [new TextRun({ text: "8. NOTIFICATIONS", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/notifications/devices", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Register device token" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"token": "device_token_here", "platform": "android"}', font: "Courier New" })] }),

      new Paragraph({ text: "================================================================================" }),

      // 9. CAFES
      new Paragraph({ children: [new TextRun({ text: "9. CAFES", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/cafes", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get list of cafes" }),
      new Paragraph({ text: "Query: companyId=UUID" }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/cafes/{id}", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get cafe by ID" }),

      new Paragraph({ text: "================================================================================" }),

      // 10. ADMIN PRODUCTS
      new Paragraph({ children: [new TextRun({ text: "10. ADMIN - PRODUCTS", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),
      new Paragraph({ text: "Headers: X-Company-ID: {{companyId}}" }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/admin/products", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get products list" }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/admin/products", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Create new product" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"categoryId": "REPLACE_WITH_CATEGORY_ID", "name": "Ca Phe Sua", "description": "Ca phe sua da dam da", "price": 35000, "originalPrice": 40000, "stock": 100}', font: "Courier New" })] }),

      new Paragraph({ text: "================================================================================" }),

      // 11. ADMIN ORDERS
      new Paragraph({ children: [new TextRun({ text: "11. ADMIN - ORDERS", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/admin/orders", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get orders for admin" }),

      new Paragraph({ children: [new TextRun({ text: "POST /v1/admin/orders/{id}/refund", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Refund an order" }),
      new Paragraph({ text: "Body:" }),
      new Paragraph({ children: [new TextRun({ text: '{"reason": "Customer request"}', font: "Courier New" })] }),

      new Paragraph({ text: "================================================================================" }),

      // 12. ADMIN DASHBOARD
      new Paragraph({ children: [new TextRun({ text: "12. ADMIN - DASHBOARD", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/admin/dashboard/overview", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get dashboard overview statistics" }),
      new Paragraph({ text: "Query: period=month (day|week|month|year)" }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/admin/dashboard/revenue-chart", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get revenue chart data" }),

      new Paragraph({ children: [new TextRun({ text: "GET /v1/admin/dashboard/top-products", bold: true })], heading: HeadingLevel.HEADING_2 }),
      new Paragraph({ text: "Description: Get top selling products" }),

      new Paragraph({ text: "================================================================================" }),

      // PLACEHOLDERS
      new Paragraph({ children: [new TextRun({ text: "PLACEHOLDERS", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),
      new Paragraph({ text: "{{accessToken}} - JWT access token (auto saved after login)" }),
      new Paragraph({ text: "{{refreshToken}} - Refresh token (auto saved after login)" }),
      new Paragraph({ text: "{{userId}} - User ID (auto saved after login)" }),
      new Paragraph({ text: "{{companyId}} - Company UUID" }),
      new Paragraph({ text: "REPLACE_WITH_CAFE_ID - Cafe UUID" }),
      new Paragraph({ text: "REPLACE_WITH_PRODUCT_ID - Product UUID" }),
      new Paragraph({ text: "REPLACE_WITH_ORDER_ID - Order UUID" }),
      new Paragraph({ text: "REPLACE_WITH_CATEGORY_ID - Category UUID" }),
      new Paragraph({ text: "REPLACE_WITH_REWARD_ID - Reward UUID" }),
      new Paragraph({ text: "REPLACE_WITH_PLAN_ID - Subscription plan UUID" }),

      new Paragraph({ text: "================================================================================" }),

      // RESPONSE FORMAT
      new Paragraph({ children: [new TextRun({ text: "RESPONSE FORMAT", bold: true, size: 32 })], heading: HeadingLevel.HEADING_1 }),
      new Paragraph({ children: [new TextRun({ text: '{"success": true, "data": {...}, "message": "Success", "timestamp": "2024-01-01T12:00:00Z"}', font: "Courier New" })] }),
    ]
  }]
});

Packer.toBuffer(doc).then(buffer => {
  fs.writeFileSync('Postman Collection/AI_Cafe_API_Documentation.docx', buffer);
  console.log('Created: Postman Collection/AI_Cafe_API_Documentation.docx');
});
