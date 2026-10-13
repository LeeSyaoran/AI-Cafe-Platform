// User Roles
export type UserRole = 'customer' | 'manager' | 'admin' | 'staff' | 'cashier';

// Role-based routes configuration
export const ROLE_ROUTES: Record<UserRole, { home: string; dashboard: string }> = {
  customer: {
    home: '/',
    dashboard: '/orders',
  },
  staff: {
    home: '/',
    dashboard: '/staff/orders',
  },
  cashier: {
    home: '/',
    dashboard: '/cashier',
  },
  manager: {
    home: '/',
    dashboard: '/manager',
  },
  admin: {
    home: '/',
    dashboard: '/admin',
  },
};

// Check if route is allowed for user role
export function isRouteAllowed(pathname: string, role: UserRole): boolean {
  const rolePrefixes: Record<UserRole, string[]> = {
    customer: ['/', '/cart', '/orders', '/profile'],
    staff: ['/', '/cart', '/orders', '/profile', '/staff'],
    cashier: ['/', '/cart', '/orders', '/profile', '/cashier'],
    manager: ['/', '/cart', '/orders', '/profile', '/manager'],
    admin: ['/', '/cart', '/orders', '/profile', '/manager', '/admin'],
  };

  const allowed = rolePrefixes[role] || [];
  return allowed.some((prefix) => pathname.startsWith(prefix));
}

// Get redirect path after login based on role
export function getRedirectPath(role: UserRole): string {
  return ROLE_ROUTES[role]?.dashboard || '/';
}
