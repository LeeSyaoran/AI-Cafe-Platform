// ============================================
// API Configuration
// ============================================

export const API_CONFIG = {
  // Backend URL - change per environment
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:3000',

  // API version
  API_PREFIX: '/v1',

  // Endpoints
  endpoints: {
    // Auth
    sendOTP: '/auth/send-otp',
    verifyOTP: '/auth/verify-otp',
    refreshToken: '/auth/refresh',
    logout: '/auth/logout',
    profile: '/me',

    // Menu (public)
    menu: '/menu',
    cafes: '/cafes',
    categories: '/categories',
    products: '/products',
    featured: '/products/featured',
    bestSellers: '/products/best-sellers',

    // Protected
    cart: '/cart',
    cartItems: '/cart/items',
    cartClear: '/cart/clear',
    cartCheckout: '/cart/checkout',

    orders: '/orders',
    wallet: '/wallet',
    credits: '/credits/balance',
  },
} as const;

// Get full URL for an endpoint
export function getApiUrl(endpoint: string): string {
  return `${API_CONFIG.BASE_URL}${API_CONFIG.API_PREFIX}${endpoint}`;
}
