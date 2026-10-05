# AI Café Platform - Enhanced Database Design (F&B Focus)

## Phân Tích Yêu Cầu

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         F&B BUSINESS REQUIREMENTS                                   │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  QUY MÔ DỰ ÁN:                                                                    │
│  ════════════════                                                                   │
│  • 1000+ quán cafe trên toàn quốc                                                 │
│  • 50,000+ sản phẩm (menu items)                                                 │
│  • 1,000,000+ đơn hàng/tháng                                                     │
│  • 10,000,000+ messages AI/đêm                                                    │
│  • 5,000,000+ người dùng                                                         │
│                                                                                      │
│  TÍNH NĂNG CẦN THIẾT:                                                              │
│  ═══════════════════════                                                           │
│  ☕ Quản lý Menu đa nhánh (multi-cafe)                                            │
│  📦 Quản lý Kho (inventory)                                                       │
│  🚚 Quản lý Nhà cung cấp (suppliers)                                             │
│  📋 Công thức pha chế (recipes)                                                   │
│  🍽️ Combo/Set meals                                                                │
│  📊 Báo cáo Doanh thu                                                             │
│  🎁 Chương trình Khách hàng thân thiết (loyalty)                                  │
│  🔄 Đồng bộ Offline (POS)                                                        │
│  📱 Kitchen Display System (KDS)                                                  │
│  📦 Delivery Integration                                                           │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 1. Complete Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                          FULL ERD - AI CAFÉ PLATFORM                                               │
├─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                                                  │
│                                                                                                                  │
│  ═══════════════════════════════════════════════════════════════════════════════════════════════════════════      │
│  COMPANY STRUCTURE                                                                                              │
│  ═══════════════════════════════════════════════════════════════════════════════════════════════════════════      │
│                                                                                                                  │
│       ┌─────────────┐         ┌─────────────┐         ┌─────────────┐         ┌─────────────┐            │
│       │  companies  │         │   regions   │         │   districts  │         │   wards     │            │
│       ├─────────────┤         ├─────────────┤         ├─────────────┤         ├─────────────┤            │
│       │ PK id       │────────▶│ PK id       │────────▶│ PK id       │────────▶│ PK id       │            │
│       │ PK company_id│        │ FK company_id│        │ FK region_id │        │ FK district │            │
│       │    name     │        │    name    │         │    name     │         │    name    │            │
│       │    tax_id   │        │    code    │         │    code     │         │    code    │            │
│       │    address  │        └─────────────┘         └─────────────┘         └─────────────┘            │
│       │    phone    │                                                                          │
│       └──────┬──────┘                                                                          │
│              │                                                                                  │
│              │ 1:N                                                                             │
│              ▼                                                                                  │
│       ┌─────────────┐                                                    ┌─────────────┐              │
│       │   cafes    │◀──────────────────────────────────────────────────│   staffs    │              │
│       ├─────────────┤                                                    ├─────────────┤              │
│       │ PK id       │                                                    │ PK id       │              │
│       │ FK company_id│                                                   │ FK cafe_id  │              │
│       │ FK region_id │                                                   │ FK user_id  │──────────────┐│
│       │ FK district │                                                    │    role     │              ││
│       │ FK ward_id  │                                                    │    status   │              ││
│       │    name     │                                                    │    code     │              ││
│       │    slug     │                                                    └─────────────┘              ││
│       │    code     │◀─── cafe_code format: "AIC-001"                                              ││
│       │    address  │                                                                                  ││
│       │    phone    │      ┌─────────────┐         ┌─────────────┐         ┌─────────────┐            ││
│       │    email    │      │    seats    │◀────────│ reservations │────────▶│    users    │            ││
│       │    status   │      ├─────────────┤         ├─────────────┤         ├─────────────┤            ││
│       │    hours    │      │ PK id       │         │ PK id       │         │ PK id       │            ││
│       └──────┬──────┘      │ FK cafe_id  │         │ FK cafe_id  │         │    email    │            ││
│              │             │ FK seat_id  │         │ FK user_id  │────────▶│    password │            ││
│              │             │    number   │         │    code     │         │    role     │            ││
│              │             │    zone     │         │    status   │         │    status   │            ││
│              │             │    capacity │         │    date     │         └─────────────┘            ││
│              │             └─────────────┘         │    time     │               │                   ││
│              │                                    └─────────────┘               │                   ││
│              │                                                               │                   ││
│              ▼                                                               │                   ││
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│  MENU & PRODUCTS                                                                                               │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│                                                                                                                  │
│       ┌─────────────┐         ┌─────────────┐         ┌─────────────┐         ┌─────────────┐            │
│       │ categories │         │  products   │         │   brands    │         │  allergens  │            │
│       ├─────────────┤         ├─────────────┤         ├─────────────┤         ├─────────────┤            │
│       │ PK id       │───────▶│ PK id       │         │ PK id       │         │ PK id       │            │
│       │ FK cafe_id  │        │ FK cafe_id  │         │    name     │         │    name    │            │
│       │ FK parent_id│        │ FK category_id│        │    logo    │         │    icon    │            │
│       │    name     │◀──────│ FK brand_id │         └─────────────┘         └─────────────┘            │
│       │    slug     │        │    name     │                                                         │
│       │    image    │        │    sku     │         ┌─────────────┐         ┌─────────────┐            │
│       │    icon    │        │    barcode │         │product_allergens│     │ nutritional_info│         │
│       │    color    │        │    price   │         ├─────────────┤         ├─────────────┤            │
│       │    sort     │        │    status  │         │ PK FK product_id│    │ PK FK product_id│         │
│       └─────────────┘        │    image   │         │ FK allergen_id │      │    calories │            │
│                              │    tags    │         └─────────────┘         │    protein  │            │
│       ┌─────────────┐        │    sold   │                                  │    carbs    │            │
│       │product_images│       │    rating │         ┌─────────────┐         │    fat      │            │
│       ├─────────────┤        └──────┬──────┘         │   recipes   │         │    sugar   │            │
│       │ PK id       │               │                ├─────────────┤         │    caffeine│            │
│       │ FK product_id│               │                │ PK id       │         └─────────────┘            │
│       │    url     │               │                │ FK product_id│                                  │
│       │   is_primary│               │                │    version  │         ┌─────────────┐            │
│       │    sort    │               │                │    steps    │         │ product_modifiers│         │
│       └─────────────┘               │                │    yield    │         ├─────────────┤            │
│                                     │                │    yield_unit│        │ PK id       │            │
│       ┌─────────────┐               │                └──────┬──────┘         │ FK product_id│           │
│       │product_variants│             │                       │                │    name     │            │
│       ├─────────────┤                │                       │                │    price   │            │
│       │ PK id       │                │                       ▼                │    is_active│           │
│       │ FK product_id│                │                ┌─────────────┐         └─────────────┘            │
│       │    name     │                │                │ recipe_ingredients │                                   │
│       │    sku     │                │                ├─────────────┤                                    │
│       │    price   │                │                │ PK id       │                                    │
│       │    stock   │                │                │ FK recipe_id│                                    │
│       │    status  │                │                │ FK ingredient_id│                                │
│       └─────────────┘                │                │    quantity │                                    │
│                                     │                │    unit    │                                    │
│       ┌─────────────┐               │                └─────────────┘                                    │
│       │product_options│              │                                                                   │
│       ├─────────────┤                │                                                                   │
│       │ PK id       │                │                                                                   │
│       │ FK product_id│               │                                                                   │
│       │    name     │                │                                                                   │
│       │    type    │                │                                                                   │
│       │    required│                │                                                                   │
│       └──────┬──────┘                │                                                                   │
│              │                       │                                                                   │
│              │ 1:N                   │                                                                   │
│              ▼                       │                                                                   │
│       ┌─────────────┐                │                                                                   │
│       │ option_values│               │                                                                   │
│       ├─────────────┤                │                                                                   │
│       │ PK id       │                │                                                                   │
│       │ FK option_id│                │                                                                   │
│       │    value   │                │                                                                   │
│       │    price   │                │                                                                   │
│       │    is_default│              │                                                                   │
│       │    sort    │                │                                                                   │
│       └─────────────┘                │                                                                   │
│                                     │                                                                   │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│  INVENTORY & SUPPLY CHAIN                                                                                    │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│                                                                                                                  │
│       ┌─────────────┐         ┌─────────────┐         ┌─────────────┐         ┌─────────────┐            │
│       │ ingredients│         │  suppliers  │         │supplier_items│       │ purchase_orders│           │
│       ├─────────────┤         ├─────────────┤         ├─────────────┤         ├─────────────┤            │
│       │ PK id       │◀────────│ PK id       │         │ PK id       │         │ PK id       │            │
│       │    name     │        │    name     │────────▶│ FK supplier_id│       │ FK cafe_id │             │
│       │    sku     │        │    code     │        │ FK ingredient_id│      │ FK supplier_id│          │
│       │    category│        │    contact  │         │    sku     │         │    code     │            │
│       │ FK unit_id │        │    phone    │         │    price  │         │    status  │            │
│       │    min_stock│       │    email    │         │    min_qty │         │    total   │            │
│       │    max_stock│       └─────────────┘         └─────────────┘         │    notes   │            │
│       └──────┬──────┘                                                                              │            │
│              │                                                                                     │            │
│              │ 1:N                                                                                │            │
│              ▼                                                                                     │            │
│       ┌─────────────┐         ┌─────────────┐         ┌─────────────┐                              │
│       │ ingredient_stock│     │ stock_transactions│     │ stock_alerts│                              │
│       ├─────────────┤         ├─────────────┤         ├─────────────┤                              │
│       │ PK id       │◀────────│ PK id       │         │ PK id       │                              │
│       │ FK cafe_id  │        │ FK cafe_id  │         │ FK cafe_id  │                              │
│       │ FK ingredient_id│    │ FK ingredient_id│    │ FK ingredient_id│                           │
│       │    quantity │        │ FK order_item_id │     │    type    │                              │
│       │    cost     │        │    type   │         │    message │                              │
│       │    expiry  │        │    quantity│         │    is_read │                              │
│       │    batch   │        │    notes   │         └─────────────┘                              │
│       └─────────────┘        └─────────────┘                                                       │
│                                                                                                                  │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│  ORDERS & PAYMENTS                                                                                             │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│                                                                                                                  │
│       ┌─────────────┐         ┌─────────────┐         ┌─────────────┐         ┌─────────────┐            │
│       │   orders    │         │  order_items │         │   payments   │         │ promotions  │            │
│       ├─────────────┤         ├─────────────┤         ├─────────────┤         ├─────────────┤            │
│       │ PK id       │────────▶│ PK id       │         │ PK id       │         │ PK id       │            │
│       │ FK cafe_id  │        │ FK order_id │         │ FK order_id │────────▶│ FK cafe_id  │            │
│       │ FK user_id  │────────▶│ FK product_id│        │ FK user_id  │         │    code    │            │
│       │ FK staff_id │        │ FK variant_id│         │    method   │         │    name    │            │
│       │ FK seat_id  │        │ FK recipe_id│         │    amount   │         │    type    │            │
│       │    code     │        │    qty     │         │    status   │         │    value   │            │
│       │    type    │        │    price   │         │    txn_id   │         │    min_order│            │
│       │    status   │        │    options │         │    paid_at  │         │    max_use │            │
│       │    subtotal│        │    notes   │         └─────────────┘         │    start   │            │
│       │    tax      │        │    prep_status│                                                 │    end    │            │
│       │    discount│        └─────────────┘         ┌─────────────┐         │    status  │            │
│       │    total   │                                 │order_item_options│       └─────────────┘            │
│       │    paid    │                                 ├─────────────┤                                    │
│       └──────┬──────┘                                 │ PK id       │         ┌─────────────┐            │
│              │                                        │ FK order_item_id│       │ promo_usage │            │
│              │                                        │ FK option_id│         ├─────────────┤            │
│              │                                        │    value   │         │ FK promo_id │            │
│              ▼                                        │    price   │         │ FK user_id  │            │
│       ┌─────────────┐                                └─────────────┘         │ FK order_id │            │
│       │ order_status_log│                                                               │    discount│            │
│       ├─────────────┤                                                                └─────────────┘            │
│       │ PK id       │                                                                                          │
│       │ FK order_id │         ┌─────────────┐         ┌─────────────┐         ┌─────────────┐            │
│       │    from     │         │   combos    │         │   combo_items│        │    carts    │            │
│       │    to      │         ├─────────────┤         ├─────────────┤         ├─────────────┤            │
│       │ FK staff_id │         │ PK id       │────────▶│ PK id       │         │ PK id       │            │
│       │    note    │         │ FK cafe_id  │        │ FK combo_id │         │ FK user_id  │            │
│       │    created │         │    name    │        │ FK product_id│        │ FK cafe_id  │            │
│       └─────────────┘         │    price   │        │ FK variant_id│        │    status   │            │
│                               │    status  │        │    qty     │         └──────┬──────┘            │
│       ┌─────────────┐         │    start   │        │    discount│                 │                   │
│       │order_item_modifiers│  │    end     │        └─────────────┘                 │                   │
│       ├─────────────┤         └─────────────┘                                        ▼                   │
│       │ PK id       │                                                               ┌─────────────┐      │
│       │ FK order_item_id│                                                             │ cart_items │      │
│       │ FK modifier_id│                                                               ├─────────────┤      │
│       │    price   │                                                                │ PK id       │      │
│       └─────────────┘                                                                │ FK cart_id  │      │
│                                                                                     │ FK product_id│     │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│  AI SERVICES & CREDITS                                                                                        │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│                                                                                                                  │
│       ┌─────────────┐         ┌─────────────┐         ┌─────────────┐         ┌─────────────┐            │
│       │ ai_sessions │         │ ai_messages │         │ ai_models   │         │ ai_prompts  │            │
│       ├─────────────┤         ├─────────────┤         ├─────────────┤         ├─────────────┤            │
│       │ PK id       │────────▶│ PK id       │         │ PK id       │         │ PK id       │            │
│       │ FK user_id  │        │ FK session_id│         │    name     │         │ FK user_id  │            │
│       │ FK model_id │        │    role    │         │    provider │         │    name    │            │
│       │    title    │        │    content │         │    input_cost│        │    prompt  │            │
│       │    status   │        │   tokens   │         │    output_cost│       │    is_public│           │
│       │    tokens   │        │    cost    │         │    max_tokens│        │    tags    │            │
│       │    cost     │        │   cached   │         │    context  │         └─────────────┘            │
│       └──────┬──────┘        │   latency  │         │    is_active│                                    │
│              │               └─────────────┘         └─────────────┘         ┌─────────────┐            │
│              │                                                                 │ ai_usage_daily│           │
│              │                                                                 ├─────────────┤            │
│              ▼                                                                 │ PK DATE      │            │
│       ┌─────────────┐                                                         │ FK user_id  │            │
│       │user_credits │                                                         │ FK model_id │            │
│       ├─────────────┤                                                         │    tokens   │            │
│       │ PK FK user_id│                                                        │    cost    │            │
│       │    balance  │         ┌─────────────┐         ┌─────────────┐         └─────────────┘            │
│       │    frozen   │         │credit_transactions│     │    plans   │                                    │
│       │    lifetime │         ├─────────────┤         ├─────────────┤                                    │
│       └─────────────┘         │ PK id       │         │ PK id       │         ┌─────────────┐            │
│                               │ FK user_id  │         │ FK cafe_id  │         │   orders   │            │
│                               │    type    │         │    name    │         ├─────────────┤            │
│                               │   amount   │         │    credits │         │ FK user_id  │            │
│                               │   balance  │         │    price  │         │ FK plan_id  │            │
│                               │   reference│         │    period │         │    status  │            │
│                               │    created │         │    features│         └─────────────┘            │
│                               └─────────────┘         └─────────────┘                                    │
│                                                                                                                  │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│  CUSTOMER RELATIONSHIP & LOYALTY                                                                                │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│                                                                                                                  │
│       ┌─────────────┐         ┌─────────────┐         ┌─────────────┐         ┌─────────────┐            │
│       │  customers  │         │   visits    │         │  loyalty_tiers │      │  loyalty_points│          │
│       ├─────────────┤         ├─────────────┤         ├─────────────┤         ├─────────────┤            │
│       │ PK FK user_id│◀───────│ PK id       │         │ PK id       │         │ PK id       │            │
│       │    tier_id │────────▶│ FK user_id  │         │    name    │────────▶│ FK user_id  │            │
│       │    total_spent│      │ FK cafe_id  │         │    min_points│        │    points  │            │
│       │    visit_count│       │ FK order_id │         │    discount%│         │    type    │            │
│       │    last_visit │       │    type    │         │    perks   │         │    expires │            │
│       │    birthday  │        │    created │         │    color   │         │    reference│           │
│       └─────────────┘        └─────────────┘         └─────────────┘         └─────────────┘            │
│                                                                                                                  │
│       ┌─────────────┐         ┌─────────────┐         ┌─────────────┐                                    │
│       │ customer_addresses│   │ customer_preferences│   │ customer_tags │                                │
│       ├─────────────┤         ├─────────────┤         ├─────────────┤                                    │
│       │ PK id       │         │ PK FK customer_id│     │ PK FK customer_id│                                │
│       │ FK customer_id│       │ FK product_id│         │ FK tag_id  │                                    │
│       │    label   │         │    liked   │         └─────────────┘                                    │
│       │    address │         │    disliked│         ┌─────────────┐                                    │
│       │    is_default│        │    notes  │         │    tags    │                                    │
│       └─────────────┘         └─────────────┘         ├─────────────┤                                    │
│                                                     │ PK id       │                                    │
│       ┌─────────────┐                                │    name    │                                    │
│       │    reviews   │                                │    color   │                                    │
│       ├─────────────┤                                └─────────────┘                                    │
│       │ PK id       │                                                                          │
│       │ FK user_id  │                                                                          │
│       │ FK product_id│                                                                         │
│       │ FK cafe_id  │                                                                          │
│       │ FK order_id │                                                                          │
│       │    rating   │                                                                          │
│       │    comment  │                                                                          │
│       │    images   │                                                                          │
│       │    status   │                                                                          │
│       └─────────────┘                                                                          │
│                                                                                                                  │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│  OPERATIONS & NOTIFICATIONS                                                                                     │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│                                                                                                                  │
│       ┌─────────────┐         ┌─────────────┐         ┌─────────────┐         ┌─────────────┐            │
│       │print_jobs  │         │kitchen_stations│       │    shifts   │         │    tasks    │            │
│       ├─────────────┤         ├─────────────┤         ├─────────────┤         ├─────────────┤            │
│       │ PK id       │         │ PK id       │         │ PK id       │         │ PK id       │            │
│       │ FK cafe_id  │         │ FK cafe_id  │         │ FK cafe_id  │         │ FK cafe_id  │            │
│       │ FK order_id │         │    name    │         │ FK staff_id │         │ FK assignee │            │
│       │    type    │         │    type    │         │    date    │         │    title    │            │
│       │    status  │         │    status  │         │    start   │         │    status   │            │
│       │    printed_at│        └─────────────┘         │    end     │         │    priority │            │
│       └─────────────┘                                  │    status  │         │    due_date │            │
│                                                         └─────────────┘         └─────────────┘            │
│       ┌─────────────┐         ┌─────────────┐                                                                   │
│       │notifications│         │  audit_logs  │                                                                   │
│       ├─────────────┤         ├─────────────┤                                                                   │
│       │ PK id       │         │ PK id       │                                                                   │
│       │ FK user_id  │         │ FK user_id  │                                                                   │
│       │    type    │         │    table   │                                                                   │
│       │    title   │         │ FK record_id│                                                                   │
│       │    body    │         │    action  │                                                                   │
│       │    data    │         │    old_data│                                                                   │
│       │    is_read │         │    new_data│                                                                   │
│       │    created │         │    ip     │                                                                   │
│       └─────────────┘         └─────────────┘                                                                   │
│                                                                                                                  │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│  ANALYTICS & REPORTING                                                                                         │
│  ════════════════════════════════════════════════════════════════════════════════════════════════════      │
│                                                                                                                  │
│       ┌─────────────┐         ┌─────────────┐         ┌─────────────┐         ┌─────────────┐            │
│       │sales_summary│         │product_sales│         │cafe_metrics│         │user_activities│          │
│       ├─────────────┤         ├─────────────┤         ├─────────────┤         ├─────────────┤            │
│       │ PK DATE     │         │ PK DATE     │         │ PK DATE     │         │ PK DATE     │            │
│       │ PK FK cafe_id│        │ PK FK cafe_id│        │ PK FK cafe_id│        │ PK FK user_id│           │
│       │    orders   │         │ FK product_id│        │    orders   │         │    logins   │            │
│       │    revenue  │         │    qty     │         │    revenue  │         │    orders   │            │
│       │    profit   │         │    revenue │         │    customers│         │    ai_calls │            │
│       │    avg_order│         │    profit  │         │    avg_time │         └─────────────┘            │
│       └─────────────┘         └─────────────┘         └─────────────┘                                    │
│                                                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Complete Table Definitions

### 2.1 Company & Location Structure

```sql
-- ================================================
-- COMPANIES TABLE (Tenant/Organization)
-- ================================================
CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    legal_name VARCHAR(255),
    tax_id VARCHAR(50),
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(255),
    website TEXT,
    logo_url TEXT,
    settings JSONB DEFAULT '{}',
    -- Custom settings per company
    status VARCHAR(50) DEFAULT 'active',
    -- active, inactive, trial
    trial_ends_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_companies_code ON companies(company_code);
CREATE INDEX idx_companies_status ON companies(status);

-- ================================================
-- REGIONS TABLE (Geographic)
-- ================================================
CREATE TABLE regions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    parent_id UUID REFERENCES regions(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL,
    region_type VARCHAR(50) DEFAULT 'region',
    -- region, city, province, district, ward
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    timezone VARCHAR(50) DEFAULT 'Asia/Ho_Chi_Minh',
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_regions_company ON regions(company_id);
CREATE INDEX idx_regions_parent ON regions(parent_id);
CREATE UNIQUE INDEX idx_regions_company_code ON regions(company_id, code);

-- ================================================
-- CAFES TABLE (Outlets/Stores)
-- ================================================
CREATE TABLE cafes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    region_id UUID REFERENCES regions(id) ON DELETE SET NULL,
    
    -- Basic Info
    cafe_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    
    -- Location
    address TEXT NOT NULL,
    region_path UUID[], -- [region_id, district_id, ward_id]
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    
    -- Contact
    phone VARCHAR(20),
    hotline VARCHAR(20),
    email VARCHAR(255),
    
    -- Media
    logo_url TEXT,
    cover_image_url TEXT,
    images TEXT[],
    
    -- Operating Hours
    opening_hours JSONB DEFAULT '{
        "mon": {"open": "07:00", "close": "22:00", "enabled": true},
        "tue": {"open": "07:00", "close": "22:00", "enabled": true},
        "wed": {"open": "07:00", "close": "22:00", "enabled": true},
        "thu": {"open": "07:00", "close": "22:00", "enabled": true},
        "fri": {"open": "07:00", "close": "22:00", "enabled": true},
        "sat": {"open": "07:00", "close": "23:00", "enabled": true},
        "sun": {"open": "08:00", "close": "22:00", "enabled": true}
    }',
    
    -- POS Settings
    is_active BOOLEAN DEFAULT TRUE,
    is_delivery_enabled BOOLEAN DEFAULT FALSE,
    is_pickup_enabled BOOLEAN DEFAULT TRUE,
    is_reservation_enabled BOOLEAN DEFAULT TRUE,
    max_prep_time INT DEFAULT 30, -- minutes
    
    -- KDS Settings
    kitchen_stations UUID[],
    auto_print_kds BOOLEAN DEFAULT TRUE,
    
    -- Tax & Payments
    tax_rate DECIMAL(5, 4) DEFAULT 0.1,
    currency VARCHAR(3) DEFAULT 'VND',
    accepted_payment_methods VARCHAR(50)[] DEFAULT ARRAY['cash', 'vnpay', 'stripe'],
    
    -- Settings
    settings JSONB DEFAULT '{}',
    metadata JSONB DEFAULT '{}',
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_cafes_company ON cafes(company_id);
CREATE INDEX idx_cafes_region ON cafes(region_id);
CREATE INDEX idx_cafes_slug ON cafes(slug) WHERE deleted_at IS NULL;
CREATE INDEX idx_cafes_code ON cafes(cafe_code);
CREATE INDEX idx_cafes_location ON cafes(latitude, longitude) 
    WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

COMMENT ON TABLE cafes IS 'Cafe outlets/stores - main business unit';
COMMENT ON COLUMN cafes.cafe_code IS 'Format: AIC-001, CAFE-HCM-001';

-- ================================================
-- CAFE SEATS TABLE
-- ================================================
CREATE TABLE cafe_seats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE CASCADE,
    
    seat_code VARCHAR(20) NOT NULL,
    zone VARCHAR(100),
    seat_type VARCHAR(50) DEFAULT 'standard',
    -- standard, booth, bar, outdoor, vip
    
    capacity INT DEFAULT 4,
    min_capacity INT DEFAULT 1,
    max_capacity INT DEFAULT 8,
    
    -- Position for floor plan
    pos_x INT,
    pos_y INT,
    pos_width INT,
    pos_height INT,
    rotation DECIMAL(5, 2) DEFAULT 0,
    
    -- Availability
    is_active BOOLEAN DEFAULT TRUE,
    status VARCHAR(50) DEFAULT 'available',
    -- available, reserved, occupied, unavailable, cleaning
    
    -- Pricing
    hourly_rate DECIMAL(12, 2),
    minimum_spend DECIMAL(12, 2),
    
    -- Settings
    allow_smoking BOOLEAN DEFAULT FALSE,
    has_outlet BOOLEAN DEFAULT FALSE,
    has_wifi BOOLEAN DEFAULT TRUE,
    
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(cafe_id, seat_code)
);

CREATE INDEX idx_cafe_seats_cafe ON cafe_seats(cafe_id);
CREATE INDEX idx_cafe_seats_zone ON cafe_seats(zone);
CREATE INDEX idx_cafe_seats_status ON cafe_seats(status);

-- ================================================
-- STAFFS TABLE
-- ================================================
CREATE TABLE staffs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    cafe_id UUID REFERENCES cafes(id) ON DELETE SET NULL,
    
    staff_code VARCHAR(50) UNIQUE,
    role VARCHAR(50) NOT NULL,
    -- owner, manager, supervisor, cashier, barista, server, delivery
    
    department VARCHAR(100),
    
    hire_date DATE,
    termination_date DATE,
    
    salary_type VARCHAR(50),
    -- hourly, monthly, commission
    salary_amount DECIMAL(12, 2),
    
    status VARCHAR(50) DEFAULT 'active',
    -- active, on_leave, suspended, terminated
    
    permissions JSONB DEFAULT '[]',
    -- ["order.create", "order.refund", "inventory.edit"]
    
    pin_code VARCHAR(6),
    -- For POS login
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    
    UNIQUE(user_id, company_id)
);

CREATE INDEX idx_staffs_user ON staffs(user_id);
CREATE INDEX idx_staffs_company ON staffs(company_id);
CREATE INDEX idx_staffs_cafe ON staffs(cafe_id);
CREATE INDEX idx_staffs_role ON staffs(role);
CREATE INDEX idx_staffs_status ON staffs(status);
```

### 2.2 Menu & Products

```sql
-- ================================================
-- BRANDS TABLE
-- ================================================
CREATE TABLE brands (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    logo_url TEXT,
    website TEXT,
    description TEXT,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(company_id, slug)
);

CREATE INDEX idx_brands_company ON brands(company_id);

-- ================================================
-- CATEGORIES TABLE
-- ================================================
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    cafe_id UUID REFERENCES cafes(id) ON DELETE CASCADE,
    -- NULL = global for all cafes
    parent_id UUID REFERENCES categories(id) ON DELETE CASCADE,
    
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    
    image_url TEXT,
    icon VARCHAR(50),
    color VARCHAR(7), -- hex color
    
    sort_order INT DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Menu display settings
    show_on_menu BOOLEAN DEFAULT TRUE,
    show_on_pos BOOLEAN DEFAULT TRUE,
    show_on_mobile BOOLEAN DEFAULT TRUE,
    
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(company_id, slug)
);

CREATE INDEX idx_categories_company ON categories(company_id);
CREATE INDEX idx_categories_cafe ON categories(cafe_id);
CREATE INDEX idx_categories_parent ON categories(parent_id);
CREATE INDEX idx_categories_sort ON categories(sort_order);

COMMENT ON TABLE categories IS 'Product categories - can be global or cafe-specific';

-- ================================================
-- ALLERGENS TABLE
-- ================================================
CREATE TABLE allergens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    name_vi VARCHAR(100) NOT NULL,
    icon VARCHAR(50),
    color VARCHAR(7),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_allergens_company ON allergens(company_id);

-- ================================================
-- UNITS TABLE (Measurement Units)
-- ================================================
CREATE TABLE units (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) UNIQUE NOT NULL,
    -- ml, g, kg, piece, portion
    name VARCHAR(50) NOT NULL,
    name_vi VARCHAR(50) NOT NULL,
    category VARCHAR(50),
    -- volume, weight, count, other
    conversion_factor DECIMAL(10, 4),
    -- Convert to base unit
    base_unit_id UUID REFERENCES units(id),
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Default units
INSERT INTO units (code, name, name_vi, category, conversion_factor) VALUES
('ml', 'Milliliter', 'Mililit', 'volume', 1),
('l', 'Liter', 'Lít', 'volume', 1000),
('g', 'Gram', 'Gam', 'weight', 1),
('kg', 'Kilogram', 'Kilogam', 'weight', 1000),
('piece', 'Piece', 'Cái', 'count', 1),
('portion', 'Portion', 'Phần', 'count', 1);

-- ================================================
-- PRODUCTS TABLE (Menu Items)
-- ================================================
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    cafe_id UUID REFERENCES cafes(id) ON DELETE CASCADE,
    -- NULL = available at all cafes
    category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    brand_id UUID REFERENCES brands(id) ON DELETE SET NULL,
    
    -- Basic Info
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    slug VARCHAR(255) NOT NULL,
    sku VARCHAR(100),
    barcode VARCHAR(100),
    
    -- Description
    description TEXT,
    short_description VARCHAR(500),
    
    -- Pricing (VND)
    price DECIMAL(12, 0) NOT NULL DEFAULT 0,
    compare_at_price DECIMAL(12, 0),
    cost_price DECIMAL(12, 0),
    
    -- Category Type
    product_type VARCHAR(50) DEFAULT 'standard',
    -- standard, combo, addon, gift
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    is_available BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_best_seller BOOLEAN DEFAULT FALSE,
    is_new BOOLEAN DEFAULT FALSE,
    is_taxable BOOLEAN DEFAULT TRUE,
    
    -- Display
    images TEXT[],
    video_url TEXT,
    sort_order INT DEFAULT 0,
    
    -- Recipe
    recipe_id UUID,
    yield_amount INT DEFAULT 1,
    yield_unit VARCHAR(20),
    
    -- Inventory
    track_inventory BOOLEAN DEFAULT TRUE,
    stock_quantity INT DEFAULT 0,
    low_stock_threshold INT DEFAULT 10,
    allow_out_of_stock BOOLEAN DEFAULT FALSE,
    
    -- Attributes
    preparation_time INT DEFAULT 5,
    calories INT,
    tags TEXT[],
    metadata JSONB DEFAULT '{}',
    
    -- POS Settings
    quick_add_to_order BOOLEAN DEFAULT FALSE,
    require_seat BOOLEAN DEFAULT FALSE,
    age_restriction INT DEFAULT 0,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    
    UNIQUE(company_id, slug)
);

CREATE INDEX idx_products_company ON products(company_id);
CREATE INDEX idx_products_cafe ON products(cafe_id) WHERE cafe_id IS NOT NULL;
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand_id);
CREATE INDEX idx_products_sku ON products(sku) WHERE sku IS NOT NULL;
CREATE INDEX idx_products_barcode ON products(barcode) WHERE barcode IS NOT NULL;
CREATE INDEX idx_products_slug ON products(slug) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_featured ON products(is_featured) WHERE is_featured = TRUE;
CREATE INDEX idx_products_best_seller ON products(is_best_seller) WHERE is_best_seller = TRUE;
CREATE INDEX idx_products_tags ON products USING GIN(tags);
CREATE INDEX idx_products_price ON products(price);

COMMENT ON TABLE products IS 'Menu items/products - core of the F&B system';

-- ================================================
-- PRODUCT VARIANTS TABLE
-- ================================================
CREATE TABLE product_variants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100),
    barcode VARCHAR(100),
    
    price DECIMAL(12, 0) NOT NULL,
    compare_at_price DECIMAL(12, 0),
    cost_price DECIMAL(12, 0),
    
    stock_quantity INT DEFAULT 0,
    low_stock_threshold INT DEFAULT 5,
    
    options JSONB DEFAULT '{}',
    -- {"color": "red", "size": "large"}
    
    is_active BOOLEAN DEFAULT TRUE,
    is_default BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    
    image_url TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(product_id, sku)
);

CREATE INDEX idx_product_variants_product ON product_variants(product_id);
CREATE INDEX idx_product_variants_sku ON product_variants(sku);

-- ================================================
-- PRODUCT OPTIONS TABLE (Size, Sugar, Ice, etc.)
-- ================================================
CREATE TABLE product_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    
    name VARCHAR(100) NOT NULL,
    option_type VARCHAR(50) NOT NULL,
    -- size, sugar, ice, temperature, topping, other
    
    is_required BOOLEAN DEFAULT FALSE,
    is_multi_select BOOLEAN DEFAULT FALSE,
    -- TRUE for toppings (multiple selection)
    min_selections INT DEFAULT 0,
    max_selections INT DEFAULT 1,
    
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_options_product ON product_options(product_id);

-- ================================================
-- PRODUCT OPTION VALUES TABLE
-- ================================================
CREATE TABLE product_option_values (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    option_id UUID NOT NULL REFERENCES product_options(id) ON DELETE CASCADE,
    
    value VARCHAR(100) NOT NULL,
    value_vi VARCHAR(100),
    
    price_adjustment DECIMAL(12, 0) DEFAULT 0,
    
    is_default BOOLEAN DEFAULT FALSE,
    is_available BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_option_values_option ON product_option_values(option_id);

-- ================================================
-- PRODUCT MODIFIERS TABLE (Add-ons, Toppings)
-- ================================================
CREATE TABLE product_modifiers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    
    modifier_type VARCHAR(50) DEFAULT 'addon',
    -- addon, replacement, removal
    
    price DECIMAL(12, 0) DEFAULT 0,
    
    is_active BOOLEAN DEFAULT TRUE,
    is_default BOOLEAN DEFAULT FALSE,
    is_available BOOLEAN DEFAULT TRUE,
    
    max_quantity INT DEFAULT 1,
    sort_order INT DEFAULT 0,
    
    image_url TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_modifiers_product ON product_modifiers(product_id);

-- ================================================
-- PRODUCT ALLERGENS TABLE
-- ================================================
CREATE TABLE product_allergens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    allergen_id UUID NOT NULL REFERENCES allergens(id) ON DELETE CASCADE,
    
    is_present BOOLEAN DEFAULT TRUE,
    -- TRUE = contains, FALSE = may contain traces
    severity VARCHAR(50) DEFAULT 'contains',
    -- contains, may_contain, traces
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(product_id, allergen_id)
);

CREATE INDEX idx_product_allergens_product ON product_allergens(product_id);
CREATE INDEX idx_product_allergens_allergen ON product_allergens(allergen_id);

-- ================================================
-- NUTRITIONAL INFO TABLE
-- ================================================
CREATE TABLE nutritional_info (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE UNIQUE,
    
    serving_size DECIMAL(10, 2),
    serving_unit VARCHAR(20),
    
    calories DECIMAL(10, 2) DEFAULT 0,
    calories_from_fat DECIMAL(10, 2),
    
    total_fat DECIMAL(10, 2) DEFAULT 0,
    saturated_fat DECIMAL(10, 2),
    trans_fat DECIMAL(10, 2),
    cholesterol DECIMAL(10, 2),
    
    sodium DECIMAL(10, 2),
    total_carbohydrates DECIMAL(10, 2) DEFAULT 0,
    dietary_fiber DECIMAL(10, 2),
    total_sugars DECIMAL(10, 2),
    added_sugars DECIMAL(10, 2),
    
    protein DECIMAL(10, 2) DEFAULT 0,
    
    caffeine DECIMAL(10, 2),
    -- in mg
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ================================================
-- RECIPES TABLE (Formulas/Recipes)
-- ================================================
CREATE TABLE recipes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE UNIQUE,
    
    version INT DEFAULT 1,
    status VARCHAR(50) DEFAULT 'active',
    -- draft, active, archived
    
    instructions JSONB DEFAULT '[]',
    -- [{"step": 1, "instruction": "Add espresso", "duration": null}]
    
    prep_time INT,
    -- minutes
    cook_time INT,
    
    yield_amount INT DEFAULT 1,
    yield_unit VARCHAR(20),
    
    notes TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP WITH TIME ZONE,
    approved_by UUID REFERENCES users(id)
);

CREATE INDEX idx_recipes_product ON recipes(product_id);

-- ================================================
-- RECIPE INGREDIENTS TABLE
-- ================================================
CREATE TABLE recipe_ingredients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipe_id UUID NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    ingredient_id UUID NOT NULL REFERENCES ingredients(id) ON DELETE RESTRICT,
    
    quantity DECIMAL(10, 4) NOT NULL,
    unit_id UUID REFERENCES units(id),
    
    is_optional BOOLEAN DEFAULT FALSE,
    is_flexible BOOLEAN DEFAULT FALSE,
    -- Can be substituted
    flexible_amount DECIMAL(10, 4),
    -- Variation range
    
    notes TEXT,
    sort_order INT DEFAULT 0,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(recipe_id, ingredient_id)
);

CREATE INDEX idx_recipe_ingredients_recipe ON recipe_ingredients(recipe_id);
CREATE INDEX idx_recipe_ingredients_ingredient ON recipe_ingredients(ingredient_id);

-- ================================================
-- COMBOS/SET MEALS TABLE
-- ================================================
CREATE TABLE combos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    cafe_id UUID REFERENCES cafes(id) ON DELETE CASCADE,
    
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    description TEXT,
    
    price DECIMAL(12, 0) NOT NULL,
    compare_at_price DECIMAL(12, 0),
    
    discount_amount DECIMAL(12, 0),
    discount_percent DECIMAL(5, 2),
    
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    
    images TEXT[],
    
    is_active BOOLEAN DEFAULT TRUE,
    is_available BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    
    settings JSONB DEFAULT '{}',
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_combos_company ON combos(company_id);
CREATE INDEX idx_combos_cafe ON combos(cafe_id);

-- ================================================
-- COMBO ITEMS TABLE
-- ================================================
CREATE TABLE combo_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    combo_id UUID NOT NULL REFERENCES combos(id) ON DELETE CASCADE,
    
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    variant_id UUID REFERENCES product_variants(id) ON DELETE SET NULL,
    
    quantity INT DEFAULT 1,
    
    is_required BOOLEAN DEFAULT TRUE,
    min_selections INT DEFAULT 1,
    max_selections INT DEFAULT 1,
    -- For "choose 1 of 3" type combos
    
    discount_amount DECIMAL(12, 0) DEFAULT 0,
    
    sort_order INT DEFAULT 0,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(combo_id, product_id)
);

CREATE INDEX idx_combo_items_combo ON combo_items(combo_id);
CREATE INDEX idx_combo_items_product ON combo_items(product_id);
```

### 2.3 Inventory & Supply Chain

```sql
-- ================================================
-- INGREDIENTS TABLE
-- ================================================
CREATE TABLE ingredients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    sku VARCHAR(100),
    barcode VARCHAR(100),
    
    category VARCHAR(100),
    unit_id UUID REFERENCES units(id),
    
    cost_price DECIMAL(12, 2) DEFAULT 0,
    min_stock_level INT DEFAULT 0,
    max_stock_level INT DEFAULT 1000,
    
    shelf_life_days INT,
    storage_conditions VARCHAR(100),
    
    supplier_id UUID,
    supplier_sku VARCHAR(100),
    
    is_active BOOLEAN DEFAULT TRUE,
    track_stock BOOLEAN DEFAULT TRUE,
    
    image_url TEXT,
    notes TEXT,
    
    metadata JSONB DEFAULT '{}',
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_ingredients_company ON ingredients(company_id);
CREATE INDEX idx_ingredients_sku ON ingredients(sku);
CREATE INDEX idx_ingredients_category ON ingredients(category);
CREATE INDEX idx_ingredients_supplier ON ingredients(supplier_id);

-- ================================================
-- SUPPLIERS TABLE
-- ================================================
CREATE TABLE suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    tax_id VARCHAR(50),
    
    contact_name VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    
    payment_terms VARCHAR(100),
    credit_limit DECIMAL(12, 2),
    
    rating DECIMAL(3, 2),
    is_active BOOLEAN DEFAULT TRUE,
    
    notes TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_suppliers_company ON suppliers(company_id);

-- ================================================
-- SUPPLIER ITEMS TABLE
-- ================================================
CREATE TABLE supplier_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id UUID NOT NULL REFERENCES suppliers(id) ON DELETE CASCADE,
    ingredient_id UUID NOT NULL REFERENCES ingredients(id) ON DELETE CASCADE,
    
    supplier_sku VARCHAR(100),
    name VARCHAR(255),
    
    unit_price DECIMAL(12, 2) NOT NULL,
    unit_id UUID REFERENCES units(id),
    min_order_qty INT DEFAULT 1,
    
    is_primary BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    
    lead_time_days INT DEFAULT 1,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(supplier_id, ingredient_id)
);

CREATE INDEX idx_supplier_items_supplier ON supplier_items(supplier_id);
CREATE INDEX idx_supplier_items_ingredient ON supplier_items(ingredient_id);

-- ================================================
-- INGREDIENT STOCK TABLE (Per Cafe)
-- ================================================
CREATE TABLE ingredient_stock (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE CASCADE,
    ingredient_id UUID NOT NULL REFERENCES ingredients(id) ON DELETE RESTRICT,
    
    quantity DECIMAL(10, 4) DEFAULT 0,
    unit_cost DECIMAL(12, 2) DEFAULT 0,
    
    batch_number VARCHAR(50),
    manufacturing_date DATE,
    expiry_date DATE,
    
    last_purchase_date DATE,
    last_purchase_price DECIMAL(12, 2),
    
    reorder_point INT DEFAULT 10,
    reorder_qty INT DEFAULT 50,
    
    warehouse_location VARCHAR(100),
    
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1,
    
    UNIQUE(cafe_id, ingredient_id)
);

CREATE INDEX idx_ingredient_stock_cafe ON ingredient_stock(cafe_id);
CREATE INDEX idx_ingredient_stock_ingredient ON ingredient_stock(ingredient_id);
CREATE INDEX idx_ingredient_stock_expiry ON ingredient_stock(expiry_date) 
    WHERE expiry_date IS NOT NULL;

-- ================================================
-- STOCK TRANSACTIONS TABLE
-- ================================================
CREATE TABLE stock_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE RESTRICT,
    ingredient_id UUID NOT NULL REFERENCES ingredients(id) ON DELETE RESTRICT,
    
    transaction_type VARCHAR(50) NOT NULL,
    -- purchase, sale, adjustment, transfer, waste, expired
    
    quantity DECIMAL(10, 4) NOT NULL,
    -- Positive for in, Negative for out
    
    unit_cost DECIMAL(12, 2) DEFAULT 0,
    total_cost DECIMAL(12, 2),
    
    reference_type VARCHAR(50),
    reference_id UUID,
    -- purchase_order_id, order_item_id, stock_adjustment_id
    
    batch_number VARCHAR(50),
    expiry_date DATE,
    
    notes TEXT,
    
    staff_id UUID REFERENCES staffs(id),
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_transactions_cafe ON stock_transactions(cafe_id);
CREATE INDEX idx_stock_transactions_ingredient ON stock_transactions(ingredient_id);
CREATE INDEX idx_stock_transactions_type ON stock_transactions(transaction_type);
CREATE INDEX idx_stock_transactions_created ON stock_transactions(created_at DESC);
CREATE INDEX idx_stock_transactions_reference ON stock_transactions(reference_type, reference_id);

-- ================================================
-- PURCHASE ORDERS TABLE
-- ================================================
CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE RESTRICT,
    supplier_id UUID NOT NULL REFERENCES suppliers(id) ON DELETE RESTRICT,
    
    order_number VARCHAR(50) UNIQUE NOT NULL,
    
    status VARCHAR(50) DEFAULT 'draft',
    -- draft, submitted, confirmed, shipped, received, cancelled
    
    order_date DATE NOT NULL,
    expected_date DATE,
    
    subtotal DECIMAL(12, 2) DEFAULT 0,
    tax_amount DECIMAL(12, 2) DEFAULT 0,
    total_amount DECIMAL(12, 2) DEFAULT 0,
    
    notes TEXT,
    
    received_at TIMESTAMP WITH TIME ZONE,
    received_by UUID REFERENCES staffs(id),
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_purchase_orders_cafe ON purchase_orders(cafe_id);
CREATE INDEX idx_purchase_orders_supplier ON purchase_orders(supplier_id);
CREATE INDEX idx_purchase_orders_status ON purchase_orders(status);
CREATE INDEX idx_purchase_orders_date ON purchase_orders(order_date);

-- ================================================
-- PURCHASE ORDER ITEMS TABLE
-- ================================================
CREATE TABLE purchase_order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
    ingredient_id UUID NOT NULL REFERENCES ingredients(id) ON DELETE RESTRICT,
    
    quantity DECIMAL(10, 4) NOT NULL,
    unit_id UUID REFERENCES units(id),
    unit_price DECIMAL(12, 2) NOT NULL,
    
    received_qty DECIMAL(10, 4) DEFAULT 0,
    
    tax_rate DECIMAL(5, 4) DEFAULT 0,
    tax_amount DECIMAL(12, 2) DEFAULT 0,
    line_total DECIMAL(12, 2) NOT NULL,
    
    notes TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_purchase_order_items_order ON purchase_order_items(purchase_order_id);

-- ================================================
-- STOCK ALERTS TABLE
-- ================================================
CREATE TABLE stock_alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE CASCADE,
    ingredient_id UUID NOT NULL REFERENCES ingredients(id) ON DELETE CASCADE,
    
    alert_type VARCHAR(50) NOT NULL,
    -- low_stock, out_of_stock, expiring_soon, expired
    
    current_quantity DECIMAL(10, 4),
    threshold INT,
    
    message TEXT,
    
    is_resolved BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP WITH TIME ZONE,
    resolved_by UUID REFERENCES users(id),
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_alerts_cafe ON stock_alerts(cafe_id);
CREATE INDEX idx_stock_alerts_resolved ON stock_alerts(is_resolved) WHERE is_resolved = FALSE;
```

### 2.4 Orders & Payments

```sql
-- ================================================
-- ORDERS TABLE
-- ================================================
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE RESTRICT,
    
    order_number VARCHAR(50) UNIQUE NOT NULL,
    
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    staff_id UUID REFERENCES staffs(id) ON DELETE SET NULL,
    seat_id UUID REFERENCES cafe_seats(id) ON DELETE SET NULL,
    table_session_id UUID,
    
    -- Order Type
    order_type VARCHAR(50) NOT NULL,
    -- dine_in, take_away, delivery, pos
    
    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    -- pending, confirmed, preparing, ready, served, completed, cancelled
    
    priority VARCHAR(20) DEFAULT 'normal',
    -- low, normal, high, urgent
    
    -- Customer Info
    customer_name VARCHAR(255),
    customer_phone VARCHAR(20),
    customer_email VARCHAR(255),
    customer_note TEXT,
    
    -- Pricing (VND)
    subtotal DECIMAL(12, 0) DEFAULT 0,
    discount_amount DECIMAL(12, 0) DEFAULT 0,
    promotion_id UUID REFERENCES promotions(id),
    promotion_code VARCHAR(50),
    
    tax_rate DECIMAL(5, 4) DEFAULT 0.1,
    tax_amount DECIMAL(12, 0) DEFAULT 0,
    
    service_charge DECIMAL(12, 0) DEFAULT 0,
    delivery_fee DECIMAL(12, 0) DEFAULT 0,
    
    total_amount DECIMAL(12, 0) DEFAULT 0,
    total_paid DECIMAL(12, 0) DEFAULT 0,
    total_refunded DECIMAL(12, 0) DEFAULT 0,
    
    -- Delivery Info
    delivery_address_id UUID,
    delivery_address TEXT,
    delivery_latitude DECIMAL(10, 8),
    delivery_longitude DECIMAL(11, 8),
    delivery_partner VARCHAR(50),
    delivery_tracking_id VARCHAR(100),
    
    -- Timing
    estimated_prep_time INT,
    estimated_ready_at TIMESTAMP WITH TIME ZONE,
    ready_at TIMESTAMP WITH TIME ZONE,
    served_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    
    -- Cancellation
    cancelled_at TIMESTAMP WITH TIME ZONE,
    cancellation_reason TEXT,
    cancelled_by UUID REFERENCES users(id),
    
    -- Kitchen
    kitchen_station_id UUID,
    kitchen_status VARCHAR(50) DEFAULT 'pending',
    printed_at TIMESTAMP WITH TIME ZONE,
    
    -- Payment
    payment_status VARCHAR(50) DEFAULT 'unpaid',
    -- unpaid, partial, paid, overpaid, refunded
    
    -- Source
    source VARCHAR(50) DEFAULT 'pos',
    -- pos, web, mobile, api, delivery
    
    -- Metadata
    metadata JSONB DEFAULT '{}',
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_orders_company ON orders(company_id);
CREATE INDEX idx_orders_cafe ON orders(cafe_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_staff ON orders(staff_id);
CREATE INDEX idx_orders_seat ON orders(seat_id);
CREATE INDEX idx_orders_number ON orders(order_number);
CREATE INDEX idx_orders_type ON orders(order_type);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);
CREATE INDEX idx_orders_created ON orders(created_at DESC);
CREATE INDEX idx_orders_date ON orders(DATE(created_at));
CREATE INDEX idx_orders_kitchen_status ON orders(kitchen_status);

COMMENT ON TABLE orders IS 'Customer orders - core transaction table';
COMMENT ON COLUMN orders.order_number IS 'Format: YYYYMMDD-XXXXXX';

-- ================================================
-- ORDER ITEMS TABLE
-- ================================================
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    variant_id UUID REFERENCES product_variants(id) ON DELETE SET NULL,
    combo_id UUID REFERENCES combos(id) ON DELETE SET NULL,
    recipe_id UUID REFERENCES recipes(id) ON DELETE SET NULL,
    
    -- Snapshot of product info at time of order
    product_name VARCHAR(255) NOT NULL,
    product_snapshot JSONB,
    -- Full product data snapshot
    
    quantity DECIMAL(10, 2) NOT NULL DEFAULT 1,
    unit_price DECIMAL(12, 0) NOT NULL,
    
    -- Customizations
    options JSONB DEFAULT '[]',
    -- [{"option_id": "xxx", "name": "Size", "value": "L", "price": 5000}]
    
    modifiers JSONB DEFAULT '[]',
    -- [{"id": "xxx", "name": "Extra shot", "price": 5000}]
    
    modifiers_price DECIMAL(12, 0) DEFAULT 0,
    
    -- Pricing
    discount_amount DECIMAL(12, 0) DEFAULT 0,
    line_total DECIMAL(12, 0) NOT NULL,
    
    -- Combo tracking
    is_combo_item BOOLEAN DEFAULT FALSE,
    combo_parent_id UUID REFERENCES order_items(id),
    
    -- Preparation
    item_status VARCHAR(50) DEFAULT 'pending',
    -- pending, preparing, ready, served
    
    notes TEXT,
    special_instructions TEXT,
    
    -- Kitchen
    prep_station_id UUID,
    prep_started_at TIMESTAMP WITH TIME ZONE,
    prep_completed_at TIMESTAMP WITH TIME ZONE,
    served_at TIMESTAMP WITH TIME ZONE,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);
CREATE INDEX idx_order_items_status ON order_items(item_status);

COMMENT ON TABLE order_items IS 'Individual items in an order';

-- ================================================
-- ORDER STATUS HISTORY TABLE
-- ================================================
CREATE TABLE order_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    
    from_status VARCHAR(50),
    to_status VARCHAR(50) NOT NULL,
    
    staff_id UUID REFERENCES staffs(id),
    staff_name VARCHAR(255),
    
    note TEXT,
    
    -- Kitchen specific
    kitchen_station_id UUID,
    
    -- Delivery specific
    delivery_status VARCHAR(50),
    delivery_location VARCHAR(255),
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_status_history_order ON order_status_history(order_id);
CREATE INDEX idx_order_status_history_created ON order_status_history(created_at DESC);

-- ================================================
-- PAYMENTS TABLE
-- ================================================
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    
    payment_type VARCHAR(50) NOT NULL,
    -- order, topup, refund, withdrawal
    
    payment_method VARCHAR(50) NOT NULL,
    -- cash, vnpay, stripe, momo, zalopay, bank_transfer
    
    amount DECIMAL(12, 0) NOT NULL,
    currency VARCHAR(3) DEFAULT 'VND',
    exchange_rate DECIMAL(10, 4) DEFAULT 1,
    
    status VARCHAR(50) DEFAULT 'pending',
    -- pending, processing, completed, failed, refunded, cancelled
    -- Partial refund support
    is_partial_refund BOOLEAN DEFAULT FALSE,
    original_payment_id UUID REFERENCES payments(id),
    refund_reason TEXT,
    refunded_at TIMESTAMP WITH TIME ZONE,
    refunded_by UUID REFERENCES users(id),
    
    -- Provider info
    provider VARCHAR(50),
    transaction_id VARCHAR(255),
    provider_reference VARCHAR(255),
    provider_response JSONB,
    
    -- Cash specific
    cash_received DECIMAL(12, 0),
    cash_change DECIMAL(12, 0),
    
    staff_id UUID REFERENCES staffs(id),
    
    paid_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_user ON payments(user_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_transaction ON payments(transaction_id);
CREATE INDEX idx_payments_created ON payments(created_at DESC);

-- ================================================
-- CARTS TABLE (Online/Mobile)
-- ================================================
CREATE TABLE carts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE CASCADE,
    
    session_id VARCHAR(100),
    
    status VARCHAR(50) DEFAULT 'active',
    -- active, converted, expired
    
    subtotal DECIMAL(12, 0) DEFAULT 0,
    discount_amount DECIMAL(12, 0) DEFAULT 0,
    total_amount DECIMAL(12, 0) DEFAULT 0,
    
    expires_at TIMESTAMP WITH TIME ZONE,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, company_id, cafe_id)
);

CREATE INDEX idx_carts_user ON carts(user_id);
CREATE INDEX idx_carts_cafe ON carts(cafe_id);

-- ================================================
-- CART ITEMS TABLE
-- ================================================
CREATE TABLE cart_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    variant_id UUID REFERENCES product_variants(id) ON DELETE SET NULL,
    
    quantity INT NOT NULL DEFAULT 1,
    
    options JSONB DEFAULT '[]',
    modifiers JSONB DEFAULT '[]',
    
    notes TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);
```

### 2.5 AI & Credits

```sql
-- ================================================
-- AI MODELS TABLE
-- ================================================
CREATE TABLE ai_models (
    id VARCHAR(100) PRIMARY KEY,
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    -- NULL = global model
    
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    provider VARCHAR(50) NOT NULL,
    -- openai, anthropic, google, huggingface, ollama
    
    model_type VARCHAR(50) DEFAULT 'chat',
    -- chat, completion, embedding, image, audio
    
    description TEXT,
    
    input_cost_per_1k DECIMAL(10, 6) NOT NULL,
    output_cost_per_1k DECIMAL(10, 6) NOT NULL,
    
    max_tokens INT DEFAULT 4096,
    context_window INT,
    
    capabilities JSONB DEFAULT '{}',
    -- {"vision": true, "function_calling": false}
    
    is_available BOOLEAN DEFAULT TRUE,
    is_default BOOLEAN DEFAULT FALSE,
    is_free BOOLEAN DEFAULT FALSE,
    
    settings JSONB DEFAULT '{}',
    -- Provider-specific settings
    
    sort_order INT DEFAULT 0,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Default models
INSERT INTO ai_models (id, name, provider, model_type, input_cost_per_1k, output_cost_per_1k, max_tokens, context_window, is_default, is_free) VALUES
('gpt-4o', 'GPT-4o', 'openai', 'chat', 0.005, 0.015, 128000, 128000, FALSE, FALSE),
('gpt-4-turbo', 'GPT-4 Turbo', 'openai', 'chat', 0.01, 0.03, 128000, 128000, FALSE, FALSE),
('gpt-3.5-turbo', 'GPT-3.5 Turbo', 'openai', 'chat', 0.0015, 0.002, 16385, 16385, TRUE, FALSE),
('claude-3-5-sonnet', 'Claude 3.5 Sonnet', 'anthropic', 'chat', 0.003, 0.015, 8192, 200000, TRUE, FALSE),
('claude-3-5-haiku', 'Claude 3.5 Haiku', 'anthropic', 'chat', 0.0008, 0.004, 8192, 200000, FALSE, FALSE),
('gemini-1.5-flash', 'Gemini 1.5 Flash', 'google', 'chat', 0.000075, 0.0003, 8192, 1000000, TRUE, TRUE),
('gemini-1.5-pro', 'Gemini 1.5 Pro', 'google', 'chat', 0.00125, 0.005, 8192, 2000000, FALSE, FALSE),
('mistral-7b-instruct', 'Mistral 7B', 'huggingface', 'chat', 0, 0, 4096, 8192, FALSE, TRUE);

-- ================================================
-- AI SESSIONS TABLE
-- ================================================
CREATE TABLE ai_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    session_type VARCHAR(50) DEFAULT 'chat',
    -- chat, completion, image, embedding, voice
    
    model_id VARCHAR(100) NOT NULL REFERENCES ai_models(id),
    
    title VARCHAR(255),
    description TEXT,
    
    context JSONB DEFAULT '[]',
    -- Previous conversation context for continuity
    
    status VARCHAR(50) DEFAULT 'active',
    -- active, archived, deleted
    
    message_count INT DEFAULT 0,
    total_input_tokens INT DEFAULT 0,
    total_output_tokens INT DEFAULT 0,
    total_tokens INT DEFAULT 0,
    total_cost DECIMAL(12, 4) DEFAULT 0,
    
    first_message_at TIMESTAMP WITH TIME ZONE,
    last_message_at TIMESTAMP WITH TIME ZONE,
    
    is_favorite BOOLEAN DEFAULT FALSE,
    is_shared BOOLEAN DEFAULT FALSE,
    share_token VARCHAR(100),
    
    metadata JSONB DEFAULT '{}',
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_ai_sessions_company ON ai_sessions(company_id);
CREATE INDEX idx_ai_sessions_user ON ai_sessions(user_id);
CREATE INDEX idx_ai_sessions_model ON ai_sessions(model_id);
CREATE INDEX idx_ai_sessions_status ON ai_sessions(status);
CREATE INDEX idx_ai_sessions_last_message ON ai_sessions(last_message_at DESC) 
    WHERE status = 'active';
CREATE INDEX idx_ai_sessions_created ON ai_sessions(created_at DESC);

-- ================================================
-- AI MESSAGES TABLE
-- ================================================
CREATE TABLE ai_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES ai_sessions(id) ON DELETE CASCADE,
    
    message_type VARCHAR(50) DEFAULT 'user',
    -- system, user, assistant, tool, function
    
    role VARCHAR(50),
    -- system, user, assistant (for API compatibility)
    
    content TEXT,
    content_html TEXT,
    content_json JSONB,
    
    -- For attachments
    attachments JSONB DEFAULT '[]',
    -- [{"type": "image", "url": "...", "name": "..."}]
    
    -- Model info
    model_id VARCHAR(100),
    provider VARCHAR(50),
    
    -- Tokens & Cost
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    total_tokens INT DEFAULT 0,
    cost DECIMAL(12, 6) DEFAULT 0,
    
    -- Performance
    latency_ms INT,
    first_token_ms INT,
    -- Time to first token (for streaming)
    
    -- Caching
    is_cached BOOLEAN DEFAULT FALSE,
    cache_hit BOOLEAN DEFAULT FALSE,
    -- Whether response was served from cache
    
    -- Error handling
    error_code VARCHAR(50),
    error_message TEXT,
    
    -- Citations/Sources
    citations JSONB DEFAULT '[]',
    
    -- Metadata
    metadata JSONB DEFAULT '{}',
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_messages_session ON ai_messages(session_id);
CREATE INDEX idx_ai_messages_created ON ai_messages(created_at DESC);
CREATE INDEX idx_ai_messages_role ON ai_messages(role);

-- Partitioning note: For millions of messages, partition by month
-- PARTITION BY RANGE (created_at)

-- ================================================
-- USER CREDITS TABLE
-- ================================================
CREATE TABLE user_credits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    balance BIGINT DEFAULT 0,
    -- In credits (1 credit ≈ 10 tokens)
    
    frozen BIGINT DEFAULT 0,
    -- Credits locked for ongoing requests
    
    lifetime_earned BIGINT DEFAULT 0,
    lifetime_used BIGINT DEFAULT 0,
    lifetime_refunded BIGINT DEFAULT 0,
    
    -- Monthly tracking
    month_earned BIGINT DEFAULT 0,
    month_used BIGINT DEFAULT 0,
    month_reset_at TIMESTAMP WITH TIME ZONE,
    
    -- Limits
    daily_limit BIGINT,
    monthly_limit BIGINT,
    
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1
);

CREATE INDEX idx_user_credits_user ON user_credits(user_id);

-- ================================================
-- CREDIT TRANSACTIONS TABLE
-- ================================================
CREATE TABLE credit_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    type VARCHAR(50) NOT NULL,
    -- earned, used, refunded, bonus, promo, purchase, expired, transferred, received
    
    amount BIGINT NOT NULL,
    -- Positive = credit, Negative = debit
    
    balance_before BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    
    -- Reference
    reference_type VARCHAR(50),
    reference_id UUID,
    -- purchase, subscription, ai_usage, promotion, refund, transfer
    
    description TEXT,
    
    -- Expiration for earned credits
    expires_at TIMESTAMP WITH TIME ZONE,
    expired_at TIMESTAMP WITH TIME ZONE,
    
    -- Metadata
    metadata JSONB DEFAULT '{}',
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credit_transactions_user ON credit_transactions(user_id);
CREATE INDEX idx_credit_transactions_type ON credit_transactions(type);
CREATE INDEX idx_credit_transactions_created ON credit_transactions(created_at DESC);
CREATE INDEX idx_credit_transactions_reference ON credit_transactions(reference_type, reference_id);

-- ================================================
-- CREDIT PLANS TABLE
-- ================================================
CREATE TABLE credit_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    -- NULL = global plan
    
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    
    credits BIGINT NOT NULL,
    price DECIMAL(12, 0) NOT NULL,
    -- VND
    
    credits_per_month BIGINT,
    -- For subscriptions
    
    period_days INT,
    -- NULL = lifetime
    
    features JSONB DEFAULT '[]',
    -- ["priority_support", "advanced_models"]
    
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_subscription BOOLEAN DEFAULT FALSE,
    
    sort_order INT DEFAULT 0,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ================================================
-- AI PROMPTS LIBRARY
-- ================================================
CREATE TABLE ai_prompts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    name VARCHAR(255) NOT NULL,
    description TEXT,
    
    prompt_text TEXT NOT NULL,
    prompt_variables JSONB DEFAULT '[]',
    -- [{"name": "topic", "type": "string", "required": true}]
    
    model_id VARCHAR(100),
    
    category VARCHAR(100),
    tags TEXT[],
    
    is_public BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    
    use_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_prompts_company ON ai_prompts(company_id);
CREATE INDEX idx_ai_prompts_user ON ai_prompts(user_id);
CREATE INDEX idx_ai_prompts_tags ON ai_prompts USING GIN(tags);
```

### 2.6 Customer & Loyalty

```sql
-- ================================================
-- CUSTOMERS TABLE (Enhanced User Profile)
-- ================================================
CREATE TABLE customers (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    
    -- Loyalty
    loyalty_tier_id UUID REFERENCES loyalty_tiers(id),
    loyalty_points BIGINT DEFAULT 0,
    lifetime_points BIGINT DEFAULT 0,
    
    -- Stats
    total_orders INT DEFAULT 0,
    total_spent DECIMAL(14, 0) DEFAULT 0,
    avg_order_value DECIMAL(12, 0) DEFAULT 0,
    
    visit_count INT DEFAULT 0,
    last_visit_at TIMESTAMP WITH TIME ZONE,
    first_order_at TIMESTAMP WITH TIME ZONE,
    
    -- Preferences
    favorite_products UUID[],
    preferred_payment VARCHAR(50),
    preferred_cafe_id UUID REFERENCES cafes(id),
    
    -- Important dates
    birthday DATE,
    anniversary DATE,
    
    -- Segments
    customer_segment VARCHAR(50),
    -- vip, regular, new, churned, at_risk
    
    lead_source VARCHAR(100),
    
    notes TEXT,
    internal_notes TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customers_tier ON customers(loyalty_tier_id);
CREATE INDEX idx_customers_segment ON customers(customer_segment);
CREATE INDEX idx_customers_points ON customers(loyalty_points DESC);

-- ================================================
-- LOYALTY TIERS TABLE
-- ================================================
CREATE TABLE loyalty_tiers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    
    name VARCHAR(100) NOT NULL,
    name_vi VARCHAR(100),
    
    min_points BIGINT NOT NULL,
    max_points BIGINT,
    -- NULL = no upper limit
    
    discount_percent DECIMAL(5, 2) DEFAULT 0,
    points_multiplier DECIMAL(3, 2) DEFAULT 1,
    -- Earn more points
    
    benefits JSONB DEFAULT '[]',
    -- ["free_delivery", "birthday_gift", "priority_support"]
    
    color VARCHAR(7),
    icon VARCHAR(50),
    
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_loyalty_tiers_company ON loyalty_tiers(company_id);

-- ================================================
-- LOYALTY POINTS TRANSACTIONS
-- ================================================
CREATE TABLE loyalty_points_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    type VARCHAR(50) NOT NULL,
    -- earned, redeemed, expired, adjusted, bonus, transferred
    
    points BIGINT NOT NULL,
    balance_before BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    
    reference_type VARCHAR(50),
    reference_id UUID,
    -- order_id, reward_id, promotion_id
    
    description TEXT,
    
    expires_at TIMESTAMP WITH TIME ZONE,
    expired_at TIMESTAMP WITH TIME ZONE,
    
    staff_id UUID REFERENCES staffs(id),
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_loyalty_points_user ON loyalty_points_transactions(user_id);
CREATE INDEX idx_loyalty_points_type ON loyalty_points_transactions(type);
CREATE INDEX idx_loyalty_points_created ON loyalty_points_transactions(created_at DESC);

-- ================================================
-- REVIEWS TABLE
-- ================================================
CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    cafe_id UUID REFERENCES cafes(id) ON DELETE CASCADE,
    product_id UUID REFERENCES products(id) ON DELETE SET NULL,
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    
    title VARCHAR(255),
    content TEXT,
    
    images TEXT[],
    
    -- Response
    response TEXT,
    responded_at TIMESTAMP WITH TIME ZONE,
    responded_by UUID REFERENCES staffs(id),
    
    -- Feedback
    helpful_count INT DEFAULT 0,
    report_count INT DEFAULT 0,
    
    status VARCHAR(50) DEFAULT 'pending',
    -- pending, approved, rejected, hidden
    
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    
    metadata JSONB DEFAULT '{}',
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reviews_company ON reviews(company_id);
CREATE INDEX idx_reviews_cafe ON reviews(cafe_id);
CREATE INDEX idx_reviews_product ON reviews(product_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);
CREATE INDEX idx_reviews_created ON reviews(created_at DESC);
```

### 2.7 Operations

```sql
-- ================================================
-- KITCHEN DISPLAY STATIONS
-- ================================================
CREATE TABLE kitchen_stations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE CASCADE,
    
    name VARCHAR(100) NOT NULL,
    station_type VARCHAR(50) NOT NULL,
    -- prep, grill, fryer, espresso, cold_drink, desserts
    
    categories UUID[],
    -- Category IDs this station handles
    
    settings JSONB DEFAULT '{}',
    
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_kitchen_stations_cafe ON kitchen_stations(cafe_id);

-- ================================================
-- PRINT JOBS TABLE (KDS, Receipts)
-- ================================================
CREATE TABLE print_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE RESTRICT,
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    
    printer_type VARCHAR(50) NOT NULL,
    -- kds, receipt, kitchen, bar
    
    printer_id VARCHAR(100),
    printer_name VARCHAR(100),
    
    print_type VARCHAR(50) NOT NULL,
    -- order, receipt, invoice, label
    
    content TEXT,
    content_html TEXT,
    
    copies INT DEFAULT 1,
    
    status VARCHAR(50) DEFAULT 'pending',
    -- pending, printing, completed, failed, cancelled
    
    attempts INT DEFAULT 0,
    last_error TEXT,
    
    printed_at TIMESTAMP WITH TIME ZONE,
    printed_by UUID REFERENCES staffs(id),
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_print_jobs_cafe ON print_jobs(cafe_id);
CREATE INDEX idx_print_jobs_order ON print_jobs(order_id);
CREATE INDEX idx_print_jobs_status ON print_jobs(status);

-- ================================================
-- AUDIT LOGS TABLE
-- ================================================
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID REFERENCES companies(id) ON DELETE SET NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    staff_id UUID REFERENCES staffs(id) ON DELETE SET NULL,
    
    action VARCHAR(100) NOT NULL,
    -- create, update, delete, login, logout, export, import
    
    entity_type VARCHAR(100) NOT NULL,
    -- orders, products, users, payments
    
    entity_id UUID,
    
    old_data JSONB,
    new_data JSONB,
    
    ip_address INET,
    user_agent TEXT,
    
    session_id VARCHAR(100),
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_company ON audit_logs(company_id);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_created ON audit_logs(created_at DESC);

-- Partition by month for high volume

-- ================================================
-- NOTIFICATIONS TABLE
-- ================================================
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    type VARCHAR(100) NOT NULL,
    -- order_status, payment, promotion, ai_credits, system, reminder
    
    channel VARCHAR(50) DEFAULT 'in_app',
    -- in_app, email, sms, push
    
    title VARCHAR(255) NOT NULL,
    title_vi VARCHAR(255),
    
    body TEXT,
    body_vi TEXT,
    
    data JSONB DEFAULT '{}',
    -- Deep link data: {screen: "order", params: {orderId: "xxx"}}
    
    image_url TEXT,
    action_url TEXT,
    
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    
    is_delivered BOOLEAN DEFAULT FALSE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    
    is_archived BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_unread ON notifications(user_id, is_read) 
    WHERE is_read = FALSE;
CREATE INDEX idx_notifications_created ON notifications(created_at DESC);
```

---

## 3. Key Functions

```sql
-- ================================================
-- CREDIT DEDUCTION FUNCTION (Atomic)
-- ================================================
CREATE OR REPLACE FUNCTION fn_deduct_credits(
    p_user_id UUID,
    p_amount BIGINT,
    p_reference_type VARCHAR(50),
    p_reference_id UUID,
    p_description TEXT
)
RETURNS TABLE(success BOOLEAN, new_balance BIGINT, error_message TEXT) AS $$
DECLARE
    v_balance BIGINT;
    v_new_balance BIGINT;
    v_result RECORD;
BEGIN
    -- Lock and get current balance
    SELECT balance INTO v_balance
    FROM user_credits
    WHERE user_id = p_user_id
    FOR UPDATE;
    
    -- Check balance
    IF v_balance IS NULL THEN
        RETURN QUERY SELECT FALSE, BIGINT '0', 'Credits record not found'::TEXT;
        RETURN;
    END IF;
    
    IF v_balance < p_amount THEN
        RETURN QUERY SELECT FALSE, v_balance, 
            format('Insufficient credits. Need %s, have %s', p_amount, v_balance);
        RETURN;
    END IF;
    
    -- Deduct
    v_new_balance := v_balance - p_amount;
    
    UPDATE user_credits SET
        balance = v_new_balance,
        lifetime_used = lifetime_used + p_amount,
        month_used = month_used + p_amount,
        updated_at = CURRENT_TIMESTAMP,
        version = version + 1
    WHERE user_id = p_user_id;
    
    -- Record transaction
    INSERT INTO credit_transactions (
        user_id, type, amount, balance_before, balance_after,
        reference_type, reference_id, description
    ) VALUES (
        p_user_id, 'used', -p_amount, v_balance, v_new_balance,
        p_reference_type, p_reference_id, p_description
    );
    
    RETURN QUERY SELECT TRUE, v_new_balance, NULL::TEXT;
END;
$$ LANGUAGE plpgsql;

-- ================================================
-- ORDER NUMBER GENERATOR
-- ================================================
CREATE OR REPLACE FUNCTION fn_generate_order_number(p_cafe_code VARCHAR)
RETURNS VARCHAR AS $$
DECLARE
    v_date VARCHAR := TO_CHAR(CURRENT_DATE, 'YYYYMMDD');
    v_seq INT;
BEGIN
    SELECT COALESCE(MAX(
        CAST(SUBSTRING(order_number FROM 10) AS INT)
    ), 0) + 1 INTO v_seq
    FROM orders
    WHERE DATE(created_at) = CURRENT_DATE
    AND cafe_id IN (
        SELECT id FROM cafes WHERE cafe_code = p_cafe_code
    );
    
    RETURN v_date || '-' || LPAD(v_seq::TEXT, 6, '0');
END;
$$ LANGUAGE plpgsql;

-- ================================================
-- UPDATE ORDER TOTALS TRIGGER
-- ================================================
CREATE OR REPLACE FUNCTION fn_update_order_totals()
RETURNS TRIGGER AS $$
BEGIN
    -- Recalculate item totals
    UPDATE orders SET
        subtotal = (
            SELECT COALESCE(SUM(line_total), 0)
            FROM order_items
            WHERE order_id = NEW.order_id
        ),
        total_amount = subtotal - discount_amount + tax_amount + service_charge + delivery_fee,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.order_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_order_totals
    AFTER INSERT OR UPDATE OR DELETE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION fn_update_order_totals();

-- ================================================
-- AI SESSION AGGREGATION
-- ================================================
CREATE OR REPLACE FUNCTION fn_update_ai_session_stats()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE ai_sessions SET
        message_count = message_count + 1,
        total_input_tokens = total_input_tokens + COALESCE(NEW.input_tokens, 0),
        total_output_tokens = total_output_tokens + COALESCE(NEW.output_tokens, 0),
        total_tokens = total_tokens + COALESCE(NEW.total_tokens, 0),
        total_cost = total_cost + COALESCE(NEW.cost, 0),
        last_message_at = NEW.created_at,
        first_message_at = COALESCE(first_message_at, NEW.created_at),
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.session_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_ai_session_stats
    AFTER INSERT ON ai_messages
    FOR EACH ROW
    EXECUTE FUNCTION fn_update_ai_session_stats();
```

---

## 4. Data Volume Estimates

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         EXPECTED DATA VOLUME                                        │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  TABLES WITH MILLIONS OF ROWS:                                                       │
│  ═════════════════════════════════                                                   │
│                                                                                      │
│  ┌─────────────────────────┬────────────────┬────────────────────────────────┐      │
│  │ Table                  │ Est. Rows/Mo   │ Partition Strategy              │      │
│  ├─────────────────────────┼────────────────┼────────────────────────────────┤      │
│  │ orders                 │ 1,000,000      │ BY RANGE (created_at) monthly  │      │
│  │ order_items           │ 5,000,000      │ BY RANGE (created_at) monthly  │      │
│  │ ai_messages           │ 10,000,000     │ BY RANGE (created_at) monthly  │      │
│  │ ai_usage              │ 1,000,000      │ BY RANGE (created_at) daily    │      │
│  │ stock_transactions    │ 500,000        │ BY RANGE (created_at) monthly   │      │
│  │ audit_logs           │ 2,000,000      │ BY RANGE (created_at) daily     │      │
│  │ notifications        │ 5,000,000      │ BY RANGE (created_at) monthly  │      │
│  │ reviews              │ 10,000         │ BY RANGE (created_at) yearly   │      │
│  └─────────────────────────┴────────────────┴────────────────────────────────┘      │
│                                                                                      │
│  STATIC TABLES (Thousands):                                                         │
│  ════════════════════════                                                           │
│                                                                                      │
│  ┌─────────────────────────┬────────────────┬────────────────────────────────┐      │
│  │ Table                  │ Est. Rows      │ Growth Rate                    │      │
│  ├─────────────────────────┼────────────────┼────────────────────────────────┤      │
│  │ users                  │ 5,000,000      │ +50,000/month                 │      │
│  │ products               │ 50,000         │ +1,000/month                  │      │
│  │ ingredients            │ 10,000         │ +100/month                    │      │
│  │ cafes                  │ 1,000          │ +50/month                     │      │
│  │ categories             │ 5,000          │ +100/month                    │      │
│  └─────────────────────────┴────────────────┴────────────────────────────────┘      │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Scaling Considerations

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         SCALING STRATEGY                                            │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  1. TABLE PARTITIONING (for time-series data)                                       │
│  ════════════════════════════════════════════                                       │
│                                                                                      │
│  -- Partition orders by month                                                        │
│  CREATE TABLE orders (                                                             │
│      ...                                                                            │
│  ) PARTITION BY RANGE (created_at);                                                 │
│                                                                                      │
│  CREATE TABLE orders_2024_01 PARTITION OF orders                                   │
│      FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');                               │
│                                                                                      │
│  2. READ REPLICAS                                                                  │
│  ══════════════════                                                                 │
│                                                                                      │
│  Primary DB ───▶ Read Replica 1 (reporting)                                       │
│       │         ───▶ Read Replica 2 (POS)                                           │
│       │         ───▶ Read Replica 3 (AI services)                                   │
│                                                                                      │
│  3. CACHING LAYER                                                                  │
│  ══════════════════                                                                 │
│                                                                                      │
│  Redis Cluster:                                                                     │
│  ├─ Session data                                                                    │
│  ├─ Product catalog (1hr TTL)                                                       │
│  ├─ AI message cache (5min TTL)                                                     │
│  ├─ Rate limiting                                                                   │
│  └─ Order queue                                                                    │
│                                                                                      │
│  4. ASYNC PROCESSING                                                                │
│  ═══════════════════                                                                │
│                                                                                      │
│  Kafka + Workers:                                                                   │
│  ├─ Audit log writing                                                               │
│  ├─ Notification delivery                                                           │
│  ├─ Analytics aggregation                                                           │
│  ├─ AI response caching                                                             │
│  └─ Stock alert processing                                                          │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 6. Summary

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         COMPLETE TABLE COUNT                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────┐  │
│  │ Module                          │ Tables   │ Purpose                          │  │
│  ├────────────────────────────────┼──────────┼─────────────────────────────────┤  │
│  │ Company & Location              │ 5        │ Multi-tenant, geo hierarchy     │  │
│  │ Menu & Products                │ 12       │ Full F&B product management     │  │
│  │ Inventory & Supply             │ 8        │ Stock, suppliers, recipes       │  │
│  │ Orders & Payments              │ 7        │ Transactions, cart, checkout     │  │
│  │ AI & Credits                   │ 8        │ AI chat, credits system        │  │
│  │ Customer & Loyalty             │ 5        │ CRM, loyalty programs           │  │
│  │ Operations                     │ 5        │ KDS, printing, audit            │  │
│  ├────────────────────────────────┼──────────┼─────────────────────────────────┤  │
│  │ TOTAL                          │ 50       │ Complete F&B platform           │  │
│  └────────────────────────────────┴──────────┴─────────────────────────────────┘  │
│                                                                                      │
│  ✅ RELATIONSHIPS:                                                                  │
│  ├─ 1:1  → user ↔ customer, product ↔ nutritional_info                             │
│  ├─ 1:N  → cafe → orders → order_items                                            │
│  └─ N:N  → product ↔ allergens (via product_allergens)                             │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

Bây giờ database đã đầy đủ cho F&B platform với:

| Module | Tables | Key Features |
|--------|--------|--------------|
| **Company** | 5 | Multi-tenant, geo hierarchy, staff management |
| **Products** | 12 | Full menu, variants, options, modifiers, recipes, combos |
| **Inventory** | 8 | Stock tracking, suppliers, purchase orders, alerts |
| **Orders** | 7 | Dine-in, take-away, delivery, cart, payments |
| **AI** | 8 | Chat sessions, messages, credits, prompts |
| **CRM** | 5 | Customer profiles, loyalty tiers, reviews |
| **Operations** | 5 | KDS, printing, audit logs, notifications |

**Tổng: 50 bảng** - Đủ cho enterprise F&B platform!

Bạn muốn tiếp tục với bước nào?
1. **Tạo SQL Migrations** (Flyway)
2. **Tạo TypeScript Entities** (Node.js/TypeORM)
3. **Tạo JPA Entities** (Java/Spring Boot)
4. **Design API Endpoints** (REST)
