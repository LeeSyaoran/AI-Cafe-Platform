-- ================================================
-- AI CAFÉ PLATFORM - SAMPLE DATA
-- ================================================

-- Insert company
INSERT INTO companies (id, company_code, name, legal_name, tax_id, address, phone, email, description, is_active)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'AICAFE',
    'AI Café Platform',
    'Công Ty Cổ Phần AI Café',
    '0123456789',
    '123 Nguyễn Huệ, Quận 1, TP.HCM',
    '02812345678',
    'contact@aicafe.vn',
    'Nền tảng đặt đồ uống thông minh với AI',
    true
);

-- Insert regions
INSERT INTO regions (id, company_id, name, code, region_type, sort_order)
VALUES
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'TP. Hồ Chí Minh', 'HCM', 'city', 1),
    ('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'Hà Nội', 'HN', 'city', 2);

-- Insert cafes
INSERT INTO cafes (id, company_id, region_id, cafe_code, name, slug, description, address, province, district, phone, is_active, is_delivery_enabled, is_pickup_enabled, is_reservation_enabled, tax_rate)
VALUES
    ('c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'AIC001', 'AI Café Quận 1', 'ai-cafe-quan-1', 'Quán cà phê hiện đại với không gian sáng tạo', '123 Nguyễn Huệ, Quận 1', 'TP. Hồ Chí Minh', 'Quận 1', '02812345601', true, true, true, true, 0.1),
    ('c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'AIC002', 'AI Café Quận 3', 'ai-cafe-quan-3', 'Không gian yên tĩnh để làm việc', '456 Điện Biên Phủ, Quận 3', 'TP. Hồ Chí Minh', 'Quận 3', '02812345602', true, true, true, false, 0.1),
    ('c0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'AIC003', 'AI Café Ba Đình', 'ai-cafe-ba-dinh', 'Quán cà phê AI đầu tiên tại Hà Nội', '78 Hoàng Hoa Thám, Ba Đình', 'Hà Nội', 'Ba Đình', '02412345603', true, true, false, false, 0.1);

-- Insert categories
INSERT INTO categories (id, company_id, name, slug, icon, color, sort_order, is_active, is_featured)
VALUES
    ('d0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'Cà Phê', 'ca-phe', 'coffee', '#6F4E37', 1, true, true),
    ('d0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'Trà', 'tra', 'tea', '#98D8AA', 2, true, true),
    ('d0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000001', 'Nước Ép & Sinh Tố', 'nuoc-ep-sinh-to', 'juice', '#FFB347', 3, true, true),
    ('d0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000001', 'Smoothie & Mocktail', 'smoothie-mocktail', 'smoothie', '#FF6B6B', 4, true, false),
    ('d0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000001', 'Bánh & Snack', 'banh-snack', 'cake', '#DDA0DD', 5, true, false),
    ('d0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000001', 'Set Combo', 'set-combo', 'combo', '#4ECDC4', 6, true, true);

-- Insert products
INSERT INTO products (id, company_id, category_id, name, name_vi, slug, sku, description, short_description, price, image_url, calories, preparation_time, is_active, is_featured, is_best_seller)
VALUES
    -- Cà Phê
    ('e0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000001', 'Cà Phê Đen', 'Black Coffee', 'ca-phe-den', 'CP001', 'Cà phê rang xay nguyên chất 100% Robusta', 'Thức uống classic', 25000, 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400', 5, 3, true, false, true),
    ('e0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000001', 'Cà Phê Sữa', 'Vietnamese Coffee', 'ca-phe-sua', 'CP002', 'Cà phê Việt Nam pha sữa đặc truyền', 'Hương vị quen thuộc', 29000, 'https://images.unsplash.com/photo-1578314675249-a6910f80cc39?w=400', 80, 3, true, true, true),
    ('e0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000001', 'Cappuccino', 'Cappuccino', 'cappuccino', 'CP003', 'Espresso với bọt sữa mịn', 'Italy authentic', 45000, 'https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=400', 120, 4, true, false, false),
    ('e0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000001', 'Latte', 'Latte', 'latte', 'CP004', 'Espresso với sữa nóng', 'Smooth & creamy', 45000, 'https://images.unsplash.com/photo-1561882468-9110e03e0f78?w=400', 150, 4, true, false, false),
    ('e0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000001', 'Mocha', 'Mocha', 'mocha', 'CP005', 'Espresso với chocolate và sữa', 'Chocolate lover', 50000, 'https://images.unsplash.com/photo-1578314675249-a6910f80cc39?w=400', 200, 4, true, true, false),

    -- Trà
    ('e0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000002', 'Trà Đen', 'Black Tea', 'tra-den', 'TR001', 'Trà đen nguyên lá Darjeeling', 'Classic tea', 25000, 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=400', 2, 2, true, false, false),
    ('e0000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000002', 'Trà Sữa Trân Châu', 'Bubble Tea', 'tra-sua-tran-chau', 'TR002', 'Trà sữa với trân châu hoàng gia', 'Best seller', 35000, 'https://images.unsplash.com/photo-1558857563-b371033873b8?w=400', 250, 3, true, true, true),
    ('e0000000-0000-0000-0000-000000000008', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000002', 'Trà Đào', 'Peach Tea', 'tra-dao', 'TR003', 'Trà xanh với đào hũ thơm ngon', 'Refreshing', 32000, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', 100, 3, true, false, false),
    ('e0000000-0000-0000-0000-000000000009', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000002', 'Matcha Latte', 'Matcha Latte', 'matcha-latte', 'TR004', 'Matcha Nhật Bản với sữa', 'Japanese style', 55000, 'https://images.unsplash.com/photo-1515823064-d6e0c04616a7?w=400', 180, 4, true, true, false),

    -- Nước Ép
    ('e0000000-0000-0000-0000-000000000010', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000003', 'Nước Ép Cam', 'Orange Juice', 'nuoc-ep-cam', 'NE001', 'Cam vắt tươi 100%', 'Fresh daily', 35000, 'https://images.unsplash.com/photo-1534353473418-4cfa6c56fd38?w=400', 90, 2, true, false, false),
    ('e0000000-0000-0000-0000-000000000011', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000003', 'Sinh Tố Bơ', 'Avocado Smoothie', 'sinh-to-bo', 'NE002', 'Bơ tươi xay với sữa và đường', 'Creamy delight', 40000, 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400', 200, 3, true, true, false),

    -- Smoothie
    ('e0000000-0000-0000-0000-000000000012', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000004', 'Smoothie Dâu', 'Strawberry Smoothie', 'smoothie-dau', 'SM001', 'Dâu tây xay với sữa chua', 'Berry blast', 45000, 'https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=400', 150, 3, true, false, false),
    ('e0000000-0000-0000-0000-000000000013', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000004', 'Mocktail Tropical', 'Tropical Mocktail', 'mocktail-tropical', 'SM002', 'Xoài, dứa,chanh dây', 'Tropical vibes', 50000, 'https://images.unsplash.com/photo-1536935338788-846bb9981813?w=400', 100, 3, true, false, false),

    -- Bánh
    ('e0000000-0000-0000-0000-000000000014', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000005', 'Bánh Mì Bơ Tỏi', 'Butter Garlic Bread', 'banh-mi-bo-toi', 'BN001', 'Bánh mì nướng bơ tỏi thơm phức', 'Crunchy snack', 22000, 'https://images.unsplash.com/photo-1541529086526-db283c563270?w=400', 180, 5, true, false, false),
    ('e0000000-0000-0000-0000-000000000015', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000005', 'Croissant Bơ', 'Butter Croissant', 'croissant-bo', 'BN002', 'Bánh croissant bơ Pháp', 'French style', 35000, 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400', 250, 3, true, false, false),

    -- Combo
    ('e0000000-0000-0000-0000-000000000016', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000006', 'Combo Cà Phê Sáng', 'Morning Coffee Combo', 'combo-ca-phe-sang', 'CM001', 'Cà phê sữa + Bánh mì bơ tỏi', 'Perfect breakfast', 49000, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400', 260, 5, true, true, true),
    ('e0000000-0000-0000-0000-000000000017', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000006', 'Combo Trà Chiều', 'Afternoon Tea Combo', 'combo-tra-chieu', 'CM002', 'Trà đen + Croissant', 'Sweet afternoon', 55000, 'https://images.unsplash.com/photo-1498804103079-a6351b050096?w=400', 252, 5, true, false, false);
