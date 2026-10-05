-- V1.0.1__Seed_Data.sql
-- Initial seed data for AI Café Platform

-- Insert default membership plans
INSERT INTO membership_plans (code, name, description, price_monthly, price_yearly, credits_monthly, discount_percent, free_delivery, priority_support, benefits) VALUES
('basic', 'Basic', 'Gói cơ bản với 100 credits/tháng', 99000, 990000, 100, 0, false, false,
 '["100 AI credits/tháng", "Sử dụng tất cả tính năng AI", "Hỗ trợ qua email"]'::jsonb),
('premium', 'Premium', 'Gói cao cấp với 500 credits/tháng và ưu đãi', 249000, 2490000, 500, 5, true, false,
 '["500 AI credits/tháng", "Giảm giá 5% cho đơn hàng", "Miễn phí giao hàng", "Ưu tiên hỗ trợ"]'::jsonb),
('vip', 'VIP', 'Gói VIP với 2000 credits/tháng và tất cả ưu đãi', 499000, 4990000, 2000, 10, true, true,
 '["2000 AI credits/tháng", "Giảm giá 10% cho đơn hàng", "Miễn phí giao hàng", "Hỗ trợ 24/7", "Quà tặng sinh nhật", "Early access tính năng mới"]'::jsonb)
ON CONFLICT (code) DO NOTHING;

-- Insert demo company
INSERT INTO companies (id, name, slug, description, logo_url) VALUES
('00000000-0000-0000-0000-000000000001', 'AI Café Demo', 'ai-cafe-demo', 'Nền tảng đặt đồ uống AI-powered đầu tiên tại Việt Nam', '/assets/logos/aicafe-logo.png')
ON CONFLICT (slug) DO NOTHING;

-- Insert demo cafe
INSERT INTO cafes (id, company_id, name, slug, description, address, province, district, phone, email, opening_hours, status) VALUES
('00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000001', 'AI Café Quận 1', 'ai-cafe-quan-1',
 'Cửa hàng đầu tiên của AI Café với không gian hiện đại', '123 Nguyễn Huệ, Phường Bến Nghé, Quận 1', 'TP. Hồ Chí Minh', 'Quận 1', '02812345678', 'quan1@aicafe.vn',
 '{"mon": "07:00-22:00", "tue": "07:00-22:00", "wed": "07:00-22:00", "thu": "07:00-22:00", "fri": "07:00-23:00", "sat": "08:00-23:00", "sun": "08:00-22:00"}'::jsonb, 'open')
ON CONFLICT (slug) DO NOTHING;

-- Insert demo categories
INSERT INTO categories (id, company_id, name, slug, icon, color, sort_order) VALUES
('00000000-0000-0000-0000-000000000020', '00000000-0000-0000-0000-000000000001', 'Cà Phê', 'ca-phe', 'coffee', '#6F4E37', 1),
('00000000-0000-0000-0000-000000000021', '00000000-0000-0000-0000-000000000001', 'Trà & Trà Sữa', 'tra-sua', 'tea', '#2E8B57', 2),
('00000000-0000-0000-0000-000000000022', '00000000-0000-0000-0000-000000000001', 'Nước Trái Cây', 'nuoc-trai-cay', 'juice', '#FF6347', 3),
('00000000-0000-0000-0000-000000000023', '00000000-0000-0000-0000-000000000001', 'Sinh Tố', 'sinh-to', 'smoothie', '#FFA07A', 4),
('00000000-0000-0000-0000-000000000024', '00000000-0000-0000-0000-000000000001', 'Bánh Ngọt', 'banh-ngot', 'cake', '#DDA0DD', 5)
ON CONFLICT (slug) DO NOTHING;

-- Insert demo products
INSERT INTO products (id, company_id, category_id, name, description, price, original_price, image_url, stock, is_active, is_featured, is_best_seller, preparation_time) VALUES
-- Coffee
('00000000-0000-0000-0000-000000000030', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000020', 'Cà Phê Đen', 'Cà phê Việt Nam rang xay truyền thống', 29000, 35000, '/assets/products/cafe-den.jpg', 100, true, false, true, 5),
('00000000-0000-0000-0000-000000000031', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000020', 'Cà Phê Sữa', 'Cà phê Việt Nam pha sữa đặc', 35000, 42000, '/assets/products/cafe-sua.jpg', 100, true, true, true, 5),
('00000000-0000-0000-0000-000000000032', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000020', 'Cappuccino', 'Espresso với bọt sữa mịn', 45000, 55000, '/assets/products/cappuccino.jpg', 80, true, false, false, 8),
('00000000-0000-0000-0000-000000000033', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000020', 'Latte', 'Espresso với sữa ấm', 48000, 58000, '/assets/products/latte.jpg', 80, true, true, false, 8),
-- Tea
('00000000-0000-0000-0000-000000000034', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000021', 'Trà Đá', 'Trà xanh đá mát lạnh', 20000, 25000, '/assets/products/tra-da.jpg', 200, true, false, false, 3),
('00000000-0000-0000-0000-000000000035', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000021', 'Trà Sữa Trân Châu', 'Trà sữa với trân châu dai giòn', 35000, 40000, '/assets/products/tra-sua.jpg', 150, true, true, true, 5),
('00000000-0000-0000-0000-000000000036', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000021', 'Trà Vải', 'Trà xanh với vải tươi', 38000, 45000, '/assets/products/tra-vai.jpg', 100, true, false, false, 4),
-- Juice
('00000000-0000-0000-0000-000000000037', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000022', 'Cam Vắt', 'Cam vắt tươi 100%', 40000, 48000, '/assets/products/cam-vat.jpg', 100, true, true, false, 4),
('00000000-0000-0000-0000-000000000038', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000022', 'Nước Ép Rau Má', 'Rau má xanh tươi, giải nhiệt', 25000, 30000, '/assets/products/rau-ma.jpg', 80, true, false, false, 3),
-- Smoothie
('00000000-0000-0000-0000-000000000039', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000023', 'Sinh Tố Bơ', 'Bơ chín xay mịn với sữa', 45000, 52000, '/assets/products/sinh-to-bo.jpg', 60, true, true, false, 5),
('00000000-0000-0000-0000-00000000003a', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000023', 'Smoothie Dâu', 'Dâu tây xay với sữa chua', 50000, 60000, '/assets/products/smoothie-dau.jpg', 50, true, false, false, 5),
-- Cake
('00000000-0000-0000-0000-00000000003b', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000024', 'Bánh Mì', 'Bánh mì bơ tỏi giòn rụm', 25000, 30000, '/assets/products/banh-mi.jpg', 50, true, false, false, 3),
('00000000-0000-0000-0000-00000000003c', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000024', 'Croissant', 'Bánh croissant bơ thơm ngậy', 35000, 42000, '/assets/products/croissant.jpg', 40, true, true, false, 2)
ON CONFLICT DO NOTHING;

-- Insert demo loyalty rewards
INSERT INTO rewards (code, name, description, reward_type, discount_type, discount_value, points_cost, validity_days, applicable_tiers) VALUES
('WELCOME10', 'Giảm 10% cho đơn hàng đầu tiên', 'Chào mừng khách hàng mới', 'discount', 'percentage', 10, 0, 30, 'bronze,silver,gold,platinum'),
('BRONZE50', 'Giảm 5K cho thành viên Bronze', 'Ưu đãi cho thành viên Bronze', 'discount', 'fixed_amount', 5000, 100, 14, 'bronze'),
('SILVER100', 'Giảm 10K cho thành viên Silver', 'Ưu đãi cho thành viên Silver', 'discount', 'fixed_amount', 10000, 200, 14, 'silver'),
('GOLD200', 'Giảm 20K cho thành viên Gold', 'Ưu đãi cho thành viên Gold', 'discount', 'fixed_amount', 20000, 400, 14, 'gold'),
('PLATINUM500', 'Giảm 50K cho thành viên Platinum', 'Ưu đãi cho thành viên Platinum', 'discount', 'fixed_amount', 50000, 800, 14, 'platinum'),
('FREE_DELIVERY', 'Miễn phí giao hàng', 'Miễn phí giao hàng cho đơn từ 100K', 'free_delivery', NULL, NULL, 150, 7, 'bronze,silver,gold,platinum')
ON CONFLICT (code) DO NOTHING;

-- Insert demo promotions
INSERT INTO promotions (code, name, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_date, end_date, usage_limit) VALUES
('WELCOME50', 'Chào mừng - Giảm 50K', 'Giảm 50K cho đơn hàng từ 200K', 'fixed_amount', 50000, 200000, 50000, NOW(), NOW() + INTERVAL '30 days', 1000),
('FREESHIP', 'Miễn phí giao hàng', 'Miễn phí giao hàng cho đơn từ 150K', 'free_delivery', NULL, 150000, NULL, NOW(), NOW() + INTERVAL '90 days', NULL),
('SUMMER30', 'Summer Sale - Giảm 30%', 'Giảm 30% cho đơn hàng mùa hè', 'percentage', 30, 100000, 100000, NOW(), NOW() + INTERVAL '60 days', 500)
ON CONFLICT (code) DO NOTHING;
