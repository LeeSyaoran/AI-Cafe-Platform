// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
  };
}

// Auth Types
export interface SendOTPRequest {
  phone: string;
}

export interface SendOTPResponse {
  message: string;
  expires_in: number;
  resend_available_in: number;
  _debug_otp?: string;
}

export interface VerifyOTPRequest {
  phone: string;
  code: string;
}

export interface AuthTokens {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  token_type: string;
}

export interface User {
  id: string;
  phone: string;
  email: string;
  name?: string;
  role: 'customer' | 'manager' | 'admin';
  tier: 'bronze' | 'silver' | 'gold' | 'platinum';
  avatar?: string;
}

export interface VerifyOTPResponse {
  tokens: AuthTokens;
  user: User;
}

// Menu Types
export interface Category {
  id: string;
  name: string;
  slug: string;
  icon?: string;
  color?: string;
  sort_order?: number;
  is_featured?: boolean;
}

export interface Product {
  id: string;
  name: string;
  name_vi?: string;
  slug: string;
  description?: string;
  short_description?: string;
  price: number;
  images?: string[];
  calories?: number;
  preparation_time?: number;
  category?: Category;
  is_featured?: boolean;
  is_best_seller?: boolean;
}

export interface Cafe {
  id: string;
  name: string;
  slug: string;
  address?: string;
  phone?: string;
  is_active?: boolean;
  is_delivery_enabled?: boolean;
  is_pickup_enabled?: boolean;
}

// Cart Types
export interface CartItem {
  id: string;
  product_id: string;
  product?: Product;
  quantity: number;
  unit_price: number;
  line_total: number;
  options?: Record<string, unknown>;
  notes?: string;
}

export interface Cart {
  id: string;
  user_id: string;
  cafe_id: string;
  items: CartItem[];
  item_count: number;
  subtotal: number;
  discount_amount?: number;
  tax_amount?: number;
  total: number;
  expires_at?: string;
}

// Cart Request Types
export interface AddToCartRequest {
  product_id: string;
  quantity: number;
  options?: Record<string, unknown>;
  notes?: string;
}

export interface UpdateCartItemRequest {
  quantity?: number;
  options?: Record<string, unknown>;
  notes?: string;
}

// Order Types
export interface Order {
  id: string;
  order_number: string;
  status: 'pending' | 'confirmed' | 'preparing' | 'ready' | 'delivering' | 'completed' | 'cancelled';
  order_type: 'pickup' | 'delivery' | 'dine_in';
  subtotal: number;
  discount_amount?: number;
  tax_amount?: number;
  total_amount: number;
  total_paid?: number;
  payment_status: 'pending' | 'paid' | 'failed' | 'refunded';
  payment_method?: string;
  customer_note?: string;
  created_at: string;
  updated_at: string;
  items?: OrderItem[];
}

export interface OrderItem {
  id: string;
  product_id: string;
  product_name: string;
  quantity: number;
  unit_price: number;
  line_total: number;
}

// Wallet Types
export interface WalletBalance {
  balance: number;
  lifetime_earned: number;
  lifetime_used: number;
  tier: string;
  daily_limit: number;
  daily_used: number;
  remaining_today: number;
}

export interface Transaction {
  id: string;
  type: 'credit' | 'debit';
  amount: number;
  description: string;
  balance_after: number;
  created_at: string;
}

// Wallet Types (alias for compatibility)
export interface Wallet extends WalletBalance {}

export interface CreditBalance {
  balance: number;
  lifetime_earned: number;
  lifetime_used: number;
  tier: string;
}
