# Convert HTML to DOCX using python-docx
import re
from docx import Document
from docx.shared import Pt, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH

doc = Document()

# Title
title = doc.add_heading('AI CAFE PLATFORM - API DOCUMENTATION', 0)
title.alignment = WD_ALIGN_PARAGRAPH.CENTER

doc.add_paragraph('Base URL: https://api-staging.aicafe.vn/api')
doc.add_paragraph('Production: https://api.aicafe.vn/api')
doc.add_paragraph('Local: http://localhost:3000/api')
doc.add_paragraph('=' * 80)

# Function to add API endpoint
def add_api(method, path, desc='', body=''):
    # Method + Path
    p = doc.add_paragraph()
    run = p.add_run(f'{method} ')
    run.bold = True
    run.font.color.rgb = None
    if method == 'POST':
        run.font.color.rgb = None
    p.add_run(path)

    # Description
    if desc:
        doc.add_paragraph(f'Description: {desc}')

    # Body
    if body:
        p = doc.add_paragraph('Body (JSON):')
        code_p = doc.add_paragraph(body)
        code_p.style = 'Quote'

# 1. AUTHENTICATION
doc.add_heading('1. AUTHENTICATION', level=1)
add_api('POST', '/v1/auth/send-otp', 'Send OTP to phone or email', '{"email": "test@example.com", "phone": "0909123456", "type": "register"}')
add_api('POST', '/v1/auth/verify-otp', 'Verify OTP and get tokens', '{"email": "test@example.com", "phone": "0909123456", "otp": "123456"}')
add_api('POST', '/v1/auth/register', 'Register new user account', '{"email": "test@example.com", "password": "password123", "phone": "0909123456"}')
add_api('POST', '/v1/auth/login', 'Login with email and password', '{"email": "test@example.com", "password": "password123"}')
add_api('POST', '/v1/auth/refresh', 'Refresh access token', '{"refreshToken": "{{refreshToken}}"}')
add_api('POST', '/v1/auth/logout', 'Logout and invalidate token', '{"refreshToken": "{{refreshToken}}"}')
doc.add_paragraph('=' * 80)

# 2. USER PROFILE
doc.add_heading('2. USER PROFILE', level=1)
add_api('GET', '/v1/me', 'Get current user profile')
doc.add_paragraph('Headers: Authorization: Bearer {{accessToken}}, X-User-ID: {{userId}}')
add_api('PUT', '/v1/me', 'Update current user profile', '{"fullName": "Nguyen Van Test", "displayName": "TestUser", "dateOfBirth": "1990-01-15", "gender": "male"}')
add_api('GET', '/v1/users/{id}', 'Get user by ID')
doc.add_paragraph('=' * 80)

# 3. CART
doc.add_heading('3. CART', level=1)
add_api('GET', '/v1/cart', 'Get user cart for a cafe')
doc.add_paragraph('Headers: Authorization: Bearer {{accessToken}}, X-User-ID: {{userId}}')
doc.add_paragraph('Query: cafeId=UUID')
add_api('POST', '/v1/cart/items', 'Add item to cart', '{"cafeId": "REPLACE_WITH_CAFE_ID", "productId": "REPLACE_WITH_PRODUCT_ID", "quantity": 1, "options": {}, "modifiers": [], "notes": ""}')
add_api('PUT', '/v1/cart/items/{id}', 'Update cart item', '{"quantity": 2, "notes": "Extra ice"}')
add_api('DELETE', '/v1/cart/items/{id}', 'Remove item from cart')
add_api('DELETE', '/v1/cart', 'Clear all items from cart')
doc.add_paragraph('Query: cafeId=UUID')
doc.add_paragraph('=' * 80)

# 4. ORDERS
doc.add_heading('4. ORDERS', level=1)
add_api('POST', '/v1/orders', 'Create new order from cart', '{"companyId": "REPLACE_WITH_COMPANY_ID", "cafeId": "REPLACE_WITH_CAFE_ID", "orderType": "pickup", "customerNote": "", "promoCode": ""}')
add_api('GET', '/v1/orders', 'Get user order history')
doc.add_paragraph('Query: page=0, size=20')
add_api('GET', '/v1/orders/{id}', 'Get order by ID')
add_api('GET', '/v1/orders/number/{orderNumber}', 'Get order by order number')
add_api('PUT', '/v1/orders/{id}/confirm', 'Staff confirms order')
add_api('PUT', '/v1/orders/{id}/preparing', 'Staff starts preparing order')
add_api('PUT', '/v1/orders/{id}/ready', 'Staff marks order as ready')
add_api('PUT', '/v1/orders/{id}/complete', 'Staff completes order')
add_api('POST', '/v1/orders/{id}/cancel', 'Cancel order', '{"reason": "Customer requested cancellation"}')
doc.add_paragraph('=' * 80)

# 5. PAYMENTS
doc.add_heading('5. PAYMENTS', level=1)
add_api('GET', '/v1/payments/methods', 'Get available payment methods')
add_api('POST', '/v1/payments', 'Create payment for order', '{"orderId": "REPLACE_WITH_ORDER_ID", "companyId": "REPLACE_WITH_COMPANY_ID", "amount": 50000, "paymentMethod": "CREDIT", "returnUrl": "https://aicafe.vn/payment/return"}')
add_api('GET', '/v1/payments/{id}', 'Get payment by ID')
add_api('GET', '/v1/payments/order/{orderId}', 'Get payment by order ID')
add_api('POST', '/v1/payments/{id}/cancel', 'Cancel pending payment')
doc.add_paragraph('=' * 80)

# 6. LOYALTY
doc.add_heading('6. LOYALTY & REWARDS', level=1)
add_api('GET', '/v1/loyalty/account', 'Get user loyalty account info')
add_api('GET', '/v1/loyalty/rewards', 'Get available rewards')
add_api('POST', '/v1/loyalty/redeem', 'Redeem loyalty points', '{"rewardId": "REPLACE_WITH_REWARD_ID", "orderId": "REPLACE_WITH_ORDER_ID"}')
add_api('GET', '/v1/loyalty/history', 'Get loyalty transaction history')
add_api('GET', '/v1/loyalty/redemptions', 'Get reward redemption history')
doc.add_paragraph('=' * 80)

# 7. SUBSCRIPTIONS
doc.add_heading('7. SUBSCRIPTIONS', level=1)
add_api('GET', '/v1/subscriptions/plans', 'Get available membership plans')
add_api('GET', '/v1/subscriptions/plans/{id}', 'Get subscription plan details')
add_api('GET', '/v1/subscriptions/me', 'Get user current subscription')
add_api('POST', '/v1/subscriptions', 'Subscribe to membership plan', '{"planId": "REPLACE_WITH_PLAN_ID", "billingCycle": "monthly", "paymentMethod": "CREDIT"}')
add_api('POST', '/v1/subscriptions/cancel', 'Cancel subscription', '{"reason": "No longer needed"}')
add_api('POST', '/v1/subscriptions/pause', 'Pause subscription')
add_api('POST', '/v1/subscriptions/resume', 'Resume subscription')
doc.add_paragraph('=' * 80)

# 8. NOTIFICATIONS
doc.add_heading('8. NOTIFICATIONS', level=1)
add_api('POST', '/v1/notifications/devices', 'Register device token', '{"token": "device_token", "platform": "android"}')
add_api('DELETE', '/v1/notifications/devices/{token}', 'Remove device token')
add_api('POST', '/v1/notifications/test', 'Send test notification')
doc.add_paragraph('=' * 80)

# 9. CAFES
doc.add_heading('9. CAFES', level=1)
add_api('GET', '/v1/cafes', 'Get list of cafes')
doc.add_paragraph('Query: companyId=UUID')
add_api('GET', '/v1/cafes/{id}', 'Get cafe by ID')
doc.add_paragraph('=' * 80)

# 10. ADMIN PRODUCTS
doc.add_heading('10. ADMIN - PRODUCTS', level=1)
doc.add_paragraph('Headers: X-Company-ID: {{companyId}}')
add_api('GET', '/v1/admin/products', 'Get products list')
add_api('POST', '/v1/admin/products', 'Create new product', '{"categoryId": "REPLACE_WITH_CATEGORY_ID", "name": "Ca Phe Sua", "price": 35000, "stock": 100}')
add_api('PUT', '/v1/admin/products/{id}', 'Update product')
add_api('DELETE', '/v1/admin/products/{id}', 'Delete product')
add_api('GET', '/v1/admin/products/categories', 'Get categories')
add_api('POST', '/v1/admin/products/categories', 'Create category', '{"name": "Ca Phe", "icon": "coffee", "color": "#8B4513"}')
add_api('GET', '/v1/admin/products/promotions', 'Get promotions')
add_api('POST', '/v1/admin/products/promotions', 'Create promotion', '{"code": "SUMMER2024", "discountType": "percentage", "discountValue": 20}')
add_api('GET', '/v1/admin/products/rewards', 'Get loyalty rewards')
add_api('POST', '/v1/admin/products/rewards', 'Create reward', '{"code": "FREESHIP", "pointsCost": 100}')
doc.add_paragraph('=' * 80)

# 11. ADMIN ORDERS
doc.add_heading('11. ADMIN - ORDERS', level=1)
add_api('GET', '/v1/admin/orders', 'Get orders for admin')
add_api('GET', '/v1/admin/orders/{id}', 'Get order details')
add_api('PUT', '/v1/admin/orders/{id}/status', 'Update order status', '{"status": "preparing"}')
add_api('PUT', '/v1/admin/orders/{id}/assign', 'Assign order', '{"staffId": "ID", "cafeId": "ID"}')
add_api('PUT', '/v1/admin/orders/{id}/note', 'Update note', '{"note": "VIP Customer"}')
add_api('POST', '/v1/admin/orders/{id}/cancel', 'Cancel order', '{"reason": "Out of stock"}')
add_api('POST', '/v1/admin/orders/{id}/refund', 'Refund order', '{"reason": "Customer request"}')
doc.add_paragraph('=' * 80)

# 12. ADMIN DASHBOARD
doc.add_heading('12. ADMIN - DASHBOARD', level=1)
add_api('GET', '/v1/admin/dashboard/overview', 'Dashboard overview')
doc.add_paragraph('Query: period=month (day|week|month|year)')
add_api('GET', '/v1/admin/dashboard/revenue-chart', 'Revenue chart')
add_api('GET', '/v1/admin/dashboard/top-products', 'Top selling products')
add_api('GET', '/v1/admin/dashboard/quick-stats', 'Quick statistics')
doc.add_paragraph('=' * 80)

# PLACEHOLDERS
doc.add_heading('PLACEHOLDERS', level=1)
table = doc.add_table(rows=11, cols=2)
table.style = 'Table Grid'
hdr = table.rows[0].cells
hdr[0].text = 'Placeholder'
hdr[1].text = 'Description'
data = [
    ('{{accessToken}}', 'JWT access token'),
    ('{{refreshToken}}', 'Refresh token'),
    ('{{userId}}', 'User ID'),
    ('{{companyId}}', 'Company UUID'),
    ('REPLACE_WITH_CAFE_ID', 'Cafe UUID'),
    ('REPLACE_WITH_PRODUCT_ID', 'Product UUID'),
    ('REPLACE_WITH_ORDER_ID', 'Order UUID'),
    ('REPLACE_WITH_CATEGORY_ID', 'Category UUID'),
    ('REPLACE_WITH_REWARD_ID', 'Reward UUID'),
    ('REPLACE_WITH_PLAN_ID', 'Plan UUID'),
]
for i, (p, d) in enumerate(data, 1):
    table.rows[i].cells[0].text = p
    table.rows[i].cells[1].text = d

doc.add_paragraph()
doc.add_heading('RESPONSE FORMAT', level=1)
p = doc.add_paragraph('{"success": true, "data": {...}, "message": "Success", "timestamp": "2024-01-01T12:00:00Z"}')
p.style = 'Quote'

# Save
doc.save('D:/project code/AI Cafe Platform/BackEnd/java/Postman Collection/AI_Cafe_API_Documentation.docx')
print('Created: AI_Cafe_API_Documentation.docx')
