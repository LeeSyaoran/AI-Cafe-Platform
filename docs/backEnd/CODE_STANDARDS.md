# Code Standards & Conventions

## 1. Technology Stack

### Backend
| Layer | Technology | Version |
|-------|------------|---------|
| Runtime | Node.js | 20 LTS |
| Framework | Express.js | 5.x |
| Language | TypeScript | 5.x |
| ORM | Prisma | 5.x |
| Validation | Zod | 3.x |
| Testing | Jest + Supertest | Latest |

### AI Gateway
| Layer | Technology | Version |
|-------|------------|---------|
| Runtime | Python | 3.12 |
| Framework | FastAPI | 0.109+ |
| Async | asyncio + httpx | Latest |
| AI SDK | OpenAI, Anthropic | Latest |

### Mobile
| Layer | Technology | Version |
|-------|------------|---------|
| Framework | Expo SDK | 52+ |
| Language | TypeScript | 5.x |
| UI | Tamagui | 1.115+ |
| Navigation | expo-router | 4.x |
| State | Zustand | 5.x |
| API | Axios + TanStack Query | Latest |

### Web
| Layer | Technology | Version |
|-------|------------|---------|
| Framework | Next.js | 14+ (App Router) |
| Language | TypeScript | 5.x |
| UI | TailwindCSS + shadcn/ui | Latest |
| State | Zustand | 5.x |
| Auth | NextAuth.js | 5.x |

---

## 2. TypeScript Guidelines

### 2.1 File Naming

```
// Components: PascalCase
UserProfile.tsx
ChatBubble.tsx
SettingsModal.tsx

// Hooks: camelCase with 'use' prefix
useAuth.ts
useCredits.ts
useChatStream.ts

// Services: camelCase
authService.ts
paymentService.ts
aiService.ts

// Utils: camelCase
formatCurrency.ts
validatePhone.ts

// Types/Interfaces: PascalCase
user.types.ts
api.types.ts

// Constants: SCREAMING_SNAKE_CASE
API_ENDPOINTS.ts
TIER_LIMITS.ts
```

### 2.2 Naming Conventions

```typescript
// Variables & Functions: camelCase
const userName = 'John';
const isAuthenticated = true;
function getUserData() {}

// Types & Interfaces: PascalCase
type UserProfile = { ... }
interface ChatMessage { ... }
class AuthService { ... }

// Constants: SCREAMING_SNAKE_CASE
const MAX_RETRY_ATTEMPTS = 3;
const API_BASE_URL = 'https://api.aicafe.vn';

// Boolean variables: prefix with 'is', 'has', 'can', 'should'
const isLoading = true;
const hasCredits = true;
const canAccess = true;
const shouldRefresh = false;

// Enum members: PascalCase
enum OrderStatus {
  Pending = 'pending',
  Completed = 'completed',
  Failed = 'failed',
}
```

### 2.3 Type Definitions

```typescript
// Prefer interfaces for object shapes
interface User {
  id: string;
  phone: string;
  name?: string;
  tier: MembershipTier;
  createdAt: Date;
}

// Use type for unions, intersections, and utility types
type ApiResponse<T> = {
  success: boolean;
  data?: T;
  error?: ApiError;
};

type PaymentMethod = 'vnpay' | 'momo' | 'zalopay' | 'card';

type Props = {
  children: React.ReactNode;
  className?: string;
};

// Use readonly for immutability
interface Config {
  readonly apiUrl: string;
  readonly maxRetries: number;
}

// Generic constraints
function getData<T extends { id: string }>(items: T[]): T {
  return items[0];
}
```

### 2.4 Imports/Exports

```typescript
// Named exports (preferred)
export const API_URL = 'https://api.aicafe.vn';
export function getUser() { }
export class AuthService { }

// Default exports (use sparingly)
export default function App() { }

// Barrel exports for modules
// types/index.ts
export * from './user';
export * from './order';
export * from './payment';

// Sorted imports
import React, { useState, useEffect } from 'react';           // React
import { View, Text } from 'react-native';                     // Internal modules
import { Button, Input } from '@/components/ui';              // Components
import { useAuthStore } from '@/stores/authStore';             // Stores
import { apiClient } from '@/services/api';                    // Services
import { TIER_LIMITS, CREDIT_COSTS } from '@aicafe/shared';    // Packages
```

---

## 3. React/React Native Guidelines

### 3.1 Component Structure

```typescript
// 1. Imports
import React, { useState, useCallback } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { useRouter } from 'expo-router';
import { useAuthStore } from '@/stores';
import { Button, Card } from '@/components/ui';

// 2. Types
interface Props {
  userId: string;
  onSuccess?: () => void;
}

// 3. Component
export function UserProfile({ userId, onSuccess }: Props) {
  // Hooks first
  const router = useRouter();
  const { user, updateUser } = useAuthStore();
  
  // State
  const [isLoading, setIsLoading] = useState(false);
  const [name, setName] = useState(user?.name || '');
  
  // Effects
  useEffect(() => {
    fetchUser();
  }, [userId]);
  
  // Callbacks
  const handleSave = useCallback(async () => {
    setIsLoading(true);
    try {
      await updateUser({ name });
      onSuccess?.();
    } catch (error) {
      // Handle error
    } finally {
      setIsLoading(false);
    }
  }, [name, updateUser, onSuccess]);
  
  // Render
  return (
    <Card>
      <Text>{name}</Text>
      <Button onPress={handleSave} loading={isLoading}>
        Lưu
      </Button>
    </Card>
  );
}

// 4. Styles (co-located)
const styles = StyleSheet.create({
  container: { flex: 1 },
  title: { fontSize: 18, fontWeight: '600' },
});

// 5. Named export preferred, default export for pages
export default UserProfile;
```

### 3.2 Hooks Guidelines

```typescript
// Custom hooks must start with 'use'
export function useCredits() {
  // ...
}

export function useChatStream(sessionId: string) {
  // ...
}

// Return object for multiple values (easier to extend)
function useAuth() {
  return {
    user,
    isAuthenticated,
    login,
    logout,
  };
}

// Use callback/memo for expensive operations
const memoizedValue = useMemo(() => computeExpensiveValue(a, b), [a, b]);
const memoizedCallback = useCallback(() => doSomething(a, b), [a, b]);

// Custom hooks should handle loading/error states
function useUser(userId: string) {
  const [data, setData] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    // fetch logic...
  }, [userId]);

  return { data, isLoading, error };
}
```

### 3.3 Next.js Specific

```typescript
// Server Components (default)
// app/users/page.tsx
async function UsersPage() {
  const users = await db.user.findMany(); // Server-side
  return <UserList users={users} />;
}

// Client Components
'use client';

import { useState } from 'react';
export function ChatInput() {
  const [message, setMessage] = useState('');
  // ...
}

// Use React Server Actions for mutations
async function submitForm(formData: FormData) {
  'use server';
  // Server-side logic
}
```

---

## 4. API Design Guidelines

### 4.1 REST Endpoints

```
GET     /api/v1/users              # List users
GET     /api/v1/users/:id          # Get single user
POST    /api/v1/users              # Create user
PATCH   /api/v1/users/:id          # Update user
DELETE  /api/v1/users/:id          # Delete user

GET     /api/v1/users/me           # Current user
GET     /api/v1/users/me/wallet    # User wallet

POST    /api/v1/auth/send-otp      # Send OTP
POST    /api/v1/auth/verify-otp    # Verify OTP
POST    /api/v1/auth/refresh        # Refresh token
POST    /api/v1/auth/logout         # Logout

GET     /api/v1/orders             # List orders
POST    /api/v1/orders             # Create order
GET     /api/v1/orders/:id         # Get order
POST    /api/v1/orders/:id/confirm # Confirm payment

POST    /api/v1/ai/chat            # Chat completion
POST    /api/v1/ai/chat/stream     # Streaming chat
POST    /api/v1/ai/image           # Image generation
POST    /api/v1/ai/video           # Video generation
```

### 4.2 Request/Response Format

```typescript
// Request body (Zod schema)
const CreateOrderSchema = z.object({
  packageId: z.string().min(1),
  paymentMethod: z.enum(['vnpay', 'momo', 'zalopay', 'card']),
});

// Response wrapper
interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    details?: Record<string, unknown>;
  };
  meta?: {
    page?: number;
    limit?: number;
    total?: number;
  };
}

// Example response
{
  "success": true,
  "data": {
    "id": "ord_123",
    "status": "pending",
    "paymentUrl": "https://vnpay.vn/..."
  },
  "meta": {
    "timestamp": "2024-01-15T10:30:00Z"
  }
}

// Error response
{
  "success": false,
  "error": {
    "code": "INSUFFICIENT_CREDITS",
    "message": "Bạn không đủ credits để thực hiện yêu cầu này",
    "details": {
      "required": 50,
      "available": 20
    }
  }
}
```

### 4.3 HTTP Status Codes

| Code | Usage |
|------|-------|
| 200 | Success (GET, PATCH) |
| 201 | Created (POST) |
| 204 | No Content (DELETE) |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (not logged in) |
| 403 | Forbidden (no permission) |
| 404 | Not Found |
| 409 | Conflict (duplicate) |
| 422 | Unprocessable Entity (business logic) |
| 429 | Too Many Requests (rate limit) |
| 500 | Internal Server Error |

---

## 5. Python Guidelines (AI Gateway)

### 5.1 File Structure

```python
# src/
#   ├── main.py              # FastAPI app
#   ├── api/
#   │   ├── __init__.py
#   │   ├── routes/
#   │   │   ├── chat.py
#   │   │   ├── image.py
#   │   │   └── video.py
#   │   └── deps.py          # Dependencies
#   ├── core/
#   │   ├── __init__.py
#   │   ├── config.py        # Settings
#   │   ├── security.py      # Auth
#   │   └── exceptions.py    # Custom exceptions
#   ├── models/
#   │   ├── __init__.py
#   │   ├── chat.py
#   │   └── user.py
#   ├── services/
#   │   ├── __init__.py
#   │   ├── openai_service.py
#   │   ├── anthropic_service.py
#   │   └── router.py        # Model routing
#   └── utils/
#       ├── __init__.py
#       └── rate_limiter.py
```

### 5.2 Python Code Style

```python
# naming_convention.py (PEP 8)
# Classes: PascalCase
class ChatService:
    pass

# Functions & Variables: snake_case
def get_user_data(user_id: str) -> dict:
    total_credits = 1000
    return {"user_id": user_id, "credits": total_credits}

# Constants: UPPER_SNAKE_CASE
MAX_RETRIES = 3
API_TIMEOUT = 30

# Private: _single_leading_underscore
def _internal_helper():
    pass

# Type hints (required)
def process_chat(
    message: str,
    model: str,
    user_id: str,
    stream: bool = False,
) -> dict | AsyncGenerator[str, None]:
    ...
```

### 5.3 FastAPI Patterns

```python
# src/api/routes/chat.py
from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel, Field

router = APIRouter(prefix="/chat", tags=["Chat"])

class ChatRequest(BaseModel):
    message: str = Field(..., min_length=1, max_length=10000)
    model: str = Field(default="gpt-4o")
    stream: bool = False

class ChatResponse(BaseModel):
    id: str
    content: str
    model: str
    credits_used: int

@router.post("", response_model=ChatResponse)
async def chat(
    request: ChatRequest,
    user: User = Depends(get_current_user),
) -> ChatResponse:
    """Chat completion endpoint."""
    try:
        result = await chat_service.process(
            message=request.message,
            model=request.model,
            user_id=user.id,
        )
        return ChatResponse(**result)
    except InsufficientCreditsError as e:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=str(e),
        )
```

---

## 6. Database Guidelines

### 6.1 Prisma Schema

```prisma
// Naming
// - Model names: PascalCase
// - Field names: camelCase
// - Enum names: PascalCase
// - Enum values: SCREAMING_SNAKE_CASE

model User {
  id            String      @id @default(uuid())
  phone         String      @unique
  name          String?
  email         String?     @unique
  tier          MembershipTier @default(BASIC)
  createdAt     DateTime    @default(now())
  updatedAt     DateTime    @updatedAt

  // Relations
  wallet        Wallet?
  orders        Order[]
  sessions      ChatSession[]
}

model Wallet {
  id              String  @id @default(uuid())
  userId          String  @unique
  user            User    @relation(fields: [userId], references: [id])
  credits         Int     @default(0)
  workspaceMinutes Int    @default(0)
  updatedAt       DateTime @updatedAt
}

enum MembershipTier {
  BASIC
  DEVELOPER
  PRO
  BUILDER
}
```

### 6.2 Queries

```typescript
// Prefer Prisma client over raw SQL
const user = await prisma.user.findUnique({
  where: { id: userId },
  include: {
    wallet: true,
    membership: true,
  },
});

// Use pagination for lists
const orders = await prisma.order.findMany({
  where: { userId },
  orderBy: { createdAt: 'desc' },
  skip: (page - 1) * limit,
  take: limit,
});

// Use transactions for complex operations
await prisma.$transaction([
  prisma.wallet.update({
    where: { userId },
    data: { credits: { decrement: amount } },
  }),
  prisma.creditTransaction.create({
    data: {
      userId,
      type: 'USAGE',
      amount: -amount,
      service: 'chat',
    },
  }),
]);
```

---

## 7. Git Conventions

### 7.1 Branch Naming

```
feature/add-otp-auth
feature/chat-streaming
feature/image-generation
feature/credit-system
bugfix/fix-login-otp
hotfix/urgent-payment-fix
refactor/cleanup-auth
docs/update-api-spec
```

### 7.2 Commit Messages

```
feat: add OTP authentication
feat: implement chat streaming
feat: add image generation API

fix: resolve OTP verification timeout
fix: fix credit deduction calculation

refactor: simplify auth middleware
refactor: extract API client

docs: update API documentation
docs: add deployment guide

test: add credit system tests
test: add AI gateway integration tests

chore: update dependencies
chore: setup CI/CD pipeline
```

### 7.3 Pull Request Format

```markdown
## Description
Brief description of the changes.

## Type of Change
- [ ] Feature
- [ ] Bug fix
- [ ] Refactoring
- [ ] Documentation

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed

## Screenshots (if UI changes)
Before | After
:-----:|:-----:
img1   | img2

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No console errors
```

---

## 8. Code Review Checklist

### General
- [ ] Code follows naming conventions
- [ ] No commented-out code
- [ ] No console.log/debug statements
- [ ] Proper error handling
- [ ] TypeScript types are correct

### React/React Native
- [ ] Component names are PascalCase
- [ ] Hooks have proper dependencies
- [ ] No memory leaks (subscriptions)
- [ ] Loading states implemented
- [ ] Error boundaries where needed

### API/Backend
- [ ] Input validation (Zod)
- [ ] Authentication/Authorization
- [ ] Rate limiting
- [ ] Proper HTTP status codes
- [ ] API documentation updated

### Testing
- [ ] Unit tests for utilities
- [ ] Component tests
- [ ] Integration tests for API
- [ ] E2E tests for critical flows

---

## 9. Documentation Requirements

### Code Comments
```typescript
// Good: explains WHY, not WHAT
// Retry with exponential backoff to handle temporary network issues
async function fetchWithRetry(url: string) {
  for (let i = 0; i < MAX_RETRIES; i++) {
    try {
      return await fetch(url);
    } catch (error) {
      if (i === MAX_RETRIES - 1) throw error;
      await sleep(Math.pow(2, i) * 1000);
    }
  }
}

// Bad: states the obvious
// Increment the counter
counter++;

// Bad: no comments at all
async function fetchWithRetry(url: string) { ... }
```

### Function Documentation
```typescript
/**
 * Calculate credits required for a chat request.
 * 
 * @param model - The AI model to use
 * @param inputTokens - Number of input tokens
 * @param outputTokens - Number of output tokens (estimated)
 * @returns Total credits required
 * 
 * @example
 * const credits = calculateChatCredits('gpt-4o', 100, 200);
 * // credits = 2.5 + 2.0 = 4.5
 */
function calculateChatCredits(
  model: string,
  inputTokens: number,
  outputTokens: number
): number {
  // ...
}
```

---

## 10. ESLint & Prettier Configuration

### .eslintrc.js
```javascript
module.exports = {
  extends: [
    'expo',
    'plugin:@typescript-eslint/recommended',
  ],
  rules: {
    '@typescript-eslint/no-unused-vars': ['error', { argsIgnorePattern: '^_' }],
    '@typescript-eslint/explicit-function-return-type': 'off',
    '@typescript-eslint/no-explicit-any': 'warn',
    'react-hooks/exhaustive-deps': 'error',
    'prefer-const': 'error',
    'no-var': 'error',
  },
};
```

### .prettierrc
```json
{
  "semi": false,
  "singleQuote": true,
  "trailingComma": "es5",
  "printWidth": 100,
  "tabWidth": 2,
  "useTabs": false,
  "bracketSpacing": true,
  "arrowParens": "avoid"
}
```
