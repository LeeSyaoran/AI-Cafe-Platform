# AI Café Platform - V1 Schema Placeholder

> **Lưu ý:** Schema đầy đủ 103 bảng rất lớn (~3000+ dòng SQL).
> Vui lòng tham khảo file schema V2 đầy đủ tại: [DATABASE_SCHEMA.md](../docs/DATABASE_SCHEMA.md) hoặc [ENHANCED_DATABASE_DESIGN.md](../docs/ENHANCED_DATABASE_DESIGN.md).

Schema gồm 16 modules:
1. **Company** (5) - companies, regions, cafes, cafe_seats, staffs
2. **Products** (12) - categories, products, variants, options, modifiers, allergens, recipes, combos
3. **Inventory** (8) - ingredients, stock_levels, suppliers, purchase_orders, etc.
4. **Orders** (7) - orders, order_items, payments, carts, etc.
6. **CRM** (5) - customers, loyalty_tiers, loyalty_transactions, reviews, segments
7. **Delivery** (5) - zones, partners, tracking, schedules
9. **Table Mgmt** (5) - reservations, table_sessions, waitlist, charges
10. **KDS** (3) - kitchen_orders, prep_stations, display_screens
11. **Financial** (6) - cash_drawers, expenses, commissions, payouts
12. **Gifts** (5) - gift_cards, vouchers, redemptions
13. **HR** (5) - schedules, attendance, payroll, leaves
14. **Marketing** (7) - campaigns, segments, referrals, coupons
15. **Support** (5) - tickets, complaints, suggestions
17. **Operations** (5) - print_jobs, audit_logs, notifications, devices
19. **Analytics** (4) - daily_summaries, metrics, dashboards
20. **Extras** (12) - translations, files, push_jobs, subscriptions, events, settings

Xem chi tiết schema, indexes, constraints, triggers trong các file docs đi kèm.