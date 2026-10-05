# FrontEnd

Tất cả client apps: Web (khách hàng), Mobile (Android + iOS), BackOffice (admin), KDS (bếp).

## Cấu trúc

```
FrontEnd/
├── Web/         # Customer web app — Next.js 14 (App Router)
│                # URL: https://aicafe.vn
│
├── Mobile/      # React Native + Expo
│                # Single codebase → build cho Android & iOS
│                # Cùng features như Web, thêm push notification, offline cache
│
├── BackOffice/  # Admin dashboard — Next.js (React)
│                # Quản lý menu, orders, staff, inventory, analytics
│
└── KDS/         # Kitchen Display System — React + Vite
                 # Hiển thị order queue real-time cho bếp
```

## Tech stack

| App | Framework | UI | State |
|-----|-----------|-----|-------|
| Web | Next.js 14 | Tailwind + Radix | Zustand + React Query |
| Mobile | React Native + Expo | Native Wind | Zustand + React Query |
| BackOffice | Next.js | Ant Design | Redux Toolkit |
| KDS | React + Vite | Tailwind | Zustand + WebSocket |

## Apps chính

| App | Users | Mục đích |
|-----|-------|---------|
| **Web** | Khách hàng | Duyệt menu, đặt hàng, loyalty, thanh toán online |
| **Mobile** | Khách hàng | Cùng web + push notification, offline mode, native UX |
| **BackOffice** | Admin / Manager | Quản lý toàn bộ hệ thống |
| **KDS** | Nhân viên bếp | Hiển thị order, cập nhật trạng thái |

## API Base URL

```
Production:  https://api.aicafe.vn/v1
Staging:     https://api-staging.aicafe.vn/v1
Local:       http://localhost:3000/v1
```

## Build & deploy

```bash
# Web
cd Web && npm run build && npm start

# Mobile (Android + iOS từ 1 codebase)
cd Mobile && eas build --platform android
cd Mobile && eas build --platform ios
```

## Liên kết backend

- API Gateway: [`../BackEnd/services/api-gateway/`](../BackEnd/services/api-gateway/)
- API docs: [`../BackEnd/docs/REST_API.md`](../BackEnd/docs/REST_API.md)