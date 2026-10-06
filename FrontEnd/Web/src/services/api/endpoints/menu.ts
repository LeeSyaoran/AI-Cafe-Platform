import { api } from '../client';
import type { ApiResponse } from '@/types';
import type { Category, Product, Cafe } from '@/types';

// Get all cafes
export async function getCafes(): Promise<ApiResponse<Cafe[]>> {
  return api.get<ApiResponse<Cafe[]>>('/v1/cafes');
}

// Get categories
export async function getCategories(): Promise<ApiResponse<Category[]>> {
  return api.get<ApiResponse<Category[]>>('/v1/categories');
}

// Get products
export async function getProducts(params?: {
  category_id?: string;
  search?: string;
  limit?: number;
  offset?: number;
}): Promise<ApiResponse<Product[]>> {
  const queryParams = new URLSearchParams();
  if (params?.category_id) queryParams.set('category_id', params.category_id);
  if (params?.search) queryParams.set('search', params.search);
  if (params?.limit) queryParams.set('limit', String(params.limit));
  if (params?.offset) queryParams.set('offset', String(params.offset));

  const query = queryParams.toString();
  return api.get<ApiResponse<Product[]>>(`/v1/products${query ? `?${query}` : ''}`);
}

// Get featured products
export async function getFeaturedProducts(): Promise<ApiResponse<Product[]>> {
  return api.get<ApiResponse<Product[]>>('/v1/products/featured');
}

// Get best sellers
export async function getBestSellers(): Promise<ApiResponse<Product[]>> {
  return api.get<ApiResponse<Product[]>>('/v1/products/best-sellers');
}

// Get product by ID
export async function getProduct(id: string): Promise<ApiResponse<Product>> {
  return api.get<ApiResponse<Product>>(`/v1/products/${id}`);
}
