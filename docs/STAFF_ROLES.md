# AI Café - Staff Roles & Permissions

## 1. Tổng quan cấu trúc tổ chức

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                    AI CAFÉ STORE ORGANIZATION STRUCTURE                               │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                              ┌─────────────────┐                                     │
│                              │      OWNER       │                                     │
│                              │     (Chủ quán)   │                                     │
│                              │  Toàn quyền     │                                     │
│                              └────────┬────────┘                                     │
│                                       │                                              │
│                    ┌──────────────────┼──────────────────┐                          │
│                    │                  │                  │                           │
│                    ▼                  ▼                  ▼                           │
│           ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐             │
│           │    MANAGER     │ │   ACCOUNTANT   │ │   SUPERVISOR   │             │
│           │   (Quản lý)   │ │   (Kế toán)   │ │  (Giám sát)   │             │
│           │               │ │               │ │               │             │
│           │ • Quản lý ca │ │ • Báo cáo   │ │ • Ca trưởng   │             │
│           │ • Nhân viên  │ │ • Thu chi   │ │ • Kiểm tra  │             │
│           │ • Kho hàng   │ │ • Hóa đơn  │ │ • Hỗ trợ KH │             │
│           └────────┬────────┘ └────────┬────────┘ └────────┬────────┘             │
│                    │                  │                  │                          │
│                    └──────────────────┼──────────────────┘                         │
│                                       │                                             │
│                    ┌──────────────────┼──────────────────┐                         │
│                    │                  │                  │                          │
│                    ▼                  ▼                  ▼                          │
│           ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐             │
│           │    CASHIER     │ │    SALES      │ │    STAFF       │             │
│           │   (Thu ngân)   │ │   (Nhân viên)  │ │  (Phục vụ)    │             │
│           │               │ │               │ │               │             │
│           │ • POS操作    │ │ • Bán hàng  │ │ • Hỗ trợ KH │             │
│           │ • Thu tiền    │ │ • Giới thiệu│ │ • Làm đồ     │             │
│           │ • In hóa đơn │ │ • Chăm sóc │ │ • Dọn dẹp   │             │
│           └─────────────────┘ └─────────────────┘ └─────────────────┘             │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Danh sách vai trò chi tiết

### 2.1 Role Matrix

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              ROLE PERMISSION MATRIX                                   │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Permission                    │ Owner │ Manager │ Accountant │ Supervisor │ Cashier │ Staff │
│  ─────────────────────────────┼───────┼─────────┼───────────┼───────────┼─────────┼─────── │
│  VIEW DASHBOARD               │   ✓   │    ✓    │     ✓     │     ✓     │    ✓    │   ✓    │
│  ─────────────────────────────┼───────┼─────────┼───────────┼───────────┼─────────┼─────── │
│  POS: Process Sale            │   ✓   │    ✓    │           │     ✓     │    ✓    │        │
│  POS: Custom Amount           │   ✓   │    ✓    │           │     ✓     │    ✓    │        │
│  POS: Apply Discount          │   ✓   │    ✓    │           │     ✓     │        │        │
│  POS: Void Transaction       │   ✓   │    ✓    │           │     ✓     │        │        │
│  POS: Refund                  │   ✓   │    ✓    │           │     ✓     │        │        │
│  POS: Print Receipt           │   ✓   │    ✓    │           │     ✓     │    ✓    │        │
│  ─────────────────────────────┼───────┼─────────┼───────────┼───────────┼─────────┼─────── │
│  CUSTOMER: Create             │   ✓   │    ✓    │           │     ✓     │    ✓    │        │
│  CUSTOMER: View               │   ✓   │    ✓    │     ✓     │     ✓     │    ✓    │        │
│  CUSTOMER: Edit               │   ✓   │    ✓    │           │     ✓     │        │        │
│  CUSTOMER: Adjust Credits     │   ✓   │    ✓    │           │     ✓     │        │        │
│  CUSTOMER: Block/Unblock      │   ✓   │    ✓    │           │           │        │        │
│  ─────────────────────────────┼───────┼─────────┼───────────┼───────────┼─────────┼─────── │
│  SHIFT: Open/Close           │   ✓   │    ✓    │           │     ✓     │        │        │
│  SHIFT: View Shift Report    │   ✓   │    ✓    │     ✓     │     ✓     │        │        │
│  SHIFT: Assign Staff          │   ✓   │    ✓    │           │           │        │        │
│  ─────────────────────────────┼───────┼─────────┼───────────┼───────────┼─────────┼─────── │
│  INVENTORY: View             │   ✓   │    ✓    │     ✓     │     ✓     │        │        │
│  INVENTORY: Manage           │   ✓   │    ✓    │           │           │        │        │
│  ─────────────────────────────┼───────┼─────────┼───────────┼───────────┼─────────┼─────── │
│  REPORTS: Daily Sales        │   ✓   │    ✓    │     ✓     │           │        │        │
│  REPORTS: Revenue            │   ✓   │    ✓    │     ✓     │           │        │        │
│  REPORTS: Export             │   ✓   │    ✓    │     ✓     │           │        │        │
│  REPORTS: Tax/Fiscal         │   ✓   │    ✓    │     ✓     │           │        │        │
│  ─────────────────────────────┼───────┼─────────┼───────────┼───────────┼─────────┼─────── │
│  STAFF: View                  │   ✓   │    ✓    │           │           │        │        │
│  STAFF: Add                  │   ✓   │    ✓    │           │           │        │        │
│  STAFF: Edit                 │   ✓   │    ✓    │           │           │        │        │
│  STAFF: Delete               │   ✓   │         │           │           │        │        │
│  STAFF: Assign Role          │   ✓   │         │           │           │        │        │
│  STAFF: Set PIN              │   ✓   │         │           │           │        │        │
│  ─────────────────────────────┼───────┼─────────┼───────────┼───────────┼─────────┼─────── │
│  SETTINGS: Store Info         │   ✓   │    ✓    │           │           │        │        │
│  SETTINGS: Pricing           │   ✓   │    ✓    │           │           │        │        │
│  SETTINGS: Payment Methods   │   ✓   │         │           │           │        │        │
│  SETTINGS: System Config    │   ✓   │         │           │           │        │        │
│  ─────────────────────────────┼───────┼─────────┼───────────┼───────────┼─────────┼─────── │
│  BACKUP: Manual Backup       │   ✓   │         │           │           │        │        │
│  BACKUP: Restore            │   ✓   │         │           │           │        │        │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Chi tiết từng vai trò

### 3.1 OWNER (Chủ quán)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              OWNER - CHỦ QUÁN                                         │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Mô tả:                                                                             │
│  ───────                                                                             │
│  Người sở hữu hoặc quản lý cao nhất của cửa hàng. Có toàn quyền trên              │
│  hệ thống, có thể thực hiện mọi thao tác và cấu hình.                            │
│                                                                                      │
│  Thông tin:                                                                          │
│  ─────────                                                                             │
│  • Số lượng: 1-2 người                                                              │
│  • Báo cáo: Trực tiếp với Owner                                                    │
│  • Thiết bị: Có thể dùng trên mọi thiết bị POS                                   │
│                                                                                      │
│  Quyền hạn:                                                                         │
│  ────────                                                                             │
│  ✅ Tất cả các quyền trên hệ thống                                                  │
│  ✅ Cấu hình hệ thống (giá, thanh toán, v.v.)                                      │
│  ✅ Quản lý nhân viên (thêm, sửa, xóa, phân quyền)                                │
│  ✅ Xem và xuất báo cáo tài chính                                                  │
│  ✅ Hoàn tiền (refund) không giới hạn                                              │
│  ✅ Cấu hình phương thức thanh toán                                                │
│  ✅ Backup và restore dữ liệu                                                       │
│  ✅ Quản lý nhiều chi nhánh (nếu có)                                               │
│                                                                                      │
│  Hạn chế:                                                                            │
│  ────────                                                                             │
│  ⚠️ Không có hạn chế - Toàn quyền                                                  │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 MANAGER (Quản lý)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              MANAGER - QUẢN LÝ                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Mô tả:                                                                             │
│  ───────                                                                             │
│  Người quản lý hàng ngày của cửa hàng. Phụ trách vận hành, quản lý nhân viên       │
│  theo ca và đảm bảo cửa hàng hoạt động trơn tru.                                  │
│                                                                                      │
│  Thông tin:                                                                          │
│  ─────────                                                                             │
│  • Số lượng: 1-3 người (tùy quy mô)                                               │
│  • Báo cáo: Trực tiếp với Owner                                                    │
│  • Ca làm việc: Có thể quản lý mọi ca                                              │
│                                                                                      │
│  Quyền hạn:                                                                         │
│  ────────                                                                             │
│  ✅ Xem dashboard và KPIs                                                            │
│  ✅ Mở/đóng ca làm việc                                                             │
│  ✅ Xem và phân công nhân viên theo ca                                              │
│  ✅ Xử lý giao dịch bán hàng (POS)                                                 │
│  ✅ Tạo và quản lý khách hàng                                                      │
│  ✅ Áp dụng giảm giá cho khách                                                     │
│  ✅ Hoàn tiền (có giới hạn theo cấu hình)                                          │
│  ✅ Xem báo cáo doanh thu, bán hàng                                                │
│  ✅ Điều chỉnh credits khách hàng (có giới hạn)                                    │
│  ✅ Khóa/mở khóa tài khoản khách hàng                                             │
│  ✅ Cấu hình thông tin cửa hàng (không bao gồm tài chính)                         │
│  ✅ Cấu hình giá sản phẩm                                                          │
│                                                                                      │
│  Hạn chế:                                                                            │
│  ────────                                                                             │
│  ⚠️ Không thể xóa nhân viên                                                        │
│  ⚠️ Không thể cấu hình phương thức thanh toán                                      │
│  ⚠️ Không thể thay đổi cấu hình hệ thống quan trọng                               │
│  ⚠️ Hoàn tiền có giới hạn (theo cấu hình của Owner)                               │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.3 ACCOUNTANT (Kế toán)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              ACCOUNTANT - KẾ TOÁN                                    │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Mô tả:                                                                             │
│  ───────                                                                             │
│  Người phụ trách theo dõi tài chính, tính toán thu chi và lập báo cáo.            │
│  Thường không trực tiếp bán hàng nhưng có quyền xem dữ liệu tài chính.            │
│                                                                                      │
│  Thông tin:                                                                          │
│  ─────────                                                                             │
│  • Số lượng: 1-2 người                                                             │
│  • Báo cáo: Trực tiếp với Owner/Manager                                            │
│  • Ca làm việc: Thường là giờ hành chính                                          │
│                                                                                      │
│  Quyền hạn:                                                                         │
│  ────────                                                                             │
│  ✅ Xem dashboard tài chính                                                         │
│  ✅ Xem báo cáo doanh thu chi tiết                                                 │
│  ✅ Xem báo cáo thu chi                                                            │
│  ✅ Xuất báo cáo (Excel, PDF)                                                      │
│  ✅ Xem lịch sử giao dịch                                                          │
│  ✅ Xem thông tin khách hàng (không chỉnh sửa)                                    │
│  ✅ Xem báo cáo ca làm việc                                                        │
│  ✅ Xem tồn kho                                                                   │
│                                                                                      │
│  Hạn chế:                                                                            │
│  ────────                                                                             │
│  ⚠️ Không thể thực hiện giao dịch bán hàng                                       │
│  ⚠️ Không thể tạo/sửa khách hàng                                                 │
│  ⚠️ Không thể điều chỉnh credits                                                  │
│  ⚠️ Không thể hoàn tiền                                                           │
│  ⚠️ Không thể quản lý nhân viên                                                  │
│  ⚠️ Không thể thay đổi cấu hình                                                  │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.4 SUPERVISOR (Giám sát / Ca trưởng)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              SUPERVISOR - GIÁM SÁT                                   │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Mô tả:                                                                             │
│  ───────                                                                             │
│  Người phụ trách một ca làm việc cụ thể. Có quyền hạn chế hơn Manager              │
│  nhưng đủ để điều hành ca làm việc một cách độc lập.                              │
│                                                                                      │
│  Thông tin:                                                                          │
│  ─────────                                                                             │
│  • Số lượng: 2-4 người (1-2 người/ca)                                             │
│  • Báo cáo: Trực tiếp với Manager                                                  │
│  • Ca làm việc: Phụ trách ca được phân công                                        │
│                                                                                      │
│  Quyền hạn:                                                                         │
│  ────────                                                                             │
│  ✅ Xem dashboard ca làm việc                                                       │
│  ✅ Xử lý giao dịch bán hàng (POS)                                                 │
│  ✅ Tạo khách hàng mới                                                             │
│  ✅ Áp dụng giảm giá cho khách                                                     │
│  ✅ Hoàn tiền (có giới hạn nhỏ)                                                   │
│  ✅ Xem thông tin khách hàng                                                        │
│  ✅ Điều chỉnh credits khách hàng (giới hạn nhỏ)                                  │
│  ✅ In hóa đơn                                                                    │
│  ✅ Xem báo cáo ca                                                                  │
│  ✅ Hỗ trợ nhân viien bán hàng                                                     │
│                                                                                      │
│  Hạn chế:                                                                            │
│  ────────                                                                             │
│  ⚠️ Không thể mở/đóng ca (trừ ca mình)                                            │
│  ⚠️ Không thể xóa giao dịch                                                      │
│  ⚠️ Không thể quản lý nhân viên                                                   │
│  ⚠️ Không thể xuất báo cáo tài chính                                              │
│  ⚠️ Điều chỉnh credits có giới hạn (VD: tối đa 50,000đ/ngày)                     │
│  ⚠️ Hoàn tiền có giới hạn (VD: tối đa 100,000đ/ngày)                            │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.5 CASHIER (Thu ngân)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              CASHIER - THU NGÂN                                     │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Mô tả:                                                                             │
│  ───────                                                                             │
│  Nhân viên chịu trách nhiệm thu tiền và xử lý giao dịch tại quầy.                │
│  Là vai trò chính trong POS operation.                                             │
│                                                                                      │
│  Thông tin:                                                                          │
│  ─────────                                                                             │
│  • Số lượng: 2-4 người/ca                                                          │
│  • Báo cáo: Trực tiếp với Supervisor/Manager                                        │
│  • Ca làm việc: Theo lịch phân công                                                │
│                                                                                      │
│  Quyền hạn:                                                                         │
│  ────────                                                                             │
│  ✅ Xem dashboard cơ bản                                                            │
│  ✅ Xử lý giao dịch bán hàng (POS)                                                 │
│  ✅ Nhập số tiền tùy chỉnh                                                         │
│  ✅ Tra cứu khách hàng                                                             │
│  ✅ Tạo khách hàng mới                                                             │
│  ✅ In hóa đơn                                                                    │
│  ✅ Xem số dư khách hàng                                                           │
│                                                                                      │
│  Hạn chế:                                                                            │
│  ────────                                                                             │
│  ⚠️ Không thể áp dụng giảm giá                                                    │
│  ⚠️ Không thể hoàn tiền                                                           │
│  ⚠️ Không thể điều chỉnh credits                                                  │
│  ⚠️ Không thể xem báo cáo tài chính                                               │
│  ⚠️ Không thể xóa/sửa giao dịch                                                  │
│  ⚠️ Không thể quản lý nhân viên                                                  │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.6 STAFF (Nhân viên phục vụ)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              STAFF - NHÂN VIÊN                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Mô tả:                                                                             │
│  ───────                                                                             │
│  Nhân viên phục vụ cơ bản. Có quyền hạn chế nhất, chỉ xem được thông tin cần thiết.│
│  Thường không sử dụng POS trực tiếp.                                               │
│                                                                                      │
│  Thông tin:                                                                          │
│  ─────────                                                                             │
│  • Số lượng: Không giới hạn                                                        │
│  • Báo cáo: Trực tiếp với Supervisor/Manager                                        │
│  • Ca làm việc: Theo lịch phân công                                                │
│                                                                                      │
│  Quyền hạn:                                                                         │
│  ────────                                                                             │
│  ✅ Xem dashboard cơ bản (số dư cá nhân)                                          │
│  ✅ Xem thông tin khách hàng (chỉ xem khi được phân công hỗ trợ)                   │
│  ✅ (Không sử dụng POS)                                                            │
│                                                                                      │
│  Hạn chế:                                                                            │
│  ────────                                                                             │
│  ⚠️ Không thể thực hiện bất kỳ giao dịch nào                                      │
│  ⚠️ Không thể xem báo cáo                                                         │
│  ⚠️ Không thể quản lý khách hàng                                                 │
│  ⚠️ Không thể xem thông tin tài chính                                             │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Cấu hình hệ thống

### 4.1 Giới hạn theo vai trò

```yaml
# Cấu hình giới hạn trong hệ thống
role_limits:
  manager:
    max_refund_per_day: 500000          # VND
    max_credit_adjustment_per_day: 200000  # VND
    can_void_transaction: true
    can_delete_transaction: false
    
  supervisor:
    max_refund_per_day: 100000          # VND
    max_credit_adjustment_per_day: 50000   # VND
    can_void_transaction: true
    can_delete_transaction: false
    
  cashier:
    max_refund_per_day: 0              # Không được hoàn tiền
    max_credit_adjustment_per_day: 0    # Không được điều chỉnh
    can_void_transaction: false
    can_delete_transaction: false
```

### 4.2 Audit Log cho các thao tác nhạy cảm

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              AUDIT LOG TRACKING                                      │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Các thao tác sau BẮT BUỘC phải được log:                                          │
│  ────────────────────────────────────────                                          │
│                                                                                      │
│  ✏️ Credit Adjustment                                                              │
│     • Ai điều chỉnh (staff_id)                                                     │
│     • Khách hàng nào (customer_id)                                                 │
│     • Số tiền trước/sau                                                            │
│     • Lý do                                                                    │
│     • Timestamp                                                                   │
│                                                                                      │
│  🔄 Refund                                                                          │
│     • Ai thực hiện (staff_id)                                                     │
│     • Transaction gốc (transaction_id)                                            │
│     • Số tiền hoàn                                                                │
│     • Lý do (required)                                                            │
│     • Approved by (manager_id - nếu vượt limit)                                   │
│                                                                                      │
│  🗑️ Void Transaction                                                              │
│     • Ai thực hiện (staff_id)                                                     │
│     • Transaction bị hủy                                                           │
│     • Lý do (required)                                                            │
│     • Approved by (manager_id)                                                     │
│                                                                                      │
│  👤 Customer Actions                                                               │
│     • Tạo/Sửa/Khóa tài khoản                                                      │
│     • Ai thực hiện                                                                │
│     • Chi tiết thay đổi                                                           │
│                                                                                      │
│  ⚙️ System Changes                                                                │
│     • Cấu hình giá                                                                 │
│     • Cấu hình thanh toán                                                          │
│     • Thêm/sửa/xóa nhân viên                                                       │
│     • Phân quyền                                                                  │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Authentication & Authorization

### 5.1 Đăng nhập POS

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              POS LOGIN FLOW                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│    Nhân viên                    POS App                     Backend                  │
│      │                          │                           │                        │
│      │  1. Nhập PIN 4-6 số    │                           │                        │
│      │ ──────────────────────► │                           │                        │
│      │                          │                           │                        │
│      │                          │  2. Gửi PIN + Device ID              │                        │
│      │                          │ ─────────────────────────► │                        │
│      │                          │ ◄───────────────────────── │                        │
│      │                          │    (token + role + permissions)     │                        │
│      │                          │                           │                        │
│      │  3. Đăng nhập thành công, hiển thị giao diện theo role           │                        │
│      │ ◄───────────────────── │                           │                        │
│      │                          │                           │                        │
│      │  PIN sai quá 3 lần → Tài khoản bị khóa tạm thời       │                        │
│      │                          │                           │                        │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 PIN Management

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              PIN MANAGEMENT                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Quy tắc PIN:                                                                      │
│  ─────────────                                                                      │
│  • Độ dài: 4-6 số (tùy cấu hình)                                                 │
│  • Không được trùng với PIN của nhân viên khác                                      │
│  • Hết hạn sau 90 ngày (tùy cấu hình)                                             │
│  • Phải đổi PIN sau khi reset                                                     │
│                                                                                      │
│  Bảo mật:                                                                          │
│  ────────                                                                          │
│  • PIN được hash trước khi lưu (bcrypt/argon2)                                     │
│  • Không hiển thị PIN dưới dạng plain text                                        │
│  • Log khi PIN được thay đổi                                                      │
│                                                                                      │
│  Ai có quyền reset PIN:                                                            │
│  ─────────────────────                                                              │
│  • Owner: Reset được tất cả PIN                                                   │
│  • Manager: Reset được PIN của Cashier/Staff (không phải Manager khác)            │
│  • Nhân viên: Tự đổi PIN của mình                                                 │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 5.3 Session Management

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              SESSION MANAGEMENT                                     │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Timeouts:                                                                          │
│  ─────────                                                                          │
│  • Session timeout: 8 giờ (1 ca làm việc)                                          │
│  • Inactivity timeout: 15 phút (khóa màn hình)                                    │
│  • Device limit: 1 thiết bị đăng nhập cùng lúc                                  │
│                                                                                      │
│  Shift Binding:                                                                    │
│  ─────────────                                                                      │
│  • Mỗi session gắn với 1 ca làm việc                                             │
│  • Khi đóng ca, session tự động kết thúc                                         │
│  • Báo cáo gắn với staff_id + shift_id                                            │
│                                                                                      │
│  Logout:                                                                            │
│  ───────                                                                            │
│  • Đăng xuất thủ công                                                              │
│  • Đăng xuất khi đóng ca                                                          │
│  • Đăng xuất khi hết phiên                                                        │
│  • Đăng xuất từ xa (admin khóa tài khoản)                                       │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 6. Shift Management

### 6.1 Ca làm việc

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              SHIFT SCHEDULE                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Ca tiêu chuẩn:                                                                    │
│  ───────────────                                                                    │
│  ┌────────────┬──────────────────┬──────────────┐                                 │
│  │   Ca       │   Thời gian      │   Giờ làm   │                                 │
│  ├────────────┼──────────────────┼──────────────┤                                 │
│  │  Sáng      │  06:00 - 14:00  │   8 tiếng   │                                 │
│  ├────────────┼──────────────────┼──────────────┤                                 │
│  │  Chiều     │  14:00 - 22:00  │   8 tiếng   │                                 │
│  ├────────────┼──────────────────┼──────────────┤                                 │
│  │  Đêm       │  22:00 - 06:00  │   8 tiếng   │                                 │
│  └────────────┴──────────────────┴──────────────┘                                 │
│                                                                                      │
│  Giao ca:                                                                          │
│  ───────                                                                            │
│  • Ca bắt đầu: Supervisor/Manager mở ca                                           │
│  • Ca kết thúc: Đóng ca + Xác nhận số tiền                                        │
│  • Tiền mặt: Kiểm đếm + Đối chiếu với hệ thống                                  │
│  • Bàn giao: Ghi chú + Xác nhận                                                  │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 Shift Opening

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              SHIFT OPENING FLOW                                      │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│    Supervisor/Manager              POS                       Backend                │
│           │                        │                          │                      │
│           │  1. Chọn "Mở ca"     │                          │                      │
│           │ ──────────────────────► │                          │                      │
│           │                        │                          │                      │
│           │  2. Xác nhận thông tin ca                      │                      │
│           │  • Ca: Sáng/Chiều/Đêm                          │                      │
│           │  • Giờ bắt đầu: auto                          │                      │
│           │  • Tiền đầu ca: 0 (hoặc số dư ca trước)        │                      │
│           │ ──────────────────────► │                          │                      │
│           │                        │                          │                      │
│           │                        │  3. Tạo shift record                │                      │
│           │                        │ ─────────────────────────► │                      │
│           │                        │ ◄───────────────────────── │                      │
│           │                        │    (shift_id, start_time)       │                      │
│           │                        │                          │                      │
│           │  4. Phân công nhân viên trong ca               │                      │
│           │  • Thêm cashier vào ca                         │                      │
│           │  • Gán thiết bị POS                            │                      │
│           │                        │                          │                      │
│           │  5. Ca bắt đầu hoạt động                      │                      │
│           │ ◄───────────────────── │                          │                      │
│           │                        │                          │                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 6.3 Shift Closing

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              SHIFT CLOSING FLOW                                     │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│    Supervisor/Manager              POS                       Backend                │
│           │                        │                          │                      │
│           │  1. Chọn "Đóng ca"    │                          │                      │
│           │ ──────────────────────► │                          │                      │
│           │                        │                          │                      │
│           │  2. Hiển thị tổng kết ca                        │                      │
│           │  • Tổng giao dịch: 45                                               │
│           │  • Tổng doanh thu: 4,500,000đ                                        │
│           │  • Tiền mặt: 2,000,000đ                                              │
│           │  • QR/Transfer: 2,500,000đ                                          │
│           │  • Hoàn tiền: 50,000đ                                                │
│           │ ──────────────────────► │                          │                      │
│           │                        │                          │                      │
│           │  3. Nhập số tiền mặt thực tế                       │                      │
│           │  • Đếm tiền mặt trong két                                               │
│           │  • Nhập: 1,980,000đ                                                  │
│           │ ──────────────────────► │                          │                      │
│           │                        │                          │                      │
│           │  4. Chênh lệch: -20,000đ (thiếu)                             │                      │
│           │  • Ghi nhận chênh lệch                                               │
│           │  • Lý do (nếu có)                                                    │
│           │ ──────────────────────► │                          │                      │
│           │                        │                          │                      │
│           │                        │  5. Đóng ca + cập nhật              │                      │
│           │                        │ ─────────────────────────► │                      │
│           │                        │ ◄───────────────────────── │                      │
│           │                        │    (shift closed)           │                      │
│           │                        │                          │                      │
│           │  6. In báo cáo ca      │                          │                      │
│           │ ◄───────────────────── │                          │                      │
│           │                        │                          │                      │
│           │  7. Bàn giao cho ca tiếp theo                       │                      │
│           │                        │                          │                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 7. Multi-Store Support

### 7.1 Store Hierarchy

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              MULTI-STORE HIERARCHY                                  │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                         ┌─────────────────┐                                         │
│                         │   HEADQUARTER   │                                         │
│                         │  (Trụ sở)       │                                         │
│                         │  Owner toàn quyền│                                         │
│                         └────────┬────────┘                                         │
│                                  │                                                  │
│           ┌──────────────────────┼──────────────────────┐                          │
│           │                      │                      │                          │
│           ▼                      ▼                      ▼                          │
│  ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐                   │
│  │   STORE 1       │   │   STORE 2       │   │   STORE 3       │                   │
│  │   (Quận 1)     │   │   (Quận 3)     │   │   (Quận 5)     │                   │
│  │                 │   │                 │   │                 │                   │
│  │  Owner          │   │  Owner          │   │  Owner          │                   │
│  │  Manager A      │   │  Manager B      │   │  Manager C      │                   │
│  │  Cashiers...    │   │  Cashiers...    │   │  Cashiers...    │                   │
│  └─────────────────┘   └─────────────────┘   └─────────────────┘                   │
│                                                                                      │
│  Mô hình:                                                                          │
│  ────────                                                                          │
│  • Owner có thể quản lý nhiều store                                              │
│  • Mỗi store có Manager riêng                                                    │
│  • Staff chỉ hoạt động trong 1 store (hoặc được phân công)                       │
│  • Báo cáo có thể xem theo store hoặc tổng hợp                                 │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 7.2 Staff Assignment

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              STAFF ASSIGNMENT                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Staff Types:                                                                       │
│  ────────────                                                                       │
│  ┌────────────────┬────────────────────────────────────────────────────────┐       │
│  │ Store-based    │ Làm việc cố định tại 1 store                            │       │
│  │                │ • Đăng nhập tại store đó                                 │       │
│  │                │ • Được phân công ca tại store đó                         │       │
│  ├────────────────┼────────────────────────────────────────────────────────┤       │
│  │ Multi-store    │ Làm việc tại nhiều store                                 │       │
│  │                │ • Đăng nhập tại store đang làm việc                     │       │
│  │                │ • Xem lịch làm việc tại nhiều store                      │       │
│  │                │ • Thường là Manager/Supervisor                          │       │
│  ├────────────────┼────────────────────────────────────────────────────────┤       │
│  │ Float Staff    │ Điều động linh hoạt                                     │       │
│  │                │ • Được phân công theo ngày                              │       │
│  │                │ • Check-in/out tại store được phân công                 │       │
│  └────────────────┴────────────────────────────────────────────────────────┘       │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 8. Implementation Checklist

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           IMPLEMENTATION CHECKLIST                                   │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Database:                                                                          │
│  ────────                                                                          │
│  □ staff table (id, store_id, name, phone, role, pin_hash, status)                │
│  □ staff_shifts table (id, staff_id, shift_id, check_in, check_out)               │
│  □ shifts table (id, store_id, type, open_time, close_time, opened_by)            │
│  □ role_permissions table (role, permission, allowed)                             │
│  □ audit_logs table (action, staff_id, details, timestamp)                        │
│                                                                                      │
│  API Endpoints:                                                                    │
│  ─────────────                                                                      │
│  □ POST /api/staff/login (PIN authentication)                                      │
│  □ GET /api/staff/profile                                                          │
│  □ POST /api/staff/logout                                                          │
│  □ CRUD /api/staff (admin only)                                                   │
│  □ POST /api/staff/reset-pin                                                      │
│  □ PUT /api/staff/change-pin                                                       │
│  □ GET /api/roles/{role}/permissions                                              │
│                                                                                      │
│  POS Features:                                                                      │
│  ─────────────                                                                      │
│  □ PIN login screen                                                               │
│  □ Role-based UI (show/hide features)                                             │
│  □ Shift management (open/close)                                                 │
│  □ Staff list with roles                                                         │
│  □ Audit trail display                                                            │
│                                                                                      │
│  Admin Dashboard:                                                                  │
│  ───────────────                                                                    │
│  □ Staff management page                                                          │
│  □ Role configuration                                                            │
│  □ Shift reports                                                                  │
│  □ Audit logs viewer                                                              │
│  □ Permission matrix                                                              │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 9. Summary - Quick Reference

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              ROLE QUICK REFERENCE                                    │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Role          │ Abbr │ Can Sell │ Can Refund │ Can Adjust │ Can Manage │ Reports │
│  ──────────────┼──────┼──────────┼────────────┼─────────────┼────────────┼──────── │
│  Owner         │ OWN  │    ✓     │     ✓      │     ✓       │     ✓      │   ✓    │
│  Manager       │ MGR  │    ✓     │     ✓      │     ✓       │     ✓      │   ✓    │
│  Accountant    │ ACT  │    ✗     │     ✗      │     ✗       │     ✗      │   ✓    │
│  Supervisor    │ SUP  │    ✓     │     △      │     △       │     ✗      │   △    │
│  Cashier       │ CSH  │    ✓     │     ✗      │     ✗       │     ✗      │   ✗    │
│  Staff         │ STF  │    ✗     │     ✗      │     ✗       │     ✗      │   ✗    │
│                                                                                      │
│  △ = Có giới hạn                                                                  │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```
