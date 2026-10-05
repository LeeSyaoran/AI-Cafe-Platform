// ============================================
// Cart API - Protected endpoints
// ============================================

import { api } from './client';
import type {
  Cart,
  CartItem,
  AddToCartRequest,
  UpdateCartItemRequest,
} from '../../types';

// Get current user's cart
export async function getCart(): Promise<Cart> {
  return api.get<Cart>('/v1/cart', true);
}

// Add item to cart
export async function addToCart(data: AddToCartRequest): Promise<Cart> {
  return api.post<Cart>('/v1/cart/items', data, true);
}

// Update cart item quantity
export async function updateCartItem(
  itemId: string,
  data: UpdateCartItemRequest
): Promise<Cart> {
  return api.put<Cart>(`/v1/cart/items/${itemId}`, data, true);
}

// Remove item from cart
export async function removeFromCart(itemId: string): Promise<Cart> {
  return api.delete<Cart>(`/v1/cart/items/${itemId}`, true);
}

// Clear entire cart
export async function clearCart(): Promise<void> {
  await api.post('/v1/cart/clear', undefined, true);
}

// Checkout - convert cart to order
export async function checkoutCart(): Promise<{ orderId: string }> {
  return api.post<{ orderId: string }>('/v1/cart/checkout', undefined, true);
}
