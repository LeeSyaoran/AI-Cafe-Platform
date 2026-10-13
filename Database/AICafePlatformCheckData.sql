-- ==============================================================================
-- AI CAFÉ PLATFORM - KIỂM TRA TRẠNG THÁI DỮ LIỆU CẢ 48 BẢNG TRONG CƠ SỞ DỮ LIỆU
-- File: AICafePlatformCheckData.sql
-- Hướng dẫn: Mở file này trong pgAdmin 4 và nhấn nút Execute (F5).
-- Kết quả: pgAdmin sẽ hiển thị ĐẦY ĐỦ 48 DÒNG đại diện cho 48 BẢNG cùng số dòng
--          và trạng thái "✅ Đã có dữ liệu"!
-- ==============================================================================

SELECT 
    stt AS "STT",
    phan_he AS "Phân Hệ",
    ten_bang AS "Tên Bảng",
    mo_ta AS "Mô Tả Chức Năng",
    so_dong AS "Số Dòng Hiện Có",
    CASE 
        WHEN so_dong > 0 THEN '✅ Đã có dữ liệu'
        ELSE '❌ Chưa có dữ liệu'
    END AS "Trạng Thái Kiểm Tra"
FROM (
    SELECT  1 AS stt, 'Doanh nghiệp & Chi nhánh' AS phan_he, 'companies' AS ten_bang, 'Doanh nghiệp & Công ty chủ quản' AS mo_ta, count(*) AS so_dong FROM companies UNION ALL
    SELECT  2 AS stt, 'Doanh nghiệp & Chi nhánh' AS phan_he, 'regions' AS ten_bang, 'Khu vực địa lý (Tỉnh/Thành/Quận)' AS mo_ta, count(*) AS so_dong FROM regions UNION ALL
    SELECT  3 AS stt, 'Doanh nghiệp & Chi nhánh' AS phan_he, 'cafes' AS ten_bang, 'Chi nhánh quán cà phê' AS mo_ta, count(*) AS so_dong FROM cafes UNION ALL
    SELECT  4 AS stt, 'Doanh nghiệp & Chi nhánh' AS phan_he, 'seats' AS ten_bang, 'Sơ đồ bàn ghế tại chi nhánh' AS mo_ta, count(*) AS so_dong FROM seats UNION ALL
    SELECT  5 AS stt, 'Người dùng & Nhân sự' AS phan_he, 'users' AS ten_bang, 'Tài khoản người dùng hệ thống' AS mo_ta, count(*) AS so_dong FROM users UNION ALL
    SELECT  6 AS stt, 'Người dùng & Nhân sự' AS phan_he, 'user_profiles' AS ten_bang, 'Hồ sơ chi tiết người dùng' AS mo_ta, count(*) AS so_dong FROM user_profiles UNION ALL
    SELECT  7 AS stt, 'Người dùng & Nhân sự' AS phan_he, 'user_preferences' AS ten_bang, 'Sở thích cá nhân hóa' AS mo_ta, count(*) AS so_dong FROM user_preferences UNION ALL
    SELECT  8 AS stt, 'Người dùng & Nhân sự' AS phan_he, 'user_addresses' AS ten_bang, 'Sổ địa chỉ giao hàng' AS mo_ta, count(*) AS so_dong FROM user_addresses UNION ALL
    SELECT  9 AS stt, 'Người dùng & Nhân sự' AS phan_he, 'user_devices' AS ten_bang, 'Thiết bị đăng nhập người dùng' AS mo_ta, count(*) AS so_dong FROM user_devices UNION ALL
    SELECT 10 AS stt, 'Người dùng & Nhân sự' AS phan_he, 'staffs' AS ten_bang, 'Nhân sự & phân quyền quán' AS mo_ta, count(*) AS so_dong FROM staffs UNION ALL
    SELECT 11 AS stt, 'Người dùng & Nhân sự' AS phan_he, 'customers' AS ten_bang, 'Hồ sơ khách hàng thân thiết' AS mo_ta, count(*) AS so_dong FROM customers UNION ALL
    SELECT 12 AS stt, 'Thực đơn & Kho hàng' AS phan_he, 'categories' AS ten_bang, 'Danh mục sản phẩm & đồ uống' AS mo_ta, count(*) AS so_dong FROM categories UNION ALL
    SELECT 13 AS stt, 'Thực đơn & Kho hàng' AS phan_he, 'products' AS ten_bang, 'Sản phẩm & đồ uống' AS mo_ta, count(*) AS so_dong FROM products UNION ALL
    SELECT 14 AS stt, 'Thực đơn & Kho hàng' AS phan_he, 'product_variants' AS ten_bang, 'Biến thể kích cỡ (Size M/L, Đá, Đường)' AS mo_ta, count(*) AS so_dong FROM product_variants UNION ALL
    SELECT 15 AS stt, 'Thực đơn & Kho hàng' AS phan_he, 'units' AS ten_bang, 'Đơn vị tính (kg, g, L, ml, ly)' AS mo_ta, count(*) AS so_dong FROM units UNION ALL
    SELECT 16 AS stt, 'Thực đơn & Kho hàng' AS phan_he, 'ingredients' AS ten_bang, 'Nguyên vật liệu pha chế' AS mo_ta, count(*) AS so_dong FROM ingredients UNION ALL
    SELECT 17 AS stt, 'Thực đơn & Kho hàng' AS phan_he, 'suppliers' AS ten_bang, 'Nhà cung cấp nguyên liệu' AS mo_ta, count(*) AS so_dong FROM suppliers UNION ALL
    SELECT 18 AS stt, 'Thực đơn & Kho hàng' AS phan_he, 'allergens' AS ten_bang, 'Cảnh báo chất gây dị ứng' AS mo_ta, count(*) AS so_dong FROM allergens UNION ALL
    SELECT 19 AS stt, 'Đơn hàng & Đặt bàn' AS phan_he, 'carts' AS ten_bang, 'Giỏ hàng người dùng' AS mo_ta, count(*) AS so_dong FROM carts UNION ALL
    SELECT 20 AS stt, 'Đơn hàng & Đặt bàn' AS phan_he, 'cart_items' AS ten_bang, 'Chi tiết món trong giỏ hàng' AS mo_ta, count(*) AS so_dong FROM cart_items UNION ALL
    SELECT 21 AS stt, 'Đơn hàng & Đặt bàn' AS phan_he, 'orders' AS ten_bang, 'Đơn đặt hàng' AS mo_ta, count(*) AS so_dong FROM orders UNION ALL
    SELECT 22 AS stt, 'Đơn hàng & Đặt bàn' AS phan_he, 'order_items' AS ten_bang, 'Chi tiết món trong đơn hàng' AS mo_ta, count(*) AS so_dong FROM order_items UNION ALL
    SELECT 23 AS stt, 'Đơn hàng & Đặt bàn' AS phan_he, 'seat_reservations' AS ten_bang, 'Lịch đặt bàn trước' AS mo_ta, count(*) AS so_dong FROM seat_reservations UNION ALL
    SELECT 24 AS stt, 'Thanh toán & Tài chính' AS phan_he, 'payments' AS ten_bang, 'Bản ghi thanh toán đơn hàng' AS mo_ta, count(*) AS so_dong FROM payments UNION ALL
    SELECT 25 AS stt, 'Thanh toán & Tài chính' AS phan_he, 'payment_transactions' AS ten_bang, 'Giao dịch cổng thanh toán (MoMo, VNPay)' AS mo_ta, count(*) AS so_dong FROM payment_transactions UNION ALL
    SELECT 26 AS stt, 'Thanh toán & Tài chính' AS phan_he, 'refunds' AS ten_bang, 'Hoàn tiền đơn hàng' AS mo_ta, count(*) AS so_dong FROM refunds UNION ALL
    SELECT 27 AS stt, 'Thanh toán & Tài chính' AS phan_he, 'invoices' AS ten_bang, 'Hóa đơn điện tử VAT' AS mo_ta, count(*) AS so_dong FROM invoices UNION ALL
    SELECT 28 AS stt, 'Hội viên & Khuyến mãi' AS phan_he, 'loyalty_tiers' AS ten_bang, 'Hạng hội viên (Đồng, Bạc, Vàng, Kim Cương)' AS mo_ta, count(*) AS so_dong FROM loyalty_tiers UNION ALL
    SELECT 29 AS stt, 'Hội viên & Khuyến mãi' AS phan_he, 'loyalty_accounts' AS ten_bang, 'Tài khoản điểm tích lũy' AS mo_ta, count(*) AS so_dong FROM loyalty_accounts UNION ALL
    SELECT 30 AS stt, 'Hội viên & Khuyến mãi' AS phan_he, 'loyalty_transactions' AS ten_bang, 'Lịch sử giao dịch tích/tiêu điểm' AS mo_ta, count(*) AS so_dong FROM loyalty_transactions UNION ALL
    SELECT 31 AS stt, 'Hội viên & Khuyến mãi' AS phan_he, 'rewards' AS ten_bang, 'Danh mục quà tặng đổi điểm' AS mo_ta, count(*) AS so_dong FROM rewards UNION ALL
    SELECT 32 AS stt, 'Hội viên & Khuyến mãi' AS phan_he, 'reward_redemptions' AS ten_bang, 'Lịch sử đổi quà & voucher' AS mo_ta, count(*) AS so_dong FROM reward_redemptions UNION ALL
    SELECT 33 AS stt, 'Hội viên & Khuyến mãi' AS phan_he, 'membership_plans' AS ten_bang, 'Các gói hội viên định kỳ' AS mo_ta, count(*) AS so_dong FROM membership_plans UNION ALL
    SELECT 34 AS stt, 'Hội viên & Khuyến mãi' AS phan_he, 'subscriptions' AS ten_bang, 'Đăng ký gói hội viên của khách' AS mo_ta, count(*) AS so_dong FROM subscriptions UNION ALL
    SELECT 35 AS stt, 'Tín dụng & Chi phí' AS phan_he, 'credit_plans' AS ten_bang, 'Gói nạp tín dụng trả trước' AS mo_ta, count(*) AS so_dong FROM credit_plans UNION ALL
    SELECT 36 AS stt, 'Tín dụng & Chi phí' AS phan_he, 'user_credits' AS ten_bang, 'Số dư ví tín dụng người dùng' AS mo_ta, count(*) AS so_dong FROM user_credits UNION ALL
    SELECT 37 AS stt, 'Tín dụng & Chi phí' AS phan_he, 'credit_transactions' AS ten_bang, 'Lịch sử giao dịch biến động ví' AS mo_ta, count(*) AS so_dong FROM credit_transactions UNION ALL
    SELECT 38 AS stt, 'Hội viên & Khuyến mãi' AS phan_he, 'promotions' AS ten_bang, 'Chương trình khuyến mãi & coupon' AS mo_ta, count(*) AS so_dong FROM promotions UNION ALL
    SELECT 39 AS stt, 'Tín dụng & Chi phí' AS phan_he, 'expense_categories' AS ten_bang, 'Danh mục chi phí vận hành quán' AS mo_ta, count(*) AS so_dong FROM expense_categories UNION ALL
    SELECT 40 AS stt, 'Trợ lý AI & Thông báo' AS phan_he, 'ai_sessions' AS ten_bang, 'Phiên trò chuyện với Trợ lý AI Barista' AS mo_ta, count(*) AS so_dong FROM ai_sessions UNION ALL
    SELECT 41 AS stt, 'Trợ lý AI & Thông báo' AS phan_he, 'ai_messages' AS ten_bang, 'Lịch sử tin nhắn hỏi đáp AI' AS mo_ta, count(*) AS so_dong FROM ai_messages UNION ALL
    SELECT 42 AS stt, 'Trợ lý AI & Thông báo' AS phan_he, 'ai_usage' AS ten_bang, 'Thống kê lượng Token & chi phí AI' AS mo_ta, count(*) AS so_dong FROM ai_usage UNION ALL
    SELECT 43 AS stt, 'Trợ lý AI & Thông báo' AS phan_he, 'notification_templates' AS ten_bang, 'Mẫu nội dung thông báo hệ thống' AS mo_ta, count(*) AS so_dong FROM notification_templates UNION ALL
    SELECT 44 AS stt, 'Trợ lý AI & Thông báo' AS phan_he, 'notifications' AS ten_bang, 'Thông báo gửi tới người dùng' AS mo_ta, count(*) AS so_dong FROM notifications UNION ALL
    SELECT 45 AS stt, 'Hạ tầng hệ thống' AS phan_he, 'device_tokens' AS ten_bang, 'Mã token thiết bị (FCM / APNs)' AS mo_ta, count(*) AS so_dong FROM device_tokens UNION ALL
    SELECT 46 AS stt, 'Hạ tầng hệ thống' AS phan_he, 'refresh_tokens' AS ten_bang, 'Phiên đăng nhập JWT Refresh Token' AS mo_ta, count(*) AS so_dong FROM refresh_tokens UNION ALL
    SELECT 47 AS stt, 'Hạ tầng hệ thống' AS phan_he, 'audit_logs' AS ten_bang, 'Nhật ký kiểm toán thao tác hệ thống' AS mo_ta, count(*) AS so_dong FROM audit_logs UNION ALL
    SELECT 48 AS stt, 'Hạ tầng hệ thống' AS phan_he, 'translations' AS ten_bang, 'Bảng dịch đa ngôn ngữ giao diện (i18n)' AS mo_ta, count(*) AS so_dong FROM translations
) summary
ORDER BY stt;

-- ==============================================================================
-- GHI CHÚ: TẠI SAO CÁC LỆNH DƯỚI ĐÂY ĐƯỢC ĐẶT TRONG DẤU GHI CHÚ (--) ?
-- Trong pgAdmin, nếu trong 1 file có nhiều lệnh SELECT thì khi nhấn F5, pgAdmin 
-- chỉ hiển thị kết quả của lệnh SELECT CUỐI CÙNG và đè mất bảng 48 dòng ở trên.
-- Vì vậy, câu lệnh kiểm tra 48 bảng ở trên là câu lệnh thực thi duy nhất.
-- Khi bạn muốn xem chi tiết dữ liệu của bảng nào dưới đây, chỉ cần:
-- BÔI ĐEN (HIGHLIGHT) dòng lệnh đó và nhấn F5!
-- ==============================================================================

-- 1. DOANH NGHIỆP, CHI NHÁNH & MẶT BẰNG QUÁN
-- SELECT * FROM companies;
-- SELECT * FROM regions;
-- SELECT * FROM cafes;
-- SELECT * FROM seats;

-- 2. NGƯỜI DÙNG, HỒ SƠ & PHÂN QUYỀN NHÂN SỰ
-- SELECT * FROM users;
-- SELECT * FROM user_profiles;
-- SELECT * FROM user_preferences;
-- SELECT * FROM user_addresses;
-- SELECT * FROM user_devices;
-- SELECT * FROM staffs;
-- SELECT * FROM customers;

-- 3. THỰC ĐƠN, BIẾN THỂ & KHO NGUYÊN VẬT LIỆU
-- SELECT * FROM categories;
-- SELECT * FROM products;
-- SELECT * FROM product_variants;
-- SELECT * FROM units;
-- SELECT * FROM ingredients;
-- SELECT * FROM suppliers;
-- SELECT * FROM allergens;

-- 4. GIỎ HÀNG, ĐƠN ĐẶT HÀNG & ĐẶT BÀN
-- SELECT * FROM carts;
-- SELECT * FROM cart_items;
-- SELECT * FROM orders;
-- SELECT * FROM order_items;
-- SELECT * FROM seat_reservations;

-- 5. THANH TOÁN, HÓA ĐƠN & VÍ TÍN DỤNG
-- SELECT * FROM payments;
-- SELECT * FROM payment_transactions;
-- SELECT * FROM refunds;
-- SELECT * FROM invoices;
-- SELECT * FROM user_credits;
-- SELECT * FROM credit_plans;
-- SELECT * FROM credit_transactions;
-- SELECT * FROM expense_categories;

-- 6. HỘI VIÊN, ĐIỂM THƯỞNG, ĐỔI QUÀ & KHUYẾN MÃI
-- SELECT * FROM loyalty_tiers;
-- SELECT * FROM loyalty_accounts;
-- SELECT * FROM loyalty_transactions;
-- SELECT * FROM rewards;
-- SELECT * FROM reward_redemptions;
-- SELECT * FROM membership_plans;
-- SELECT * FROM subscriptions;
-- SELECT * FROM promotions;

-- 7. TRỢ LÝ AI, THÔNG BÁO, BẢO MẬT & HỆ THỐNG
-- SELECT * FROM ai_sessions;
-- SELECT * FROM ai_messages;
-- SELECT * FROM ai_usage;
-- SELECT * FROM notification_templates;
-- SELECT * FROM notifications;
-- SELECT * FROM device_tokens;
-- SELECT * FROM refresh_tokens;
-- SELECT * FROM audit_logs;
-- SELECT * FROM translations;

-- 8. CÁC BÁO CÁO JOIN NGHIỆP VỤ (Bôi đen đoạn cần xem rồi nhấn F5):
-- SELECT o.order_number, c.name AS cafe, u.full_name, o.total_amount, o.status, p.payment_method FROM orders o JOIN cafes c ON o.cafe_id = c.id JOIN users u ON o.user_id = u.id LEFT JOIN payments p ON p.order_id = o.id;
-- SELECT s.id, u.full_name, s.session_type, s.title, m.role, m.content FROM ai_sessions s JOIN users u ON s.user_id = u.id JOIN ai_messages m ON m.session_id = s.id ORDER BY s.id, m.created_at;
-- SELECT cat.name AS category, p.name AS product, pv.name AS variant, COALESCE(pv.price, p.price) AS price FROM products p JOIN categories cat ON p.category_id = cat.id LEFT JOIN product_variants pv ON pv.product_id = p.id ORDER BY cat.name, p.name;
