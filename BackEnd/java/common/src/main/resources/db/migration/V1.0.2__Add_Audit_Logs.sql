-- V1.0.2__Add_Audit_Logs.sql
-- Add audit logging tables for compliance

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID,
    old_data JSONB,
    new_data JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);

-- V1.0.3__Add_Seat_Reservations.sql
-- Add seat reservation tables

CREATE TABLE seats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE CASCADE,
    seat_number VARCHAR(20) NOT NULL,
    seat_type VARCHAR(20) DEFAULT 'table', -- table, booth, vip, outdoor
    capacity INTEGER DEFAULT 4,
    is_available BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) DEFAULT 'available', -- available, reserved, occupied, maintenance
    hourly_rate DECIMAL(10, 2), -- For paid seats
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE seat_reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seat_id UUID NOT NULL REFERENCES seats(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    order_id UUID REFERENCES orders(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'pending', -- pending, confirmed, checked_in, completed, cancelled, no_show
    notes TEXT,
    checked_in_at TIMESTAMP,
    checked_out_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_seats_cafe ON seats(cafe_id);
CREATE INDEX idx_seat_reservations_seat ON seat_reservations(seat_id);
CREATE INDEX idx_seat_reservations_user ON seat_reservations(user_id);
CREATE INDEX idx_seat_reservations_time ON seat_reservations(start_time, end_time);

-- V1.0.4__Add_Notifications.sql
-- Add notification tables

CREATE TABLE notification_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    channel VARCHAR(20) NOT NULL, -- email, sms, push, in_app
    subject VARCHAR(255),
    body_template TEXT NOT NULL,
    variables JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    template_id UUID REFERENCES notification_templates(id),
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    data JSONB,
    channel VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending', -- pending, sent, failed, read
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_created ON notifications(created_at);

-- Insert notification templates
INSERT INTO notification_templates (code, name, channel, subject, body_template) VALUES
('order_confirmed', 'Xác nhận đơn hàng', 'in_app', 'Đơn hàng #{{order_number}} đã được xác nhận',
 'Cảm ơn bạn! Đơn hàng #{{order_number}} của bạn đã được xác nhận và đang được chuẩn bị.'),
('order_ready', 'Đơn hàng sẵn sàng', 'in_app', 'Đơn hàng #{{order_number}} đã sẵn sàng!',
 'Đơn hàng #{{order_number}} của bạn đã được chuẩn bị xong. Vui lòng đến nhận.'),
('payment_success', 'Thanh toán thành công', 'in_app', 'Thanh toán thành công',
 'Thanh toán {{amount}} cho đơn hàng #{{order_number}} đã được xử lý thành công.'),
('points_earned', 'Tích điểm thành công', 'in_app', 'Bạn đã nhận được {{points}} điểm!',
 'Cảm ơn bạn đã mua sắm! Bạn đã nhận được {{points}} điểm thưởng.'),
('tier_upgrade', 'Thăng hạng thành công', 'in_app', 'Chúc mừng bạn đã thăng hạng!',
 'Chúc mừng! Bạn đã được nâng lên hạng {{new_tier}}. Hãy tận hưởng các ưu đãi mới!')
ON CONFLICT (code) DO NOTHING;

-- V1.0.5__Add_AI_Chat_History.sql
-- Add AI chat history tables

CREATE TABLE ai_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_type VARCHAR(30) NOT NULL, -- chat, recipe, recommendation
    title VARCHAR(255),
    metadata JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    last_message_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ai_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES ai_sessions(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL, -- user, assistant, system
    content TEXT NOT NULL,
    model VARCHAR(50),
    tokens_used INTEGER,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_sessions_user ON ai_sessions(user_id);
CREATE INDEX idx_ai_messages_session ON ai_messages(session_id);

COMMENT ON TABLE ai_sessions IS 'AI conversation sessions';
COMMENT ON TABLE ai_messages IS 'Individual messages in AI conversations';
