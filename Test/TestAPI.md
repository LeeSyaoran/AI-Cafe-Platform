# 🧪 DỮ LIỆU ĐẦU VÀO VÀ KẾT QUẢ TEST API (PORT 3000)

> **Dự án**: AI Café Platform  
> **Điểm truy cập duy nhất**: `http://localhost:3000`  
> **Cơ chế**: API Gateway (Go Fiber) nhận yêu cầu tại cổng 3000 và chuyển tiếp tới 7 Java Microservices  
> **Ngày cập nhật**: 12/10/2026  

---

## 🔑 1. BỘ DỮ LIỆU ĐỊNH DANH MẪU (CORE SEED DATA)

Dưới đây là các ID thực tế đã có sẵn trong cơ sở dữ liệu PostgreSQL để phục vụ test:

* **Tài khoản quản trị viên (Admin Account)**:
  * **User ID**: `00000000-0000-0000-0000-000000000001`
  * **Email**: `admin@aicafe.com` *(hoặc `admin@aicafe.vn`)*
  * **Mật khẩu**: `SecurePass123!`
  * **Role**: `admin`
* **Tài khoản nhân viên (Staff Account)**:
  * **User ID**: `00000000-0000-0000-0000-000000000002`
  * **Email**: `staff@aicafe.com`
  * **Mật khẩu**: `SecurePass123!`
  * **Role**: `staff`
* **Tài khoản khách hàng (User Account)**:
  * **User ID**: `7b9f8522-ee0b-4bcb-a0bd-4546f878a45b`
  * **Email**: `customer@aicafe.com`
  * **Mật khẩu**: `SecurePass123!`
  * **Họ tên**: `Nguyễn Văn A`
  * **Số điện thoại**: `0912345678`
* **Công ty (Company ID)**: `00000000-0000-0000-0000-000000000001`
* **Quán Cafe (Cafe ID)**: `00000000-0000-0000-0000-000000000010` *(AI Café Quận 1)*
* **Sản phẩm mẫu (Product ID)**: `00000000-0000-0000-0000-000000000030` *(Cà phê Đen)*
* **Đơn hàng mẫu (Order ID)**: `00000000-0000-0000-0000-000000000060`

---

## 📋 2. CHI TIẾT INPUT & OUTPUT CỦA TỪNG API

### 1. Đăng nhập hệ thống (User Service)
* **Method**: `POST`
* **URL**: `http://localhost:3000/v1/auth/login`
* **Headers**:
  ```http
  Content-Type: application/json
  ```
* **Request Body (JSON)**:
  ```json
  {
    "email": "customer@aicafe.com",
    "password": "SecurePass123!"
  }
  ```
* **Dữ liệu trả về (Output)**:
  ```json
  {
    "success": true,
    "data": {
      "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
      "refreshToken": "f2a4c36e-b167-463d-bc59-4ce2311275b3",
      "tokenType": "Bearer",
      "expiresIn": 3600000,
      "user": {
        "id": "7b9f8522-ee0b-4bcb-a0bd-4546f878a45b",
        "email": "customer@aicafe.com",
        "phone": "0912345678",
        "role": "customer",
        "status": "active"
      }
    }
  }
  ```

---

### 2. Xem hồ sơ người dùng (User Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/me`
* **Headers**:
  ```http
  X-User-ID: 7b9f8522-ee0b-4bcb-a0bd-4546f878a45b
  ```
* **Dữ liệu trả về (Output)**:
  ```json
  {
    "success": true,
    "data": {
      "id": "7b9f8522-ee0b-4bcb-a0bd-4546f878a45b",
      "email": "customer@aicafe.com",
      "profile": {
        "fullName": "Nguyen Van A",
        "displayName": "Nguyen Van A",
        "language": "vi",
        "timezone": "Asia/Ho_Chi_Minh"
      }
    }
  }
  ```

---

### 3. Danh sách quán Cafe (Cafe Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/cafes`
* **Headers**: Không cần
* **Dữ liệu trả về (Output)**:
  ```json
  {
    "success": true,
    "data": [
      {
        "id": "00000000-0000-0000-0000-000000000010",
        "name": "AI Café Quận 1",
        "slug": "ai-cafe-quan-1",
        "address": "123 Nguyễn Huệ, Phường Bến Nghé, Quận 1",
        "status": "open"
      }
    ]
  }
  ```

---

### 4. Danh sách bàn theo quán (Cafe Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/cafes/00000000-0000-0000-0000-000000000010/seats`
* **Headers**: Không cần
* **Path Param**: `cafeId = 00000000-0000-0000-0000-000000000010`
* **Dữ liệu trả về (Output)**: Danh sách bàn ghế và trạng thái khả dụng.

---

### 5. Menu Đồ uống & Món ăn (Order Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/products?companyId=00000000-0000-0000-0000-000000000001`
* **Query Param**: `companyId = 00000000-0000-0000-0000-000000000001`
* **Headers**: Không cần
* **Dữ liệu trả về (Output)**:
  ```json
  {
    "success": true,
    "data": [
      { "id": "00000000-0000-0000-0000-000000000030", "name": "Cà Phê Đen", "price": 29000.0, "stock": 100, "isBestSeller": true },
      { "id": "00000000-0000-0000-0000-000000000031", "name": "Cà Phê Sữa", "price": 35000.0, "stock": 100, "isBestSeller": true },
      { "id": "00000000-0000-0000-0000-000000000032", "name": "Cappuccino", "price": 45000.0, "stock": 80,  "isBestSeller": false },
      { "id": "00000000-0000-0000-0000-000000000033", "name": "Latte",      "price": 48000.0, "stock": 80,  "isBestSeller": false },
      { "id": "00000000-0000-0000-0000-000000000034", "name": "Trà Đá",     "price": 20000.0, "stock": 200, "isBestSeller": false },
      { "id": "00000000-0000-0000-0000-000000000035", "name": "Trà Sữa Trân Châu", "price": 35000.0, "stock": 150, "isBestSeller": true },
      { "id": "00000000-0000-0000-0000-00000000003c", "name": "Croissant",  "price": 35000.0, "stock": 40,  "isBestSeller": true }
    ]
  }
  ```

---

### 6. Xem chi tiết giỏ hàng (Order Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/cart?cafeId=00000000-0000-0000-0000-000000000010`
* **Query Param**: `cafeId = 00000000-0000-0000-0000-000000000010`
* **Headers**:
  ```http
  X-User-ID: 7b9f8522-ee0b-4bcb-a0bd-4546f878a45b
  ```
* **Dữ liệu trả về (Output)**: Thông tin giỏ hàng và danh sách món đã chọn.

---

### 7. Thêm món vào giỏ hàng (Order Service)
* **Method**: `POST`
* **URL**: `http://localhost:3000/v1/cart/items`
* **Headers**:
  ```http
  X-User-ID: 7b9f8522-ee0b-4bcb-a0bd-4546f878a45b
  Content-Type: application/json
  ```
* **Request Body (JSON)**:
  ```json
  {
    "productId": "00000000-0000-0000-0000-000000000030",
    "quantity": 2,
    "note": "Ít đá, ít ngọt"
  }
  ```

---

### 8. Lịch sử đơn hàng của tôi (Order Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/orders`
* **Headers**:
  ```http
  X-User-ID: 7b9f8522-ee0b-4bcb-a0bd-4546f878a45b
  ```

---

### 9. Quà tặng đổi điểm thưởng (Member Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/loyalty/rewards`
* **Headers**:
  ```http
  X-User-ID: 7b9f8522-ee0b-4bcb-a0bd-4546f878a45b
  ```
* **Dữ liệu trả về (Output)**:
  ```json
  {
    "success": true,
    "data": [
      { "code": "WELCOME10",     "name": "Giảm 10% cho đơn hàng đầu tiên", "rewardType": "discount" },
      { "code": "BRONZE50",      "name": "Giảm 5K cho thành viên Bronze",  "rewardType": "discount" },
      { "code": "SILVER100",     "name": "Giảm 10K cho thành viên Silver", "rewardType": "discount" },
      { "code": "GOLD200",       "name": "Giảm 20K cho thành viên Gold",   "rewardType": "discount" },
      { "code": "PLATINUM500",   "name": "Giảm 50K cho thành viên Platinum", "rewardType": "discount" },
      { "code": "FREE_DELIVERY", "name": "Miễn phí giao hàng",             "rewardType": "free_delivery" }
    ]
  }
  ```

---

### 10. Danh sách các gói Hội viên (Member Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/subscriptions/plans`
* **Headers**: Không cần
* **Dữ liệu trả về (Output)**:
  ```json
  {
    "success": true,
    "data": [
      { "code": "basic",   "name": "Basic",   "priceMonthly": 99000.0,  "creditsMonthly": 100 },
      { "code": "premium", "name": "Premium", "priceMonthly": 249000.0, "creditsMonthly": 500 },
      { "code": "vip",     "name": "VIP",     "priceMonthly": 499000.0, "creditsMonthly": 2000 }
    ]
  }
  ```

---

### 11. Các phương thức thanh toán (Payment Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/payments/methods`
* **Headers**: Không cần
* **Dữ liệu trả về (Output)**:
  ```json
  {
    "success": true,
    "data": [
      { "code": "VNPAY",   "name": "VNPay",            "description": "Thanh toan qua VNPay QR / ATM / The quoc te" },
      { "code": "MOMO",    "name": "MoMo",             "description": "Thanh toan qua vi MoMo" },
      { "code": "ZALOPAY", "name": "ZaloPay",          "description": "Thanh toan qua vi ZaloPay" },
      { "code": "CREDIT",  "name": "Tai khoan tich luy", "description": "Thanh toan bang so du tai khoan ca phe" },
      { "code": "COD",     "name": "Tien mat",         "description": "Thanh toan khi nhan hang tai quan" }
    ]
  }
  ```

---

### 12. Gửi thử thông báo đẩy (Notification Service)
* **Method**: `POST`
* **URL**: `http://localhost:3000/v1/notifications/test`
* **Headers**:
  ```http
  X-User-ID: 7b9f8522-ee0b-4bcb-a0bd-4546f878a45b
  ```
* **Dữ liệu trả về (Output)**:
  ```json
  {
    "success": true,
    "data": "Test notification sent successfully"
  }
  ```

---

### 13. Dashboard Tổng quan Quản trị (Admin Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/admin/dashboard/overview`
* **Headers**:
  ```http
  X-Company-ID: 00000000-0000-0000-0000-000000000001
  ```
* **Dữ liệu trả về (Output)**:
  ```json
  {
    "success": true,
    "data": {
      "totalOrders": 0,
      "totalRevenue": 0.0,
      "totalCustomers": 1,
      "growthRate": 0.0
    }
  }
  ```

---

### 14. Danh sách mã khuyến mãi (Admin Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/admin/promotions`
* **Headers**: Không cần
* **Dữ liệu trả về (Output)**:
  ```json
  {
    "success": true,
    "data": [
      { "code": "WELCOME50", "name": "Chào mừng - Giảm 50K", "discountType": "fixed_amount", "discountValue": 50000.0 },
      { "code": "FREESHIP",  "name": "Miễn phí giao hàng",   "discountType": "free_delivery" },
      { "code": "SUMMER30",  "name": "Summer Sale - Giảm 30%", "discountType": "percentage",   "discountValue": 30.0 }
    ]
  }
  ```

---

### 15. Quản lý nhân viên (Admin Service)
* **Method**: `GET`
* **URL**: `http://localhost:3000/v1/admin/staffs`
* **Headers**:
  ```http
  X-Company-ID: 00000000-0000-0000-0000-000000000001
  ```
* **Dữ liệu trả về (Output)**: Danh sách phân trang thông tin nhân viên theo chi nhánh/công ty.

---

## ⚡ 3. CÁCH CHẠY LẠI TEST TỰ ĐỘNG

Bạn có thể tự chạy lại toàn bộ quy trình kiểm tra này bất cứ lúc nào qua lệnh PowerShell:
```powershell
powershell -ExecutionPolicy Bypass -File scripts/test_all_apis.ps1
```
