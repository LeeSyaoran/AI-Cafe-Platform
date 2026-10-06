import { api } from '../client';
import type { ApiResponse } from '@/types';
import type { Cart, CartItem } from '@/types';

// Get current cart
export async function getCart(): Promise<ApiResponse<Cart>> {
  return api.get<ApiResponse<Cart>>('/v1/cart', true);
}

// Add item to cart
export async function addToCart(data: {
  product_id: string;
  quantity: number;
  options?: Record<string, unknown>;
  notes?: string;
}): Promise<ApiResponse<Cart>> {
  return api.post<ApiResponse<Cart>>('/v1/cart/items', data, true);
}

// Update cart item
export async function updateCartItem(
  itemId: string,
  data: {
    quantity?: number;
    options?: Record<string, unknown>;
    notes?: string;
  }
): Promise<ApiResponse<Cart>> {
  return api.put<ApiResponse<Cart>>(`/v1/cart/items/${itemId}`, data, true);
}

// Remove item from cart
export async function removeFromCart(itemId: string): Promise<ApiResponse<Cart>> {
  return api.delete<ApiResponse<Cart>>(`/v1/cart/items/${itemId}`, true);
}

// Clear cart
export async function clearCart(): Promise<ApiResponse<Cart>> {
  return api.delete<ApiResponse<Cart>>('/v1/cart', true);
}

// Checkout
export async function checkout(data?: {
  payment_method?: string;
  note?: string;
}): Promise<ApiResponse<{ order_id: string }>> {
  return api.post<ApiResponse<{ order_id: string }>>('/v1/cart/checkout', data, true);
}
