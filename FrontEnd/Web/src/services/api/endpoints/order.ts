// ============================================
// Order API - Protected endpoints
// ============================================

import { api } from './client';
import type {
  Order,
  CreateOrderRequest,
  PaginationParams,
} from '../../types';

// List user's orders
export async function listOrders(
  params?: PaginationParams
): Promise<{ orders: Order[]; total: number }> {
  const searchParams = new URLSearchParams();
  if (params?.limit) searchParams.set('limit', String(params.limit));
  if (params?.offset) searchParams.set('offset', String(params.offset));

  const query = searchParams.toString();
  return api.get<{ orders: Order[]; total: number }>(`/v1/orders${query ? `?${query}` : ''}`, true);
}

// Get order by ID
export async function getOrder(id: string): Promise<Order> {
  return api.get<Order>(`/v1/orders/${id}`, true);
}

// Create new order (manual, not from cart)
export async function createOrder(data: CreateOrderRequest): Promise<Order> {
  return api.post<Order>('/v1/orders', data, true);
}

// Cancel order
export async function cancelOrder(id: string): Promise<Order> {
  return api.put<Order>(`/v1/orders/${id}/cancel`, undefined, true);
}
