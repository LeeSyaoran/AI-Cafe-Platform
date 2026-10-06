import { api } from '../client';
import type { ApiResponse } from '@/types';
import type { Order } from '@/types';

// Get orders
export async function getOrders(params?: {
  status?: string;
  limit?: number;
  offset?: number;
}): Promise<ApiResponse<Order[]>> {
  const queryParams = new URLSearchParams();
  if (params?.status) queryParams.set('status', params.status);
  if (params?.limit) queryParams.set('limit', String(params.limit));
  if (params?.offset) queryParams.set('offset', String(params.offset));

  const query = queryParams.toString();
  return api.get<ApiResponse<Order[]>>(`/v1/orders${query ? `?${query}` : ''}`, true);
}

// Get order by ID
export async function getOrder(id: string): Promise<ApiResponse<Order>> {
  return api.get<ApiResponse<Order>>(`/v1/orders/${id}`, true);
}

// Create order
export async function createOrder(data: {
  order_type: 'pickup' | 'delivery' | 'dine_in';
  payment_method?: string;
  note?: string;
}): Promise<ApiResponse<Order>> {
  return api.post<ApiResponse<Order>>('/v1/orders', data, true);
}

// Cancel order
export async function cancelOrder(id: string): Promise<ApiResponse<Order>> {
  return api.post<ApiResponse<Order>>(`/v1/orders/${id}/cancel`, undefined, true);
}
