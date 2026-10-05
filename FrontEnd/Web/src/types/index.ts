// ============================================
// AI Café Platform - Shared Types
// Backend uses snake_case, FE uses camelCase
// ============================================

// API Response wrapper
export interface ApiResponse<T = unknown> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
  };
}

// User & Auth
export interface User {
  id: string;
  email: string;
  phone?: string;
  role: 'customer' | 'admin' | 'manager' | 'staff' | 'super_admin';
  status: 'active' | 'suspended' | 'pending' | 'deleted';
  created_at: string;
  updated_at: string;
}

export interface UserProfile extends User {
  full_name?: string;
  display_name?: string;
  avatar_url?: string;
  date_of_birth?: string;
  gender?: string;
  language?: string;
  timezone?: string;
}

export interface AuthTokens {
  access_token: string;
  refresh_token: string;
  expires_at: number;
}

export interface OTPRequest {
  phone: string;
}

export interface OTPVerifyRequest {
  phone: string;
  code: string;
}

// Cafe (backend format)
export interface Cafe {
  id: string;
  cafe_code: string;
  name: string;
  slug: string;
  address: string;
  phone: string;
  opening_hours?: string; // JSON: {"mon": "07:00-22:00", ...}
  description?: string;
  is_delivery_enabled: boolean;
  is_pickup_enabled: boolean;
  is_reservation_enabled: boolean;
  tax_rate: number;
}

// Category (backend format)
export interface Category {
  id: string;
  name: string;
  slug: string;
  icon?: string;
  color?: string;
  sort_order: number;
  is_featured: boolean;
}

// Product (backend format)
export interface Product {
  id: string;
  category_id?: string;
  name: string;
  name_vi?: string;
  slug: string;
  sku?: string;
  description?: string;
  short_description?: string;
  price: number;
  image_url?: string;
  images?: string[];
  calories?: number;
  preparation_time?: number;
  is_active: boolean;
  is_featured: boolean;
  is_best_seller: boolean;
  category?: Category;
}

export interface ProductListResponse {
  products: Product[];
  total: number;
  limit: number;
  offset: number;
}

// Menu (nested structure from /v1/menu)
export interface MenuCategory extends Category {
  products: Product[];
}

// Cart
export interface Cart {
  id: string;
  user_id: string;
  company_id: string;
  cafe_id: string;
  subtotal: number;
  item_count: number;
  expires_at?: string;
  created_at?: string;
  updated_at?: string;
  items?: CartItem[];
}

export interface CartItem {
  id: string;
  cart_id: string;
  product_id: string;
  variant_id?: string;
  quantity: number;
  unit_price: number;
  line_total: number;
  options_json?: string;
  modifiers_json?: string;
  notes?: string;
  product?: Product;
}

export interface AddToCartRequest {
  product_id: string;
  variant_id?: string;
  quantity: number;
  options_json?: string;
  modifiers_json?: string;
  notes?: string;
}

export interface UpdateCartItemRequest {
  quantity: number;
  notes?: string;
}

// Order
export interface Order {
  id: string;
  order_number: string;
  user_id: string;
  company_id: string;
  cafe_id: string;
  seat_id?: string;
  order_type: 'dine_in' | 'takeaway' | 'delivery';
  status: 'pending' | 'confirmed' | 'preparing' | 'ready' | 'completed' | 'cancelled';
  subtotal: number;
  discount_amount: number;
  tax_amount: number;
  total_amount: number;
  total_paid: number;
  payment_status: 'pending' | 'paid' | 'failed' | 'refunded';
  customer_note?: string;
  created_at: string;
  updated_at: string;
  items?: OrderItem[];
}

export interface OrderItem {
  id: string;
  order_id: string;
  product_id: string;
  variant_id?: string;
  product_name: string;
  quantity: number;
  unit_price: number;
  options_json?: string;
  modifiers_json?: string;
  notes?: string;
  line_total: number;
  item_status: string;
}

export interface CreateOrderRequest {
  cafe_id: string;
  seat_id?: string;
  order_type: 'dine_in' | 'takeaway' | 'delivery';
  customer_note?: string;
  payment_method?: string;
}

// Wallet
export interface Wallet {
  user_id: string;
  balance: number;
  currency: string;
}

export interface CreditBalance {
  user_id: string;
  balance: number;
  currency: 'credits';
}

// AI Chat
export interface ChatMessage {
  type: 'text' | 'image' | 'suggestion';
  content: string;
  role: 'user' | 'assistant';
  timestamp: number;
}

// Pagination
export interface PaginationParams {
  limit?: number;
  offset?: number;
}

export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  limit: number;
  offset: number;
  has_more: boolean;
}
