# AI Café Platform - Frontend Design

## 1. Design System

### 1.1 Design Principles

```
┌─────────────────────────────────────────────────────────┐
│              Design Principles                          │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  1. Purposeful                                          │
│     Every element serves a function                      │
│                                                         │
│  2. Accessible                                          │
│     WCAG 2.1 AA compliance                              │
│                                                         │
│  3. Consistent                                         │
│     Same patterns across all screens                    │
│                                                         │
│  4. Responsive                                         │
│     Mobile-first approach                               │
│                                                         │
│  5. Performance                                         │
│     Fast loading, smooth transitions                    │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### 1.2 Color Palette

```css
:root {
  /* Primary Colors */
  --primary-50: #f0f4ff;
  --primary-100: #e0e8ff;
  --primary-200: #c7d4ff;
  --primary-300: #a4b4ff;
  --primary-400: #7f8fff;
  --primary-500: #5a6bff;      /* Main brand color */
  --primary-600: #4855e8;
  --primary-700: #3a44d1;
  --primary-800: #3137aa;
  --primary-900: #2a3087;

  /* Neutral Colors */
  --gray-50: #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-300: #d1d5db;
  --gray-400: #9ca3af;
  --gray-500: #6b7280;
  --gray-600: #4b5563;
  --gray-700: #374151;
  --gray-800: #1f2937;
  --gray-900: #111827;

  /* Semantic Colors */
  --success: #10b981;
  --warning: #f59e0b;
  --error: #ef4444;
  --info: #3b82f6;

  /* Background */
  --bg-primary: #ffffff;
  --bg-secondary: #f9fafb;
  --bg-tertiary: #f3f4f6;
  --bg-inverse: #111827;

  /* Text */
  --text-primary: #111827;
  --text-secondary: #6b7280;
  --text-tertiary: #9ca3af;
  --text-inverse: #ffffff;

  /* Border */
  --border-light: #e5e7eb;
  --border-default: #d1d5db;
  --border-focus: #5a6bff;
}
```

### 1.3 Typography

```css
:root {
  /* Font Family */
  --font-sans: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  --font-mono: 'JetBrains Mono', 'Fira Code', monospace;

  /* Font Sizes */
  --text-xs: 0.75rem;    /* 12px */
  --text-sm: 0.875rem;   /* 14px */
  --text-base: 1rem;     /* 16px */
  --text-lg: 1.125rem;    /* 18px */
  --text-xl: 1.25rem;     /* 20px */
  --text-2xl: 1.5rem;     /* 24px */
  --text-3xl: 1.875rem;    /* 30px */
  --text-4xl: 2.25rem;     /* 36px */

  /* Line Heights */
  --leading-none: 1;
  --leading-tight: 1.25;
  --leading-snug: 1.375;
  --leading-normal: 1.5;
  --leading-relaxed: 1.625;

  /* Font Weights */
  --font-normal: 400;
  --font-medium: 500;
  --font-semibold: 600;
  --font-bold: 700;
}
```

### 1.4 Spacing System

```css
:root {
  /* Spacing Scale (4px base) */
  --space-0: 0;
  --space-1: 0.25rem;   /* 4px */
  --space-2: 0.5rem;    /* 8px */
  --space-3: 0.75rem;   /* 12px */
  --space-4: 1rem;      /* 16px */
  --space-5: 1.25rem;   /* 20px */
  --space-6: 1.5rem;    /* 24px */
  --space-8: 2rem;      /* 32px */
  --space-10: 2.5rem;   /* 40px */
  --space-12: 3rem;      /* 48px */
  --space-16: 4rem;      /* 64px */
  --space-20: 5rem;     /* 80px */
  --space-24: 6rem;      /* 96px */
}
```

### 1.5 Border Radius

```css
:root {
  --radius-none: 0;
  --radius-sm: 0.25rem;    /* 4px */
  --radius-md: 0.5rem;     /* 8px */
  --radius-lg: 0.75rem;    /* 12px */
  --radius-xl: 1rem;       /* 16px */
  --radius-2xl: 1.5rem;    /* 24px */
  --radius-full: 9999px;
}
```

### 1.6 Shadows

```css
:root {
  --shadow-xs: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  --shadow-sm: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
  --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  --shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
}
```

## 2. Component Library

### 2.1 Base Components

```
components/
├── ui/
│   ├── button/
│   │   ├── Button.tsx
│   │   ├── Button.stories.tsx
│   │   └── Button.test.tsx
│   ├── input/
│   │   ├── Input.tsx
│   │   ├── InputTextarea.tsx
│   │   └── InputLabel.tsx
│   ├── card/
│   │   ├── Card.tsx
│   │   ├── CardHeader.tsx
│   │   ├── CardBody.tsx
│   │   └── CardFooter.tsx
│   ├── modal/
│   │   ├── Modal.tsx
│   │   ├── ModalHeader.tsx
│   │   ├── ModalBody.tsx
│   │   └── ModalFooter.tsx
│   ├── dropdown/
│   │   ├── Dropdown.tsx
│   │   ├── DropdownItem.tsx
│   │   └── DropdownMenu.tsx
│   ├── tabs/
│   │   ├── Tabs.tsx
│   │   ├── Tab.tsx
│   │   └── TabPanel.tsx
│   ├── badge/
│   │   ├── Badge.tsx
│   │   └── BadgeVariants.tsx
│   ├── avatar/
│   │   ├── Avatar.tsx
│   │   └── AvatarGroup.tsx
│   ├── tooltip/
│   │   └── Tooltip.tsx
│   ├── toast/
│   │   ├── Toast.tsx
│   │   ├── ToastProvider.tsx
│   │   └── useToast.ts
│   └── skeleton/
│       ├── Skeleton.tsx
│       └── SkeletonText.tsx
```

### 2.2 Button Component

```tsx
// Button.tsx
interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
}

const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  size = 'md',
  isLoading = false,
  leftIcon,
  rightIcon,
  className = '',
  disabled,
  ...props
}) => {
  const baseStyles = 'inline-flex items-center justify-center font-medium rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2';
  
  const variants = {
    primary: 'bg-primary-500 text-white hover:bg-primary-600 focus:ring-primary-500',
    secondary: 'bg-gray-100 text-gray-900 hover:bg-gray-200 focus:ring-gray-500',
    ghost: 'text-gray-700 hover:bg-gray-100 focus:ring-gray-500',
    danger: 'bg-red-500 text-white hover:bg-red-600 focus:ring-red-500',
  };

  const sizes = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-6 py-3 text-lg',
  };

  return (
    <button
      className={`${baseStyles} ${variants[variant]} ${sizes[size]} ${className}`}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading && <Spinner />}
      {!isLoading && leftIcon}
      {children}
      {!isLoading && rightIcon}
    </button>
  );
};
```

### 2.3 Component Variants

```tsx
// Button variants showcase
const ButtonShowcase = () => (
  <div className="space-y-4">
    {/* Primary */}
    <Button variant="primary">Primary Button</Button>
    
    {/* Secondary */}
    <Button variant="secondary">Secondary Button</Button>
    
    {/* Ghost */}
    <Button variant="ghost">Ghost Button</Button>
    
    {/* Danger */}
    <Button variant="danger">Delete</Button>
    
    {/* Sizes */}
    <div className="flex gap-2">
      <Button size="sm">Small</Button>
      <Button size="md">Medium</Button>
      <Button size="lg">Large</Button>
    </div>
    
    {/* With Icons */}
    <Button leftIcon={<SaveIcon />}>Save</Button>
    <Button rightIcon={<ArrowRightIcon />}>Next</Button>
    
    {/* Loading */}
    <Button isLoading>Saving...</Button>
    
    {/* Disabled */}
    <Button disabled>Disabled</Button>
  </div>
);
```

## 3. Feature Components

### 3.1 AI Chat Component

```
components/
├── features/
│   └── ai-chat/
│       ├── ChatContainer.tsx
│       ├── ChatMessage.tsx
│       ├── ChatInput.tsx
│       ├── ChatHeader.tsx
│       ├── ChatHistory.tsx
│       ├── TypingIndicator.tsx
│       ├── CodeBlock.tsx
│       ├── MarkdownRenderer.tsx
│       └── useChat.ts (hook)
```

**Chat UI Layout:**

```
┌─────────────────────────────────────────────────────────┐
│  Chat                                          [Model ▼]│
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │ 👤 User                                          │   │
│  │ Viết code React component cho button            │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │ 🤖 AI                                    12:34   │   │
│  │ Đây là code button component:                   │   │
│  │ ```tsx                                          │   │
│  │ const Button = ({                              │   │
│  │   children, variant = 'primary'               │   │
│  │ }) => (                                        │   │
│  │   <button className={variant}>                  │   │
│  │     {children}                                 │   │
│  │   </button>                                    │   │
│  │ );                                             │   │
│  │ ```                                            │   │
│  │ Credits: 150                                  │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────┐ [Send]        │
│  │ Type your message...                 │               │
│  └─────────────────────────────────────┘               │
│                                                         │
│  [📎] [💬 Chat] [💻 Code] [🖼️ Image] [🎬 Video]     │
└─────────────────────────────────────────────────────────┘
```

### 3.2 Wallet Component

```
components/
├── features/
│   └── wallet/
│       ├── WalletCard.tsx
│       ├── CreditBalance.tsx
│       ├── WorkspaceTimer.tsx
│       ├── TransactionList.tsx
│       ├── TransactionItem.tsx
│       ├── CreditBreakdown.tsx
│       └── useWallet.ts (hook)
```

**Wallet UI Layout:**

```
┌─────────────────────────────────────────────────────────┐
│  AI WALLET                                             │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │  💰 Credits                    🎯 Tier: Developer│   │
│  │  ─────────────────────────────────────────────  │   │
│  │                                                 │   │
│  │      8,450              →        5,000          │   │
│  │     Total                   Daily Limit         │   │
│  │                                                 │   │
│  │  ████████████████░░░░░░░░░░░░░░  Used: 550    │   │
│  │  Remaining today: 4,450                         │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │  ⏱️ Workspace                    2h 35m          │   │
│  │  remaining                                         │   │
│  │                              [ Start Workspace ] │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  Recent Transactions                                   │
│  ──────────────────────────────────────────────       │
│  │ 🟢 +5,000   Developer Drink        12:30  │       │
│  │ 🔴 -150     Chat                  12:25  │       │
│  │ 🔴 -200     Code                  12:20  │       │
│  │ 🔴 -500     Reasoning             12:15  │       │
│                                                         │
│  [ View All Transactions ]                             │
└─────────────────────────────────────────────────────────┘
```

### 3.3 Code Editor Component

```
components/
├── features/
│   └── code-editor/
│       ├── CodeEditor.tsx
│       ├── CodeEditorTabs.tsx
│       ├── FileTree.tsx
│       ├── Terminal.tsx
│       ├── AIAssistPanel.tsx
│       ├── CompletionPopup.tsx
│       └── useCodeEditor.ts (hook)
```

**Code Editor Layout:**

```
┌─────────────────────────────────────────────────────────────────┐
│  Workspace: My Project            [▶ Run] [🤖 AI] [⚙️]      │
├──────────┬────────────────────────────────────────────────────┤
│ Files     │  index.tsx                          [Tab 1] [Tab 2]│
│ ────────  ├────────────────────────────────────────────────────┤
│ 📁 src    │  1  │ import React from 'react';                  │
│  ├─ index │  2  │ import { Button } from './components';     │
│  ├─ App   │  3  │                                             │
│  └─ utils │  4  │ const App = () => {                        │
│ 📁 public │  5  │   return (                                 │
│  └─ index │  6  │     <div className="container">           │
│            │  7  │       <Button                             │
│            │  8  │ │  variant="primary"                     │
│            │  9  │ │  onClick={handleClick}                 │
│            │ 10→ │   │  children="Click me"                 │
│            │ 11  │   />                                     │
│            │ 12  │   </div>                                  │
│            │ 13  │ );                                       │
│            │ 14  │ };                                       │
│            │     │                                             │
│            │     │ export default App;                       │
├───────────┼────────────────────────────────────────────────────┤
│ Output    │  ┌──────────────────────────────────────────┐    │
│ ───────── │  │                                          │    │
│ > Building│  │    ┌─────────────────────┐              │    │
│ > Running │  │    │                     │              │    │
│            │  │    │     Click me        │              │    │
│            │  │    │                     │              │    │
│            │  │    └─────────────────────┘              │    │
│            │  │                                          │    │
└────────────┴──────────────────────────────────────────────────┘
│  AI Assistant                                              │
│  ─────────────────────────────────────────────────────────│
│  │ 🤖 I notice you can simplify this component...        ││
│  │ [Apply Fix] [View Suggestions] [Ask More]           ││
└─────────────────────────────────────────────────────────┘
```

## 4. Page Layouts

### 4.1 Main App Layout

```tsx
// layout.tsx (Main App)
const MainLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <Header
        user={user}
        onMenuClick={() => toggleSidebar()}
      />

      {/* Main Content Area */}
      <div className="flex">
        {/* Sidebar Navigation */}
        <Sidebar
          isOpen={sidebarOpen}
          onClose={() => setSidebarOpen(false)}
        />

        {/* Page Content */}
        <main className="flex-1 p-6">
          {children}
        </main>
      </div>

      {/* Bottom Navigation (Mobile) */}
      <BottomNav />
    </div>
  );
};
```

### 4.2 Dashboard Page

```tsx
// dashboard/page.tsx
const DashboardPage: React.FC = () => {
  const { wallet, usage } = useDashboard();

  return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <WelcomeBanner user={user} />

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Credits"
          value={wallet.credits}
          icon={<CreditIcon />}
          trend={wallet.trend}
        />
        <StatCard
          title="Workspace Time"
          value={formatTime(wallet.workspaceMinutes)}
          icon={<ClockIcon />}
        />
        <StatCard
          title="This Month"
          value={usage.thisMonth}
          icon={<ChartIcon />}
          trend={usage.trend}
        />
        <StatCard
          title="Tier"
          value={wallet.tier}
          icon={<BadgeIcon />}
          variant="highlight"
        />
      </div>

      {/* Quick Actions */}
      <QuickActions />

      {/* Recent Activity */}
      <RecentActivity />
    </div>
  );
};
```

### 4.3 AI Chat Page

```tsx
// ai/chat/page.tsx
const ChatPage: React.FC = () => {
  const [selectedModel, setSelectedModel] = useState('auto');
  const { messages, sendMessage, isLoading } = useChat();

  return (
    <div className="h-[calc(100vh-8rem)] flex flex-col">
      {/* Model Selector */}
      <ModelSelector
        selected={selectedModel}
        onChange={setSelectedModel}
      />

      {/* Chat Messages */}
      <div className="flex-1 overflow-y-auto">
        <ChatMessageList messages={messages} />
        {isLoading && <TypingIndicator />}
      </div>

      {/* Input Area */}
      <ChatInput
        onSend={sendMessage}
        isLoading={isLoading}
        credits={wallet.credits}
      />
    </div>
  );
};
```

## 5. Mobile Responsive Design

### 5.1 Breakpoints

```css
/* Mobile First Approach */
@media (min-width: 640px) { /* sm */ }
@media (min-width: 768px) { /* md */ }
@media (min-width: 1024px) { /* lg */ }
@media (min-width: 1280px) { /* xl */ }
@media (min-width: 1536px) { /* 2xl */ }
```

### 5.2 Responsive Layouts

```tsx
// Responsive Grid
const ProductGrid: React.FC<{ products: Product[] }> = ({ products }) => (
  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
    {products.map(product => (
      <ProductCard key={product.id} product={product} />
    ))}
  </div>
);

// Bottom Sheet for Mobile
const MobileActions: React.FC = () => (
  <div className="fixed bottom-0 left-0 right-0 bg-white border-t lg:hidden">
    <div className="flex items-center justify-around py-4">
      <MobileNavButton icon={<ChatIcon />} label="Chat" href="/ai/chat" />
      <MobileNavButton icon={<CodeIcon />} label="Code" href="/ai/code" />
      <MobileNavButton icon={<ImageIcon />} label="Image" href="/ai/image" />
      <MobileNavButton icon={<WalletIcon />} label="Wallet" href="/wallet" />
    </div>
  </div>
);
```

### 5.3 Mobile Menu

```
┌─────────────────────────────────────────────────────────┐
│  ☰  AI Café                              👤 Profile     │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Quick Actions                                          │
│  ┌────────┐ ┌────────┐ ┌────────┐                     │
│  │  Chat  │ │  Code  │ │ Image  │                     │
│  └────────┘ └────────┘ └────────┘                     │
│                                                         │
│  ─────────────────────────────────────────────         │
│                                                         │
│  📊 Dashboard                                          │
│  💬 AI Tools                                           │
│     ├─ Chat                                            │
│     ├─ Code                                           │
│     ├─ Image                                          │
│     └─ Video                                          │
│  💼 Workspace                                         │
│  👛 Wallet                                            │
│  🛒 Orders                                            │
│  🎫 Membership                                        │
│  🎁 Loyalty                                           │
│                                                         │
│  ─────────────────────────────────────────────         │
│                                                         │
│  ⚙️ Settings                                          │
│  ❓ Help & Support                                    │
│  🚪 Logout                                            │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## 6. Animation & Transitions

### 6.1 Transition Presets

```css
/* Transitions */
.transition-fast: transition-all duration-150ms ease-in-out;
.transition-normal: transition-all duration-300ms ease-in-out;
.transition-slow: transition-all duration-500ms ease-in-out;

/* Animations */
@keyframes fadeIn: opacity 0 → 1;
@keyframes slideUp: translateY(20px) → translateY(0);
@keyframes slideDown: translateY(-20px) → translateY(0);
@keyframes scaleIn: scale(0.95) → scale(1);
@keyframes spin: rotate 0deg → 360deg;

/* Loading Skeleton */
@keyframes shimmer: background-position -200% 0 → 200% 0;
```

### 6.2 Micro-interactions

```tsx
// Button Press
const Button: React.FC<ButtonProps> = ({ onClick, ...props }) => (
  <motion.button
    whileTap={{ scale: 0.95 }}
    whileHover={{ scale: 1.02 }}
    onClick={onClick}
    {...props}
  />
);

// Card Hover
const Card: React.FC<CardProps> = ({ children }) => (
  <motion.div
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    whileHover={{ y: -4, boxShadow: '0 12px 24px rgba(0,0,0,0.1)' }}
  >
    {children}
  </motion.div>
);

// Stagger Animation for List
const AnimatedList: React.FC<{ items: Item[] }> = ({ items }) => (
  <motion.div
    initial="hidden"
    animate="visible"
    variants={{
      hidden: { opacity: 0 },
      visible: {
        opacity: 1,
        transition: { staggerChildren: 0.1 }
      }
    }}
  >
    {items.map(item => (
      <motion.div key={item.id} variants={itemVariant}>
        {item.content}
      </motion.div>
    ))}
  </motion.div>
);
```

## 7. State Management

### 7.1 Store Structure

```tsx
// stores/authStore.ts
interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: false,
  isLoading: true,
  error: null,

  login: async (credentials) => {
    set({ isLoading: true, error: null });
    try {
      const user = await authService.login(credentials);
      set({ user, isAuthenticated: true, isLoading: false });
    } catch (error) {
      set({ error: error.message, isLoading: false });
    }
  },

  logout: () => {
    set({ user: null, isAuthenticated: false });
  },
}));

// stores/walletStore.ts
interface WalletState {
  credits: number;
  workspaceMinutes: number;
  tier: string;
  dailyLimit: number;
  dailyUsed: number;
  transactions: Transaction[];
}

const useWalletStore = create<WalletState>((set, get) => ({
  credits: 0,
  workspaceMinutes: 0,
  tier: 'basic',
  dailyLimit: 0,
  dailyUsed: 0,
  transactions: [],

  refresh: async () => {
    const wallet = await walletService.getBalance();
    set(wallet);
  },

  deductCredits: (amount: number) => {
    set(state => ({
      credits: state.credits - amount,
      dailyUsed: state.dailyUsed + amount,
    }));
  },
}));
```

### 7.2 API Hook Pattern

```tsx
// hooks/useAPI.ts
export const useApi = <T>(url: string, options?: UseApiOptions) => {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await api.get(url, options);
      setData(response.data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [url, options]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { data, loading, error, refetch: fetchData };
};

// Usage
const { data: user, loading, refetch } = useApi<User>('/api/v1/users/me');
```

## 8. Form Design

### 8.1 Form Patterns

```tsx
// forms/CheckoutForm.tsx
const CheckoutForm: React.FC<CheckoutFormProps> = ({ onSubmit }) => {
  const [formData, setFormData] = useState({
    items: [],
    paymentMethod: 'cash',
    notes: '',
  });

  const { register, handleSubmit, watch, formState: { errors } } = useForm();

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Items Selection */}
      <FormSection title="Select Items">
        <ProductSelector
          products={products}
          selected={formData.items}
          onChange={(items) => setFormData({ ...formData, items })}
        />
      </FormSection>

      {/* Payment Method */}
      <FormSection title="Payment">
        <RadioGroup
          options={paymentMethods}
          value={formData.paymentMethod}
          onChange={(value) => setFormData({ ...formData, paymentMethod: value })}
        />
      </FormSection>

      {/* Notes */}
      <FormField label="Notes" error={errors.notes?.message}>
        <Textarea
          {...register('notes')}
          placeholder="Any special requests..."
        />
      </FormField>

      {/* Order Summary */}
      <OrderSummary items={formData.items} />

      {/* Submit */}
      <Button type="submit" isLoading={isSubmitting}>
        Complete Order
      </Button>
    </form>
  );
};
```

## 9. Error Handling UI

### 9.1 Error States

```tsx
// components/ErrorBoundary.tsx
const ErrorBoundary: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <ReactErrorBoundary
      FallbackComponent={ErrorFallback}
      onError={(error) => logError(error)}
    >
      {children}
    </ReactErrorBoundary>
  );
};

const ErrorFallback: React.FC<{ error: Error }> = ({ error }) => (
  <div className="flex flex-col items-center justify-center p-8">
    <AlertCircle className="w-16 h-16 text-red-500 mb-4" />
    <h2 className="text-xl font-semibold mb-2">Something went wrong</h2>
    <p className="text-gray-600 mb-4">{error.message}</p>
    <Button onClick={() => window.location.reload()}>
      Reload Page
    </Button>
  </div>
);

// Empty State
const EmptyState: React.FC<EmptyStateProps> = ({ 
  icon, 
  title, 
  description, 
  action 
}) => (
  <div className="flex flex-col items-center justify-center py-12">
    <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
      {icon}
    </div>
    <h3 className="text-lg font-medium mb-2">{title}</h3>
    <p className="text-gray-500 mb-4 text-center max-w-md">{description}</p>
    {action}
  </div>
);

// Loading State
const LoadingState: React.FC<{ message?: string }> = ({ message }) => (
  <div className="flex flex-col items-center justify-center py-12">
    <Spinner size="lg" />
    <p className="mt-4 text-gray-500">{message || 'Loading...'}</p>
  </div>
);
```

## 10. Accessibility (A11y)

### 10.1 ARIA Patterns

```tsx
// Accessible Modal
const Modal: React.FC<ModalProps> = ({ isOpen, onClose, title, children }) => {
  const modalRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (isOpen) {
      modalRef.current?.focus();
    }
  }, [isOpen]);

  return (
    <Dialog
      open={isOpen}
      onClose={onClose}
      className="relative z-50"
    >
      <div className="fixed inset-0 bg-black/50" aria-hidden="true" />
      
      <div className="fixed inset-0 flex items-center justify-center p-4">
        <DialogPanel
          ref={modalRef}
          className="w-full max-w-md bg-white rounded-lg p-6"
          aria-labelledby="modal-title"
        >
          <DialogTitle id="modal-title" className="text-lg font-semibold">
            {title}
          </DialogTitle>
          {children}
        </DialogPanel>
      </div>
    </Dialog>
  );
};

// Accessible Tabs
const Tabs: React.FC<TabsProps> = ({ tabs, selected, onChange }) => (
  <Tab.Group selectedIndex={tabs.indexOf(selected)} onChange={onChange}>
    <Tab.List className="flex space-x-1 bg-gray-100 p-1 rounded-lg">
      {tabs.map(tab => (
        <Tab
          key={tab}
          className={({ selected }) =>
            classNames(
              'w-full py-2 text-sm font-medium rounded-md',
              selected
                ? 'bg-white shadow text-primary-600'
                : 'text-gray-600 hover:text-gray-900'
            )
          }
        >
          {tab}
        </Tab>
      ))}
    </Tab.List>
    <Tab.Panels>
      {tabs.map(tab => (
        <Tab.Panel key={tab}>{/* Panel content */}</Tab.Panel>
      ))}
    </Tab.Panels>
  </Tab.Group>
);
```

### 10.2 Keyboard Navigation

```tsx
// Keyboard shortcuts
const KeyboardShortcuts: React.FC = () => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // Cmd/Ctrl + K: Open command palette
      if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
        e.preventDefault();
        openCommandPalette();
      }

      // Cmd/Ctrl + Enter: Submit form
      if ((e.metaKey || e.ctrlKey) && e.key === 'Enter') {
        e.preventDefault();
        submitForm();
      }

      // Escape: Close modal
      if (e.key === 'Escape') {
        closeModal();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  return null;
};
```
