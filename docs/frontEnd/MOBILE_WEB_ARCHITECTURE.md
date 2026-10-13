# Mobile & Web Architecture

## 1. Overview

Tài liệu này mô tả kiến trúc chi tiết cho ứng dụng Mobile (iOS/Android) và Web (User/Admin) của nền tảng AI Café.

## 2. Platform Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          AI Café Client Applications                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         Expo / React Native                           │   │
│  │              (iOS + Android + Optional Web)                         │   │
│  │                                                                      │   │
│  │   ┌──────────────────────┐     ┌──────────────────────┐            │   │
│  │   │   Native iOS App    │     │  Native Android App  │            │   │
│  │   │   (Xcode build)     │     │   (Gradle build)     │            │   │
│  │   └──────────────────────┘     └──────────────────────┘            │   │
│  │                                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                      │                                     │
│                                      │ (Shared code: ~70%)                │
│                                      ▼                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                           Next.js Web App                             │   │
│  │                    (User-facing + Admin Dashboard)                    │   │
│  │                                                                      │   │
│  │   ┌──────────────────────┐     ┌──────────────────────┐            │   │
│  │   │    User Web App      │     │   Admin Dashboard    │            │   │
│  │   │   / (landing)        │     │   /admin             │            │   │
│  │   │   /chat, /workspace  │     │   /dashboard         │            │   │
│  │   └──────────────────────┘     └──────────────────────┘            │   │
│  │                                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                      │                                     │
└──────────────────────────────────────┼─────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              API Gateway (Node.js)                          │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 3. Technology Stack

### 3.1 Mobile App (Expo)

```json
// apps/mobile/package.json
{
  "name": "aicafe-mobile",
  "version": "1.0.0",
  "main": "expo-router/entry",
  "scripts": {
    "start": "expo start",
    "android": "expo run:android",
    "ios": "expo run:ios",
    "web": "expo start --web",
    "build:android": "eas build -p android",
    "build:ios": "eas build -p ios",
    "lint": "eslint .",
    "type-check": "tsc --noEmit"
  },
  "dependencies": {
    "expo": "~52.0.0",
    "expo-router": "~4.0.0",
    "expo-secure-store": "~14.0.0",
    "expo-notifications": "~0.30.0",
    "expo-camera": "~16.0.0",
    "expo-linking": "~7.0.0",
    "expo-constants": "~17.0.0",
    "expo-status-bar": "~2.0.0",
    
    "react": "18.3.1",
    "react-native": "0.76.5",
    
    "@react-navigation/native": "^7.0.0",
    "@react-navigation/native-stack": "^7.0.0",
    "@react-navigation/bottom-tabs": "^7.0.0",
    
    "@tanstack/react-query": "^5.60.0",
    "zustand": "^5.0.0",
    "axios": "^1.7.0",
    
    "tamagui": "^1.115.0",
    "@tamagui/core": "^1.115.0",
    
    "date-fns": "^4.1.0",
    "zod": "^3.23.0",
    "react-hook-form": "^7.54.0",
    "@hookform/resolvers": "^3.9.0",
    
    "react-native-safe-area-context": "^5.0.0",
    "react-native-screens": "~4.4.0",
    "react-native-gesture-handler": "~2.20.0",
    "react-native-reanimated": "~3.16.0"
  },
  "devDependencies": {
    "@babel/core": "^7.25.0",
    "@types/react": "~18.3.0",
    "typescript": "~5.6.0",
    "eslint": "^8.57.0",
    "@typescript-eslint/eslint-plugin": "^8.0.0",
    "@typescript-eslint/parser": "^8.0.0",
    "prettier": "^3.3.0",
    "eas-cli": "^14.0.0"
  }
}
```

### 3.2 Web App (Next.js)

```json
// apps/web/package.json
{
  "name": "aicafe-web",
  "version": "1.0.0",
  "private": true,
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint",
    "type-check": "tsc --noEmit"
  },
  "dependencies": {
    "next": "14.2.0",
    "react": "^18.3.0",
    "react-dom": "^18.3.0",
    
    "@tanstack/react-query": "^5.60.0",
    "zustand": "^5.0.0",
    "axios": "^1.7.0",
    
    "next-auth": "^5.0.0-beta.25",
    
    "tailwindcss": "^3.4.0",
    "@radix-ui/react-dialog": "^1.1.0",
    "@radix-ui/react-dropdown-menu": "^2.1.0",
    "@radix-ui/react-slot": "^1.1.0",
    "@radix-ui/react-toast": "^1.2.0",
    "@radix-ui/react-tabs": "^1.1.0",
    "class-variance-authority": "^0.7.0",
    "clsx": "^2.1.0",
    "tailwind-merge": "^2.5.0",
    "lucide-react": "^0.460.0",
    
    "date-fns": "^4.1.0",
    "zod": "^3.23.0",
    "react-hook-form": "^7.54.0",
    "@hookform/resolvers": "^3.9.0",
    
    "recharts": "^2.13.0",
    "@tanstack/react-table": "^8.20.0",
    "@tanstack/react-virtual": "^3.10.0"
  },
  "devDependencies": {
    "@types/node": "^22.0.0",
    "@types/react": "^18.3.0",
    "@types/react-dom": "^18.3.0",
    "typescript": "~5.6.0",
    "eslint": "^8.57.0",
    "eslint-config-next": "14.2.0",
    "prettier": "^3.3.0",
    "tailwindcss": "^3.4.0",
    "postcss": "^8.4.0",
    "autoprefixer": "^10.4.0"
  }
}
```

### 3.3 Shared Packages

```json
// packages/ui/package.json
{
  "name": "@aicafe/ui",
  "version": "1.0.0",
  "main": "./src/index.ts",
  "types": "./src/index.ts",
  "scripts": {
    "lint": "eslint src/",
    "type-check": "tsc --noEmit"
  },
  "dependencies": {
    "@aicafe/shared": "workspace:*",
    "react": "^18.3.0",
    "tamagui": "^1.115.0",
    "lucide-react": "^0.460.0",
    "clsx": "^2.1.0"
  },
  "devDependencies": {
    "@types/react": "^18.3.0",
    "typescript": "~5.6.0"
  }
}

// packages/api-client/package.json
{
  "name": "@aicafe/api-client",
  "version": "1.0.0",
  "main": "./src/index.ts",
  "types": "./src/index.ts",
  "scripts": {
    "lint": "eslint src/",
    "type-check": "tsc --noEmit",
    "generate": "openapi-typescript"
  },
  "dependencies": {
    "axios": "^1.7.0",
    "@tanstack/react-query": "^5.60.0",
    "zustand": "^5.0.0",
    "@aicafe/shared": "workspace:*",
    "zod": "^3.23.0"
  },
  "devDependencies": {
    "typescript": "~5.6.0"
  }
}

// packages/shared/package.json
{
  "name": "@aicafe/shared",
  "version": "1.0.0",
  "main": "./src/index.ts",
  "types": "./src/index.ts",
  "scripts": {
    "lint": "eslint src/",
    "type-check": "tsc --noEmit"
  },
  "dependencies": {
    "zod": "^3.23.0"
  },
  "devDependencies": {
    "typescript": "~5.6.0"
  }
}
```

## 4. Project Structure

### 4.1 Mobile App Structure

```
apps/mobile/
├── app/
│   ├── _layout.tsx              # Root layout with providers
│   ├── +html.tsx               # Web fallback HTML
│   │
│   ├── (tabs)/                 # Tab navigation group
│   │   ├── _layout.tsx         # Tab layout
│   │   ├── index.tsx           # Home tab
│   │   ├── chat.tsx            # Chat tab
│   │   ├── workspace.tsx        # Workspace tab
│   │   └── profile.tsx         # Profile tab
│   │
│   ├── (auth)/                 # Auth screens group
│   │   ├── _layout.tsx         # Auth layout (no tabs)
│   │   ├── login.tsx           # Phone input
│   │   ├── otp.tsx             # OTP verification
│   │   └── register.tsx        # Registration
│   │
│   ├── chat/                   # Chat detail screens
│   │   ├── [id].tsx            # Chat session
│   │   └── new.tsx             # New chat
│   │
│   ├── workspace/              # Workspace screens
│   │   ├── [id].tsx            # Workspace session
│   │   └── new.tsx             # New workspace
│   │
│   ├── orders/                 # Order screens
│   │   ├── index.tsx           # Order history
│   │   └── [id].tsx            # Order detail
│   │
│   ├── pricing.tsx             # Pricing page
│   ├── membership.tsx          # Membership info
│   ├── settings.tsx            # Settings
│   └── notifications.tsx       # Notifications
│
├── components/
│   ├── ui/                     # Base UI components
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Card.tsx
│   │   ├── Modal.tsx
│   │   ├── Toast.tsx
│   │   ├── Badge.tsx
│   │   ├── Avatar.tsx
│   │   ├── Skeleton.tsx
│   │   ├── Spinner.tsx
│   │   └── index.ts
│   │
│   ├── chat/                   # Chat-specific components
│   │   ├── ChatBubble.tsx
│   │   ├── ChatInput.tsx
│   │   ├── ChatList.tsx
│   │   ├── MessageStreaming.tsx
│   │   └── ModelSelector.tsx
│   │
│   ├── workspace/              # Workspace components
│   │   ├── CodeEditor.tsx
│   │   ├── FileTree.tsx
│   │   ├── Terminal.tsx
│   │   └── SessionTimer.tsx
│   │
│   ├── layout/                 # Layout components
│   │   ├── Header.tsx
│   │   ├── TabBar.tsx
│   │   ├── BottomSheet.tsx
│   │   └── SafeArea.tsx
│   │
│   └── home/                   # Home screen components
│       ├── BalanceCard.tsx
│       ├── QuickActions.tsx
│       └── AItoolsGrid.tsx
│
├── hooks/
│   ├── useAuth.ts              # Authentication hook
│   ├── useCredits.ts           # Credit balance hook
│   ├── useAIChat.ts            # Chat API hook
│   ├── useAIImage.ts           # Image API hook
│   ├── useWorkspace.ts         # Workspace hook
│   ├── useNotifications.ts     # Notifications hook
│   └── useOffline.ts           # Offline detection
│
├── services/
│   ├── api.ts                  # Axios instance
│   ├── auth.service.ts         # Auth API calls
│   ├── user.service.ts         # User API calls
│   ├── ai.service.ts           # AI API calls
│   ├── order.service.ts        # Order API calls
│   └── websocket.service.ts    # WebSocket service
│
├── stores/
│   ├── authStore.ts            # Auth state
│   ├── creditStore.ts          # Credit state
│   ├── chatStore.ts            # Chat state
│   ├── workspaceStore.ts       # Workspace state
│   └── settingsStore.ts        # Settings state
│
├── constants/
│   ├── api.ts                  # API endpoints
│   ├── config.ts               # App config
│   ├── tiers.ts                # Membership tiers
│   └── models.ts               # AI models
│
├── utils/
│   ├── format.ts               # Formatters
│   ├── validators.ts           # Zod schemas
│   ├── storage.ts              # Storage helpers
│   └── analytics.ts            # Analytics helpers
│
├── tamagui/
│   ├── index.tsx               # Tamagui config
│   └── themes.ts               # Theme definitions
│
├── eas.json                    # EAS Build configuration
├── app.json                    # Expo configuration
├── babel.config.js             # Babel config
├── tsconfig.json               # TypeScript config
└── package.json                 # Dependencies
```

### 4.2 Web App Structure

```
apps/web/
├── app/
│   ├── (auth)/
│   │   ├── login/
│   │   │   └── page.tsx
│   │   └── register/
│   │       └── page.tsx
│   │   └── layout.tsx
│   │
│   ├── (main)/
│   │   ├── layout.tsx          # Main layout with sidebar
│   │   ├── page.tsx           # Home/Dashboard
│   │   ├── chat/
│   │   │   ├── page.tsx       # Chat list
│   │   │   └── [id]/
│   │   │       └── page.tsx   # Chat detail
│   │   ├── workspace/
│   │   │   ├── page.tsx       # Workspace list
│   │   │   └── [id]/
│   │   │       └── page.tsx   # Workspace editor
│   │   ├── pricing/
│   │   │   └── page.tsx
│   │   ├── orders/
│   │   │   ├── page.tsx       # Order history
│   │   │   └── [id]/
│   │   │       └── page.tsx   # Order detail
│   │   └── settings/
│   │       └── page.tsx
│   │
│   ├── api/
│   │   ├── auth/
│   │   │   └── [...nextauth]/
│   │   │       └── route.ts   # NextAuth API
│   │   └── webhooks/
│   │       └── vnpay/
│   │           └── route.ts   # Payment webhook
│   │
│   ├── layout.tsx              # Root layout
│   ├── globals.css             # Global styles
│   └── not-found.tsx           # 404 page
│
├── components/
│   ├── ui/                     # shadcn/ui components
│   │   ├── button.tsx
│   │   ├── input.tsx
│   │   ├── card.tsx
│   │   ├── dialog.tsx
│   │   ├── toast.tsx
│   │   ├── badge.tsx
│   │   ├── avatar.tsx
│   │   ├── skeleton.tsx
│   │   └── index.ts
│   │
│   ├── chat/                   # Chat components
│   │   ├── ChatPanel.tsx
│   │   ├── MessageList.tsx
│   │   ├── MessageInput.tsx
│   │   ├── ModelSelector.tsx
│   │   └── StreamingIndicator.tsx
│   │
│   ├── workspace/               # Workspace components
│   │   ├── WorkspaceEditor.tsx
│   │   ├── FileExplorer.tsx
│   │   ├── CodeEditor.tsx
│   │   └── SessionInfo.tsx
│   │
│   ├── layout/                 # Layout components
│   │   ├── Sidebar.tsx
│   │   ├── Header.tsx
│   │   ├── MobileNav.tsx
│   │   └── Footer.tsx
│   │
│   └── home/                   # Home components
│       ├── Hero.tsx
│       ├── Features.tsx
│       ├── PricingTable.tsx
│       └── CTASection.tsx
│
├── lib/
│   ├── utils.ts                # Utility functions
│   ├── auth.ts                 # NextAuth config
│   └── api.ts                  # API client
│
├── hooks/
│   ├── useAuth.ts
│   ├── useCredits.ts
│   ├── useAIChat.ts
│   └── useChatSessions.ts
│
├── stores/
│   └── ...                     # Similar to mobile
│
├── services/
│   └── ...                     # Similar to mobile
│
├── next.config.js
├── tailwind.config.ts
├── tsconfig.json
└── package.json
```

### 4.3 Admin App Structure

```
apps/admin/
├── app/
│   ├── (auth)/
│   │   ├── login/
│   │   │   └── page.tsx
│   │   └── layout.tsx
│   │
│   ├── (dashboard)/
│   │   ├── layout.tsx          # Admin layout
│   │   ├── page.tsx           # Dashboard home
│   │   │
│   │   ├── users/
│   │   │   ├── page.tsx       # User list
│   │   │   ├── [id]/
│   │   │   │   └── page.tsx   # User detail
│   │   │   └── new/
│   │   │       └── page.tsx   # Create user
│   │   │
│   │   ├── orders/
│   │   │   ├── page.tsx       # Order list
│   │   │   └── [id]/
│   │   │       └── page.tsx   # Order detail
│   │   │
│   │   ├── transactions/
│   │   │   └── page.tsx       # Credit transactions
│   │   │
│   │   ├── analytics/
│   │   │   ├── page.tsx       # Analytics overview
│   │   │   ├── revenue/
│   │   │   │   └── page.tsx   # Revenue analytics
│   │   │   └── usage/
│   │   │       └── page.tsx   # Usage analytics
│   │   │
│   │   ├── memberships/
│   │   │   └── page.tsx       # Membership management
│   │   │
│   │   ├── loyalty/
│   │   │   └── page.tsx       # Loyalty program
│   │   │
│   │   ├── settings/
│   │   │   ├── page.tsx       # General settings
│   │   │   ├── pricing/
│   │   │   │   └── page.tsx   # Pricing settings
│   │   │   ├── tiers/
│   │   │   │   └── page.tsx   # Tier settings
│   │   │   ├── api-keys/
│   │   │   │   └── page.tsx   # API key management
│   │   │   └── webhook/
│   │   │       └── page.tsx   # Webhook config
│   │   │
│   │   └── ai-config/
│   │       ├── page.tsx       # AI provider config
│   │       └── models/
│   │           └── page.tsx   # Model settings
│   │
│   └── api/
│       └── ...                 # Admin API routes
│
├── components/
│   ├── ui/                     # Shared UI
│   │   └── ...                 # Same as web app
│   │
│   ├── dashboard/
│   │   ├── StatCard.tsx
│   │   ├── RecentActivity.tsx
│   │   ├── RevenueChart.tsx
│   │   └── QuickActions.tsx
│   │
│   ├── data-table/
│   │   ├── DataTable.tsx
│   │   ├── Pagination.tsx
│   │   ├── ColumnHeader.tsx
│   │   └── DataTableToolbar.tsx
│   │
│   ├── charts/
│   │   ├── RevenueLineChart.tsx
│   │   ├── UsageBarChart.tsx
│   │   ├── UserPieChart.tsx
│   │   └── ConversionFunnel.tsx
│   │
│   └── forms/
│       ├── UserForm.tsx
│       ├── OrderStatusForm.tsx
│       └── PricingForm.tsx
│
├── lib/
│   ├── utils.ts
│   ├── auth.ts                 # Admin auth config
│   └── api.ts                 # Admin API client
│
├── hooks/
│   ├── useUsers.ts
│   ├── useOrders.ts
│   ├── useAnalytics.ts
│   └── useAdminSettings.ts
│
├── next.config.js
├── tailwind.config.ts
├── tsconfig.json
└── package.json
```

## 5. Shared Packages

### 5.1 @aicafe/shared

```typescript
// packages/shared/src/index.ts

// Tier limits configuration
export const TIER_LIMITS = {
  basic: {
    creditsPerMonth: 2000,
    workspaceMinutes: 60,
    rpm: 10,
    tpm: 10000,
    dailyLimit: 2000,
  },
  developer: {
    creditsPerMonth: 5000,
    workspaceMinutes: 120,
    rpm: 30,
    tpm: 50000,
    dailyLimit: 5000,
  },
  pro: {
    creditsPerMonth: 10000,
    workspaceMinutes: 240,
    rpm: 60,
    tpm: 150000,
    dailyLimit: 10000,
  },
  builder: {
    creditsPerMonth: 18000,
    workspaceMinutes: 480,
    rpm: 120,
    tpm: 500000,
    dailyLimit: 18000,
  },
} as const;

export type Tier = keyof typeof TIER_LIMITS;

// Credit costs per service
export const CREDIT_COSTS = {
  chat: {
    'gpt-4o': { input: 2.5, output: 10 },
    'gpt-4o-mini': { input: 0.15, output: 0.6 },
    'claude-3-5-sonnet': { input: 3, output: 15 },
    'claude-3-5-haiku': { input: 0.8, output: 4 },
    'gemini-1-5-pro': { input: 1.25, output: 5 },
  },
  image: {
    'dalle-3': { perImage: 40 },
    'dalle-3-hd': { perImage: 80 },
    'stable-diffusion-xl': { perImage: 5 },
  },
  video: {
    'sora': { perSecond: 1000 },
    'stable-video': { perSecond: 100 },
  },
} as const;

// Validation schemas
export * from './schemas';
```

### 5.2 @aicafe/api-client

```typescript
// packages/api-client/src/index.ts
import axios, { AxiosInstance, AxiosError } from 'axios';
import { QueryClient } from '@tanstack/react-query';
import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import AsyncStorage from '@react-native-async-storage/async-storage';

// Types
export interface User {
  id: string;
  phone: string;
  name?: string;
  avatar?: string;
  tier: 'basic' | 'developer' | 'pro' | 'builder';
}

export interface Wallet {
  credits: number;
  workspaceMinutes: number;
}

export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  model?: string;
  createdAt: string;
}

// API Client
export const createApiClient = (baseURL: string, getToken: () => string | null) => {
  const client = axios.create({ baseURL });
  
  client.interceptors.request.use((config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });
  
  client.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
      if (error.response?.status === 401) {
        // Handle token refresh or logout
        useAuthStore.getState().logout();
      }
      return Promise.reject(error);
    }
  );
  
  return client;
};

// Query Client
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes
      retry: 1,
    },
  },
});

// Auth Store
interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  setAuth: (user: User, token: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      setAuth: (user, token) => set({ user, token, isAuthenticated: true }),
      logout: () => set({ user: null, token: null, isAuthenticated: false }),
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => AsyncStorage),
    }
  )
);

// Credit Store
interface CreditState {
  credits: number;
  workspaceMinutes: number;
  setCredits: (credits: number) => void;
  deductCredits: (amount: number) => void;
}

export const useCreditStore = create<CreditState>((set) => ({
  credits: 0,
  workspaceMinutes: 0,
  setCredits: (credits) => set({ credits }),
  deductCredits: (amount) => set((state) => ({ credits: state.credits - amount })),
}));

// API Endpoints
export const api = {
  auth: {
    sendOtp: (phone: string) => apiClient.post('/auth/send-otp', { phone }),
    verifyOtp: (phone: string, code: string) => 
      apiClient.post('/auth/verify-otp', { phone, code }),
    refresh: () => apiClient.post('/auth/refresh'),
    logout: () => apiClient.post('/auth/logout'),
    me: () => apiClient.get<User>('/auth/me'),
  },
  
  users: {
    me: () => apiClient.get<User>('/users/me'),
    update: (data: Partial<User>) => apiClient.patch('/users/me', data),
    wallet: () => apiClient.get<Wallet>('/users/me/wallet'),
    usage: () => apiClient.get('/users/me/usage'),
  },
  
  ai: {
    chat: (data: { message: string; model?: string; stream?: boolean }) =>
      apiClient.post('/ai/chat', data),
    chatStream: (data: { message: string; model?: string }) =>
      apiClient.post('/ai/chat/stream', data, { responseType: 'stream' }),
    image: (data: { prompt: string; model?: string; size?: string }) =>
      apiClient.post('/ai/image', data),
    models: () => apiClient.get('/ai/models'),
  },
  
  orders: {
    list: (params?: { page?: number; limit?: number }) =>
      apiClient.get('/orders', { params }),
    create: (data: { packageId: string; paymentMethod: string }) =>
      apiClient.post('/orders', data),
    get: (id: string) => apiClient.get(`/orders/${id}`),
    confirm: (id: string, data: { paymentData: any }) =>
      apiClient.post(`/orders/${id}/confirm`, data),
  },
};
```

### 5.3 @aicafe/ui

```typescript
// packages/ui/src/index.ts
import React from 'react';
import { TamaguiProvider, createTamagui } from '@tamagui/core';
import { config as defaultConfig } from '@tamagui/config';

// Components
export { Button } from './components/Button';
export { Input } from './components/Input';
export { Card } from './components/Card';
export { Modal } from './components/Modal';
export { Toast } from './components/Toast';
export { Badge } from './components/Badge';
export { Avatar } from './components/Avatar';
export { Skeleton } from './components/Skeleton';
export { Spinner } from './components/Spinner';

// Custom Tamagui config
const config = createTamagui({
  ...defaultConfig,
  themes: {
    light: {
      ...defaultConfig.themes?.light,
      primary: '#6366F1',
      secondary: '#8B5CF6',
      accent: '#EC4899',
    },
    dark: {
      ...defaultConfig.themes?.dark,
      primary: '#818CF8',
      secondary: '#A78BFA',
      accent: '#F472B6',
    },
  },
});

// Provider component
export const UIProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <TamaguiProvider config={config} defaultTheme="light">
    {children}
  </TamaguiProvider>
);

declare module '@tamagui/core' {
  interface TamaguiConfig extends Conf {}
}
```

## 6. Platform-Specific Considerations

### 6.1 iOS Specific

| Feature | Implementation |
|---------|---------------|
| **Safe Area** | SafeAreaView from react-native-safe-area-context |
| **Haptics** | expo-haptics for feedback |
| **Notifications** | APNs via expo-notifications |
| **Biometrics** | expo-local-authentication |
| **In-App Browser** | expo-web-browser for OAuth |

### 6.2 Android Specific

| Feature | Implementation |
|---------|---------------|
| **Status Bar** | expo-status-bar for customization |
| **Notifications** | FCM via expo-notifications |
| **Biometrics** | expo-local-authentication |
| **Share** | expo-sharing |
| **Deep Links** | expo-linking with Android manifest |

### 6.3 Web Specific

| Feature | Implementation |
|---------|---------------|
| **SSR** | Next.js App Router |
| **SEO** | Metadata API, sitemap |
| **PWA** | next-pwa for offline support |
| **Responsive** | TailwindCSS breakpoints |
| **Auth** | NextAuth.js with HTTP-only cookies |

## 7. Build & Deployment

### 7.1 Expo EAS Build

```json
// apps/mobile/eas.json
{
  "cli": {
    "version": ">= 14.0.0"
  },
  "build": {
    "development": {
      "developmentClient": true,
      "distribution": "internal",
      "ios": {
        "simulator": true
      }
    },
    "preview": {
      "distribution": "internal",
      "android": {
        "buildType": "apk"
      },
      "ios": {
        "simulator": false
      }
    },
    "production": {
      "android": {
        "buildType": "app-bundle"
      },
      "ios": {
        "simulator": false
      }
    }
  },
  "submit": {
    "production": {
      "android": {
        "serviceAccountKeyPath": "./path/to/service-account.json",
        "track": "production"
      },
      "ios": {
        "appleId": "your-apple-id",
        "ascAppId": "your-app-store-connect-app-id"
      }
    }
  }
}
```

### 7.2 GitHub Actions CI/CD

```yaml
# .github/workflows/mobile.yml
name: Mobile CI/CD

on:
  push:
    branches: [main]
    paths:
      - 'apps/mobile/**'
      - 'packages/**'

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup Node
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: apps/mobile/package-lock.json
      
      - name: Install dependencies
        run: npm ci
        working-directory: apps/mobile
      
      - name: Lint
        run: npm run lint
      
      - name: Type check
        run: npm run type-check

  build-android:
    runs-on: ubuntu-latest
    needs: lint
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup Node
        uses: actions/setup-node@v4
        with:
          node-version: '20'
      
      - name: Setup EAS
        run: npm install -g eas-cli
      
      - name: Build Android (internal)
        run: eas build -p android --profile preview --non-interactive
        working-directory: apps/mobile
        env:
          EAS_PROJECT_ID: ${{ secrets.EAS_PROJECT_ID }}

  build-ios:
    runs-on: macos-latest
    needs: lint
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup Node
        uses: actions/setup-node@v4
        with:
          node-version: '20'
      
      - name: Setup EAS
        run: npm install -g eas-cli
      
      - name: Build iOS
        run: eas build -p ios --profile preview --non-interactive
        working-directory: apps/mobile
        env:
          EAS_PROJECT_ID: ${{ secrets.EAS_PROJECT_ID }}
          APPLE_ID: ${{ secrets.APPLE_ID }}
          APPLE_APP_SPECIFIC_PASSWORD: ${{ secrets.APPLE_APP_SPECIFIC_PASSWORD }}
```

```yaml
# .github/workflows/web.yml
name: Web CI/CD

on:
  push:
    branches: [main]
    paths:
      - 'apps/web/**'
      - 'apps/admin/**'
      - 'packages/**'

jobs:
  lint-and-build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup Node
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Lint
        run: npm run lint
        working-directory: apps/web
      
      - name: Type check
        run: npm run type-check
        working-directory: apps/web
      
      - name: Build
        run: npm run build
        working-directory: apps/web
      
      - name: Deploy to Vercel
        run: npx vercel --prod --token=${{ secrets.VERCEL_TOKEN }}
        working-directory: apps/web
```

## 8. Development Workflow

### 8.1 Local Development

```bash
# Root directory
npm install

# Run all apps
npm run dev        # Run all apps concurrently
npm run dev:web    # Run only web
npm run dev:mobile # Run only mobile

# Run specific app
npm run dev --workspace=apps/web
npm run dev --workspace=apps/mobile
npm run dev --workspace=apps/admin

# Lint all
npm run lint

# Type check all
npm run type-check
```

### 8.2 Mobile Development

```bash
# Start Expo
cd apps/mobile
npm start

# Run on specific platform
npm run ios        # iOS Simulator
npm run android    # Android Emulator
npm run web        # Web browser

# Build locally
eas build --local --platform android
eas build --local --platform ios
```

### 8.3 Code Standards

```yaml
# .eslintrc.js (shared)
module.exports = {
  extends: [
    '@aicafe/eslint-config'
  ],
  rules: {
    // Project-specific rules
  }
};

# prettier.config.js
module.exports = {
  semi: false,
  singleQuote: true,
  trailingComma: 'es5',
  printWidth: 100,
};
```

## 9. Testing Strategy

### 9.1 Mobile Testing

| Type | Tool | Coverage Target |
|------|------|----------------|
| Unit | Jest | 80% |
| Component | React Native Testing Library | 70% |
| E2E | Detox | Critical flows |
| Manual | Expo Snack | Feature validation |

### 9.2 Web Testing

| Type | Tool | Coverage Target |
|------|------|----------------|
| Unit | Vitest | 80% |
| Component | Testing Library | 70% |
| E2E | Playwright | Critical flows |
| Visual | Chromatic | UI regression |

## 10. Performance Targets

| Platform | Metric | Target |
|----------|--------|--------|
| Mobile (iOS) | Cold start | < 3s |
| Mobile (Android) | Cold start | < 5s |
| Web (FCP) | First Contentful Paint | < 1.5s |
| Web (LCP) | Largest Contentful Paint | < 2.5s |
| Web (TTI) | Time to Interactive | < 3.5s |
| API | Response time (p95) | < 500ms |
| AI Stream | Time to first byte | < 1s |
