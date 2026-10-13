# AI Café Platform - Product Requirements Document

## 1. Overview

AI Café là mô hình kết hợp giữa quán cà phê + không gian làm việc + nền tảng AI.

## 2. Vision

Xây dựng AI Café Platform nơi quán trở thành điểm truy cập AI vật lý.

### Customer Journey

1. Mua đồ uống
2. Nhận AI Credits
3. Đăng nhập AI Café
4. Chọn nhiệm vụ
5. AI Café tự chọn model phù hợp
6. Sử dụng AI

## 3. Core Features

### MVP (Phase 1)
- [ ] User account
- [ ] QR login
- [ ] Order system
- [ ] Credit Wallet
- [ ] Credit Ledger
- [ ] 2-3 AI models
- [ ] Chat
- [ ] Coding
- [ ] AI Gateway
- [ ] Usage Tracking
- [ ] Cost Tracking
- [ ] Rate Limiting

### Phase 2
- [ ] Multiple AI Providers
- [ ] Model Router
- [ ] Premium Tier
- [ ] Image
- [ ] Workspace Timer
- [ ] Daily Limits
- [ ] Credit Expiration
- [ ] Promotions
- [ ] Membership
- [ ] Loyalty
- [ ] Employee Wallet
- [ ] Admin Dashboard

### Phase 3
- [ ] Video
- [ ] Public API
- [ ] Developer API Keys
- [ ] Team Accounts
- [ ] Organization Billing
- [ ] Advanced Analytics
- [ ] Provider Failover
- [ ] Automatic Cost Optimization
- [ ] Advanced Workspace

## 4. Customer Experience

1. Khách vào quán
2. Quét QR
3. Chọn đồ uống (Basic/Developer/Pro/Builder)
4. Thanh toán
5. Nhận: Credits + Workspace + AI Access
6. Mở app
7. Chọn: Code/Debug/Image/Video/Reasoning
8. AI Gateway xử lý

## 5. Technical Architecture

```
Browser → AI Café Backend → AI Gateway → AI Providers
```

### AI Gateway Responsibilities
- Authentication
- Authorization
- Credit Check
- Time Check
- Rate Limit
- Daily Limit
- Budget Check
- Model Router
- Usage Tracking
