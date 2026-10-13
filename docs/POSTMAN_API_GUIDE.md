# 🚀 TÀI LIỆU TEST TOÀN BỘ API QUA 1 CỔNG DUY NHẤT (PORT 3000)

> **Dự án**: AI Café Platform  
> **Điểm truy cập duy nhất (Single Entry Point)**: `http://localhost:3000`  
> **Kiến trúc**: Go Fiber Gateway (Cổng 3000) tự động chuyển tiếp (Reverse Proxy) sang 7 Microservices Java  
> **Cập nhật ngày**: 12/10/2026  

---

> [!NOTE]
> ### 🎯 BẠN KHÔNG CẦN NHỚ NHIỀU CỔNG NỮA!
> Giờ đây **100% tất cả các API** đều được gọi qua **DUY NHẤT CỔNG `3000`** (`http://localhost:3000`).
> * Bạn **không cần Import file** vào Postman.
> * Bạn **không cần tạo Environment**, để nguyên ô trên cùng bên phải là **`No Environment`**.
> * Chỉ cần copy trực tiếp dòng URL từ tài liệu này dán thẳng vào Postman và bấm **Send**!

---

## 📌 BẢNG TRA CỨU NHANH CÁC API THÔNG DỤNG (PORT 3000)

### 1. Menu, Sản phẩm & Giỏ hàng
| Chức năng | Method | URL Copy dán vào Postman | Headers cần có | Query / Body mẫu |
|---|---|---|---|---|
| **Xem menu món** | `GET` | `http://localhost:3000/v1/products?companyId=00000000-0000-0000-0000-000000000001` | Không | Query: `companyId` |
| **Xem chi tiết giỏ hàng** | `GET` | `http://localhost:3000/v1/cart?cafeId=00000000-0000-0000-0000-000000000010` | `X-User-ID: a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11` | Query: `cafeId` |
| **Thêm món vào giỏ** | `POST` | `http://localhost:3000/v1/cart/items` | `X-User-ID: a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11`<br>`Content-Type: application/json` | **Body (JSON)**:<br>`{"productId":"e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a55","quantity":2,"note":"Ít đá"}` |
| **Tạo đơn hàng mới** | `POST` | `http://localhost:3000/v1/orders` | `X-User-ID: a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11`<br>`X-Company-ID: 00000000-0000-0000-0000-000000000001`<br>`Content-Type: application/json` | **Body**: chi tiết đơn |
| **Lịch sử đơn hàng** | `GET` | `http://localhost:3000/v1/orders` | `X-User-ID: a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11` | Không |

---

### 2. Quán Cafe, Bàn & Đặt chỗ
| Chức năng | Method | URL Copy dán vào Postman | Headers cần có | Params / Body |
|---|---|---|---|---|
| **Xem danh sách quán** | `GET` | `http://localhost:3000/v1/cafes` | Không | Không |
| **Xem danh sách bàn** | `GET` | `http://localhost:3000/v1/cafes/00000000-0000-0000-0000-000000000010/seats` | Không | Path: `cafeId` |
| **Kiểm tra bàn trống** | `POST` | `http://localhost:3000/v1/seats/availability` | `Content-Type: application/json` | **Body**: `{"cafeId":"00000000-0000-0000-0000-000000000010","reservationDate":"2026-10-15","startTime":"14:00","endTime":"16:00","capacity":4}` |
| **Đặt bàn mới** | `POST` | `http://localhost:3000/v1/reservations` | `X-User-ID: a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11`<br>`Content-Type: application/json` | **Body**: Thông tin đặt bàn |
| **Đơn đặt bàn của tôi**| `GET` | `http://localhost:3000/v1/reservations/user/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11` | Không | Path: `userId` |

---

### 3. Tài khoản & Xác thực (Auth)
| Chức năng | Method | URL Copy dán vào Postman | Headers cần có | Body mẫu |
|---|---|---|---|---|
| **Đăng ký tài khoản** | `POST` | `http://localhost:3000/v1/auth/register` | `Content-Type: application/json` | `{"email":"khach@aicafe.vn","password":"Password123@","fullName":"Nguyễn Văn A","phone":"0987654321","companyId":"00000000-0000-0000-0000-000000000001"}` |
| **Đăng nhập lấy Token**| `POST` | `http://localhost:3000/v1/auth/login` | `Content-Type: application/json` | `{"email":"khach@aicafe.vn","password":"Password123@","companyId":"00000000-0000-0000-0000-000000000001"}` |
| **Xem hồ sơ cá nhân** | `GET` | `http://localhost:3000/v1/me` | `Authorization: Bearer <token>` hoặc `X-User-ID: a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11` | Không |

---

### 4. Tích điểm & Gói Hội viên (Member Service)
| Chức năng | Method | URL Copy dán vào Postman | Headers cần có | Ghi chú |
|---|---|---|---|---|
| **Xem điểm tích lũy** | `GET` | `http://localhost:3000/v1/loyalty/account` | `X-User-ID: a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11` | Xem số điểm & hạng thẻ |
| **Danh sách quà đổi thưởng** | `GET` | `http://localhost:3000/v1/loyalty/rewards` | Không | Danh sách voucher |
| **Đổi thưởng bằng điểm** | `POST` | `http://localhost:3000/v1/loyalty/redeem` | `X-User-ID: a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11`<br>`Content-Type: application/json` | `{"rewardId":"16eebc99-9c0b-4ef8-bb6d-6bb9bd380a77"}` |
| **Danh sách gói hội viên** | `GET` | `http://localhost:3000/v1/subscriptions/plans` | Không | Các gói Pro, VIP |

---

### 5. Thanh toán (Payment Service)
| Chức năng | Method | URL Copy dán vào Postman | Headers cần có | Body mẫu |
|---|---|---|---|---|
| **Tạo yêu cầu thanh toán** | `POST` | `http://localhost:3000/v1/payments` | `X-User-ID: a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11`<br>`Content-Type: application/json` | `{"orderId":"f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a66","amount":90000,"paymentMethod":"vnpay"}` |
| **Phương thức thanh toán** | `GET` | `http://localhost:3000/v1/payments/methods` | Không | Lấy VNPay, MoMo, ZaloPay |

---

### 6. Quản trị viên (Admin Service)
| Chức năng | Method | URL Copy dán vào Postman | Headers cần có | Ghi chú |
|---|---|---|---|---|
| **Dashboard doanh thu** | `GET` | `http://localhost:3000/v1/admin/dashboard/overview` | `X-Company-ID: 00000000-0000-0000-0000-000000000001` | Thống kê tổng quan |
| **Danh sách đơn toàn hệ thống** | `GET` | `http://localhost:3000/v1/admin/orders` | `X-Company-ID: 00000000-0000-0000-0000-000000000001` | Quản lý đơn hàng |
| **Cập nhật trạng thái đơn** | `PUT` | `http://localhost:3000/v1/admin/orders/f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a66/status` | `Content-Type: application/json` | `{"status":"completed"}` |
| **Danh sách mã giảm giá** | `GET` | `http://localhost:3000/v1/admin/promotions` | Không | Quản lý voucher |
| **Danh sách nhân viên** | `GET` | `http://localhost:3000/v1/admin/staffs` | `X-Company-ID: 00000000-0000-0000-0000-000000000001` | Quản lý nhân viên |
| **Nhật ký kiểm toán** | `GET` | `http://localhost:3000/v1/admin/audit-logs` | Không | Xem log thao tác |

---

### 7. Thông báo (Notification Service)
| Chức năng | Method | URL Copy dán vào Postman | Headers cần có | Body mẫu |
|---|---|---|---|---|
| **Bắn thông báo thử nghiệm** | `POST` | `http://localhost:3000/v1/notifications/test` | `X-User-ID: 7b9f8522-ee0b-4bcb-a0bd-4546f878a45b` | Không |
| **Gửi thông báo đẩy (Push)** | `POST` | `http://localhost:3000/v1/notifications/send` | `Content-Type: application/json` | `{"userId":"7b9f8522-ee0b-4bcb-a0bd-4546f878a45b","title":"Ưu đãi mới","body":"Bạn vừa nhận được voucher 20%"}` |
| **Đăng ký Token thiết bị** | `POST` | `http://localhost:3000/v1/notifications/devices` | `X-User-ID: 7b9f8522-ee0b-4bcb-a0bd-4546f878a45b`<br>`Content-Type: application/json` | `{"token":"fcm_device_token_sample_123","platform":"android"}` |

---

## 🔑 DANH SÁCH MÃ ID MẪU ĐỂ TEST NHANH

Bạn có thể copy trực tiếp các ID mẫu đã có sẵn trong cơ sở dữ liệu:

* **Tài khoản test vừa tạo**:
  * **User ID**: `7b9f8522-ee0b-4bcb-a0bd-4546f878a45b`
  * **Email**: `customer@aicafe.com`
  * **Mật khẩu**: `SecurePass123!`
* **Mã Công ty (Company ID)**: `00000000-0000-0000-0000-000000000001`
* **Mã Quán Cafe (Cafe ID)**: `00000000-0000-0000-0000-000000000010`
* **Mã Sản phẩm (Product ID)**: `e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a55`
* **Mã Đơn hàng (Order ID)**: `f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a66`

