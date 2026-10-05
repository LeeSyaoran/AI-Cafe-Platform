// ============================================
// API Client - Base fetch wrapper với interceptors
// ============================================

import type { ApiResponse } from '../../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// Token storage (có thể thay bằng secure storage)
const TokenStorage = {
  get accessToken() {
    return localStorage.getItem('accessToken');
  },
  set accessToken(value: string | null) {
    if (value) localStorage.setItem('accessToken', value);
    else localStorage.removeItem('accessToken');
  },
  get refreshToken() {
    return localStorage.getItem('refreshToken');
  },
  set refreshToken(value: string | null) {
    if (value) localStorage.setItem('refreshToken', value);
    else localStorage.removeItem('refreshToken');
  },
};

// Custom error class
export class ApiError extends Error {
  constructor(
    public code: string,
    message: string,
    public status: number,
    public data?: unknown
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

// Refresh token state
let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

function subscribeTokenRefresh(cb: (token: string) => void) {
  refreshSubscribers.push(cb);
}

function onRefreshComplete(newToken: string) {
  refreshSubscribers.forEach((cb) => cb(newToken));
  refreshSubscribers = [];
}

// Retry logic đơn giản
async function fetchWithRetry(
  url: string,
  options: RequestInit,
  retries = 1
): Promise<Response> {
  const response = await fetch(url, options);

  // 401 → thử refresh token
  if (response.status === 401 && retries > 0 && TokenStorage.refreshToken) {
    if (!isRefreshing) {
      isRefreshing = true;
      try {
        const newToken = await refreshAccessToken();
        isRefreshing = false;
        onRefreshComplete(newToken);
      } catch {
        isRefreshing = false;
        TokenStorage.accessToken = null;
        TokenStorage.refreshToken = null;
        window.location.href = '/login';
        throw new ApiError('AUTH_EXPIRED', 'Session expired', 401);
      }
    }

    // Chờ token mới
    return new Promise((resolve, reject) => {
      subscribeTokenRefresh((token) => {
        options.headers = {
          ...options.headers,
          Authorization: `Bearer ${token}`,
        };
        resolve(fetchWithRetry(url, options, retries - 1));
      });
    });
  }

  return response;
}

async function refreshAccessToken(): Promise<string> {
  const res = await fetch(`${API_BASE_URL}/v1/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken: TokenStorage.refreshToken }),
  });

  if (!res.ok) throw new Error('Refresh failed');
  const json: ApiResponse<{ accessToken: string; refreshToken: string }> = await res.json();
  const { accessToken, refreshToken } = json.data!;
  TokenStorage.accessToken = accessToken;
  TokenStorage.refreshToken = refreshToken;
  return accessToken;
}

// Request interceptor
function createHeaders(requiresAuth = false): HeadersInit {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };

  if (requiresAuth && TokenStorage.accessToken) {
    headers['Authorization'] = `Bearer ${TokenStorage.accessToken}`;
  }

  return headers;
}

// Core fetch function
export async function apiRequest<T>(
  endpoint: string,
  options: {
    method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
    body?: unknown;
    requiresAuth?: boolean;
    baseUrl?: string;
  } = {}
): Promise<T> {
  const { method = 'GET', body, requiresAuth = false, baseUrl = API_BASE_URL } = options;

  const url = endpoint.startsWith('http') ? endpoint : `${baseUrl}${endpoint}`;

  const requestOptions: RequestInit = {
    method,
    headers: createHeaders(requiresAuth),
  };

  if (body) {
    requestOptions.body = JSON.stringify(body);
  }

  const response = await fetchWithRetry(url, requestOptions);

  // Parse response
  const json: ApiResponse<T> = await response.json();

  if (!response.ok || !json.success) {
    throw new ApiError(
      json.error?.code || 'UNKNOWN_ERROR',
      json.error?.message || 'An error occurred',
      response.status,
      json.data
    );
  }

  return json.data as T;
}

// Convenience methods
export const api = {
  get<T>(endpoint: string, requiresAuth = false) {
    return apiRequest<T>(endpoint, { method: 'GET', requiresAuth });
  },
  post<T>(endpoint: string, body?: unknown, requiresAuth = false) {
    return apiRequest<T>(endpoint, { method: 'POST', body, requiresAuth });
  },
  put<T>(endpoint: string, body?: unknown, requiresAuth = false) {
    return apiRequest<T>(endpoint, { method: 'PUT', body, requiresAuth });
  },
  patch<T>(endpoint: string, body?: unknown, requiresAuth = false) {
    return apiRequest<T>(endpoint, { method: 'PATCH', body, requiresAuth });
  },
  delete<T>(endpoint: string, requiresAuth = false) {
    return apiRequest<T>(endpoint, { method: 'DELETE', requiresAuth });
  },
};

// Auth helpers
export function setTokens(accessToken: string, refreshToken: string) {
  TokenStorage.accessToken = accessToken;
  TokenStorage.refreshToken = refreshToken;
}

export function clearTokens() {
  TokenStorage.accessToken = null;
  TokenStorage.refreshToken = null;
}

export function isAuthenticated() {
  return !!TokenStorage.accessToken;
}

// Export for hooks
export { TokenStorage };
