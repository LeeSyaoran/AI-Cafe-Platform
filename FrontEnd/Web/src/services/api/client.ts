import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '@/stores/authStore';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3000';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - add auth token
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = useAuthStore.getState().accessToken;
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - handle errors
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Token expired - try to refresh
      const refreshToken = useAuthStore.getState().refreshToken;
      if (refreshToken) {
        try {
          const response = await axios.post(`${API_BASE_URL}/v1/auth/refresh`, {
            refresh_token: refreshToken,
          });
          const { access_token } = response.data.data;
          useAuthStore.getState().setAuth(
            useAuthStore.getState().user!,
            access_token,
            refreshToken
          );
          // Retry original request
          if (error.config && error.config.headers) {
            error.config.headers.Authorization = `Bearer ${access_token}`;
            return apiClient(error.config);
          }
        } catch {
          // Refresh failed - logout
          useAuthStore.getState().logout();
        }
      }
    }
    return Promise.reject(error);
  }
);

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
  };
}

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as ApiResponse<unknown>;
    return data?.error?.message || error.message || 'An error occurred';
  }
  if (error instanceof Error) {
    return error.message;
  }
  return 'An unexpected error occurred';
}

// Convenience methods
export const api = {
  get<T>(endpoint: string, requiresAuth = false): Promise<T> {
    return apiClient.get<T>(endpoint).then(res => res.data);
  },
  post<T>(endpoint: string, data?: unknown, requiresAuth = false): Promise<T> {
    return apiClient.post<T>(endpoint, data).then(res => res.data);
  },
  put<T>(endpoint: string, data?: unknown, requiresAuth = false): Promise<T> {
    return apiClient.put<T>(endpoint, data).then(res => res.data);
  },
  delete<T>(endpoint: string, requiresAuth = false): Promise<T> {
    return apiClient.delete<T>(endpoint).then(res => res.data);
  },
};
