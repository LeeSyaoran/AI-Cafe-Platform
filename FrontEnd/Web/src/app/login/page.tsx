'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useSendOTP, useVerifyOTP, useLogout } from '@/services/hooks/useAuth';
import { useAuthStore } from '@/stores/authStore';
import { getRedirectPath, type UserRole } from '@/types/auth';

type LoginMethod = 'phone' | 'email' | 'demo';
type AuthStep = LoginMethod | 'otp' | 'password';

export default function LoginPage() {
  const router = useRouter();
  const { user, isAuthenticated, setAuth } = useAuthStore();
  const logout = useLogout();

  // Form states
  const [loginMethod, setLoginMethod] = useState<LoginMethod>('phone');
  const [authStep, setAuthStep] = useState<AuthStep>('phone');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [otp, setOtp] = useState('');
  const [countdown, setCountdown] = useState(0);
  const [debugOTP, setDebugOTP] = useState<string | null>(null);
  const [error, setError] = useState('');

  // Demo accounts
  const demoAccounts = [
    { role: 'customer' as const, email: 'customer@aicafe.vn', password: 'demo123', label: 'Khách hàng' },
    { role: 'staff' as const, email: 'staff@aicafe.vn', password: 'demo123', label: 'Nhân viên' },
    { role: 'manager' as const, email: 'manager@aicafe.vn', password: 'demo123', label: 'Quản lý' },
    { role: 'admin' as const, email: 'admin@aicafe.vn', password: 'demo123', label: 'Quản trị viên' },
  ];

  const sendOTPMutation = useSendOTP();
  const verifyOTPMutation = useVerifyOTP();

  // Redirect if already logged in
  useEffect(() => {
    if (isAuthenticated && user) {
      const redirectPath = getRedirectPath(user.role as UserRole);
      router.push(redirectPath);
    }
  }, [isAuthenticated, user, router]);

  // Countdown timer
  useEffect(() => {
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [countdown]);

  // Handle send OTP
  const handleSendOTP = async () => {
    if (!phone || phone.length < 10) {
      setError('Vui lòng nhập số điện thoại hợp lệ');
      return;
    }
    setError('');

    try {
      const result = await sendOTPMutation.mutateAsync(phone);
      if (result.success && result.data) {
        setAuthStep('otp');
        setCountdown(result.data.resend_available_in || 60);
        if (result.data._debug_otp) {
          setDebugOTP(result.data._debug_otp);
        }
      } else {
        setError(result.error?.message || 'Gửi OTP thất bại');
      }
    } catch (err: any) {
      setError(err?.message || 'Gửi OTP thất bại');
    }
  };

  // Handle verify OTP
  const handleVerifyOTP = async () => {
    if (!otp || otp.length < 6) {
      setError('Vui lòng nhập mã xác thực 6 số');
      return;
    }
    setError('');

    try {
      const result = await verifyOTPMutation.mutateAsync({ phone, code: otp });
      if (result.success && result.data) {
        const { tokens, user: userData } = result.data;
        setAuth(userData, tokens.access_token, tokens.refresh_token);
        const redirectPath = getRedirectPath(userData.role as UserRole);
        router.push(redirectPath);
      } else {
        setError(result.error?.message || 'Mã xác thực không đúng');
      }
    } catch (err: any) {
      setError(err?.message || 'Xác thực thất bại');
    }
  };

  // Handle email/password login
  const handleEmailLogin = async () => {
    if (!email || !password) {
      setError('Vui lòng nhập email và mật khẩu');
      return;
    }
    setError('');

    // Find demo account
    const demoAccount = demoAccounts.find(
      (acc) => acc.email === email && acc.password === password
    );

    if (demoAccount) {
      // Simulate login with demo account
      const demoUser = {
        id: 'demo-' + demoAccount.role,
        phone: '+84912345678',
        email: demoAccount.email,
        name: demoAccount.label,
        role: demoAccount.role,
        tier: 'gold' as const,
      };
      const demoTokens = {
        access_token: 'demo-access-token',
        refresh_token: 'demo-refresh-token',
        expires_in: 3600,
        token_type: 'Bearer',
      };
      setAuth(demoUser, demoTokens.access_token, demoTokens.refresh_token);
      const redirectPath = getRedirectPath(demoAccount.role);
      router.push(redirectPath);
      return;
    }

    // Try API login
    const apiUrl = (typeof process !== 'undefined' && process.env?.NEXT_PUBLIC_API_URL) || 'http://localhost:3000';
    try {
      const response = await fetch(`${apiUrl}/v1/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });
      const data = await response.json();
      if (data.success && data.data) {
        const { tokens, user: userData } = data.data;
        setAuth(userData, tokens.access_token, tokens.refresh_token);
        const redirectPath = getRedirectPath(userData.role as UserRole);
        router.push(redirectPath);
      } else {
        setError(data.error?.message || 'Đăng nhập thất bại');
      }
    } catch {
      setError('Đăng nhập thất bại. Vui lòng thử lại.');
    }
  };

  // Handle demo login
  const handleDemoLogin = async (role: UserRole) => {
    const account = demoAccounts.find((acc) => acc.role === role);
    if (account) {
      const demoUser = {
        id: 'demo-' + role,
        phone: '+84912345678',
        email: account.email,
        name: account.label,
        role: role,
        tier: 'gold' as const,
      };
      const demoTokens = {
        access_token: 'demo-access-token-' + role,
        refresh_token: 'demo-refresh-token-' + role,
        expires_in: 3600,
        token_type: 'Bearer',
      };
      setAuth(demoUser, demoTokens.access_token, demoTokens.refresh_token);
      const redirectPath = getRedirectPath(role);
      router.push(redirectPath);
    }
  };

  // Reset to phone step
  const resetToMethod = () => {
    setAuthStep(loginMethod);
    setOtp('');
    setPassword('');
    setError('');
    setDebugOTP(null);
  };

  return (
    <div className="min-h-screen bg-[#0B0F19] flex flex-col items-center justify-center px-6">
      {/* Logo */}
      <div className="mb-8 text-center">
        <div className="w-20 h-20 bg-gradient-to-br from-[#5A6BFF] to-[#8B5CF6] rounded-3xl mx-auto mb-4 flex items-center justify-center shadow-lg shadow-purple-500/20">
          <span className="text-3xl">☕</span>
        </div>
        <h1 className="text-3xl font-bold text-white mb-2">AI Café</h1>
        <p className="text-gray-400 text-sm">Đăng nhập để trải nghiệm</p>
      </div>

      {/* Login Method Tabs */}
      <div className="w-full max-w-sm mb-6">
        <div className="flex bg-[#1E293B] rounded-xl p-1">
          <button
            onClick={() => { setLoginMethod('phone'); setAuthStep('phone'); setError(''); }}
            className={`flex-1 py-2.5 rounded-lg text-sm font-medium transition-all ${
              loginMethod === 'phone'
                ? 'bg-[#5A6BFF] text-white shadow-lg'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            📱 SĐT
          </button>
          <button
            onClick={() => { setLoginMethod('email'); setAuthStep('password'); setError(''); }}
            className={`flex-1 py-2.5 rounded-lg text-sm font-medium transition-all ${
              loginMethod === 'email'
                ? 'bg-[#5A6BFF] text-white shadow-lg'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            ✉️ Email
          </button>
          <button
            onClick={() => { setLoginMethod('demo'); setAuthStep('demo'); setError(''); }}
            className={`flex-1 py-2.5 rounded-lg text-sm font-medium transition-all ${
              loginMethod === 'demo'
                ? 'bg-[#5A6BFF] text-white shadow-lg'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            🎮 Demo
          </button>
        </div>
      </div>

      {/* Debug OTP Banner */}
      {debugOTP && (
        <div className="w-full max-w-sm mb-4 p-3 bg-amber-500/10 border border-amber-500/30 rounded-xl">
          <p className="text-amber-400 text-xs text-center font-mono">
            DEBUG OTP: <span className="font-bold">{debugOTP}</span>
          </p>
        </div>
      )}

      {/* Error Banner */}
      {error && (
        <div className="w-full max-w-sm mb-4 p-3 bg-red-500/10 border border-red-500/30 rounded-xl">
          <p className="text-red-400 text-sm text-center">{error}</p>
        </div>
      )}

      {/* Forms */}
      <div className="w-full max-w-sm space-y-4">
        {/* Phone OTP Login */}
        {loginMethod === 'phone' && authStep === 'phone' && (
          <>
            <div>
              <label className="block text-gray-400 text-sm mb-2">Số điện thoại</label>
              <div className="flex">
                <span className="inline-flex items-center px-4 bg-[#1E293B] border border-r-0 border-[#334155] rounded-l-xl text-gray-400">
                  +84
                </span>
                <input
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value.replace(/\D/g, '').slice(0, 10))}
                  placeholder="912345678"
                  className="flex-1 bg-[#1E293B] border border-[#334155] rounded-r-xl px-4 py-4 text-white placeholder-gray-500 focus:outline-none focus:border-[#5A6BFF] transition-colors"
                />
              </div>
            </div>

            <button
              onClick={handleSendOTP}
              disabled={sendOTPMutation.isPending}
              className="w-full bg-[#5A6BFF] hover:bg-[#4855e8] disabled:opacity-50 text-white font-semibold py-4 rounded-xl transition-all transform hover:scale-[1.02] active:scale-[0.98]"
            >
              {sendOTPMutation.isPending ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  Đang gửi...
                </span>
              ) : 'Tiếp tục'}
            </button>
          </>
        )}

        {/* OTP Verification */}
        {loginMethod === 'phone' && authStep === 'otp' && (
          <>
            <div className="text-center mb-4">
              <p className="text-gray-400 text-sm mb-1">Nhập mã xác thực gửi đến</p>
              <p className="text-white font-semibold">+84 {phone}</p>
            </div>

            <div>
              <label className="block text-gray-400 text-sm mb-2">Mã xác thực</label>
              <input
                type="text"
                value={otp}
                onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                placeholder="• • • • • •"
                className="w-full bg-[#1E293B] border border-[#334155] rounded-xl px-4 py-4 text-white text-center text-2xl tracking-[0.5em] placeholder-gray-600 focus:outline-none focus:border-[#5A6BFF] transition-colors"
                maxLength={6}
                autoFocus
              />
            </div>

            <button
              onClick={handleVerifyOTP}
              disabled={verifyOTPMutation.isPending || otp.length < 6}
              className="w-full bg-[#5A6BFF] hover:bg-[#4855e8] disabled:opacity-50 text-white font-semibold py-4 rounded-xl transition-all transform hover:scale-[1.02] active:scale-[0.98]"
            >
              {verifyOTPMutation.isPending ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  Đang xác thực...
                </span>
              ) : 'Xác thực'}
            </button>

            <div className="text-center">
              <button
                onClick={handleSendOTP}
                disabled={countdown > 0}
                className="text-[#5A6BFF] hover:text-[#4855e8] disabled:text-gray-500 text-sm transition-colors"
              >
                {countdown > 0 ? `Gửi lại sau ${countdown}s` : 'Gửi lại mã'}
              </button>
            </div>

            <button
              onClick={resetToMethod}
              className="w-full text-gray-400 hover:text-white text-sm py-2 transition-colors"
            >
              ← Thay đổi phương thức đăng nhập
            </button>
          </>
        )}

        {/* Email/Password Login */}
        {loginMethod === 'email' && authStep === 'password' && (
          <>
            <div>
              <label className="block text-gray-400 text-sm mb-2">Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="email@example.com"
                className="w-full bg-[#1E293B] border border-[#334155] rounded-xl px-4 py-4 text-white placeholder-gray-500 focus:outline-none focus:border-[#5A6BFF] transition-colors"
              />
            </div>

            <div>
              <label className="block text-gray-400 text-sm mb-2">Mật khẩu</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full bg-[#1E293B] border border-[#334155] rounded-xl px-4 py-4 text-white placeholder-gray-500 focus:outline-none focus:border-[#5A6BFF] transition-colors"
              />
            </div>

            <button
              onClick={handleEmailLogin}
              className="w-full bg-[#5A6BFF] hover:bg-[#4855e8] text-white font-semibold py-4 rounded-xl transition-all transform hover:scale-[1.02] active:scale-[0.98]"
            >
              Đăng nhập
            </button>

            <div className="text-center">
              <button className="text-[#5A6BFF] hover:text-[#4855e8] text-sm transition-colors">
                Quên mật khẩu?
              </button>
            </div>
          </>
        )}

        {/* Demo Login */}
        {loginMethod === 'demo' && (
          <>
            <p className="text-gray-400 text-sm text-center mb-4">
              Đăng nhập nhanh với tài khoản demo
            </p>

            <div className="space-y-3">
              {demoAccounts.map((account) => (
                <button
                  key={account.role}
                  onClick={() => handleDemoLogin(account.role)}
                  className="w-full flex items-center justify-between bg-[#1E293B] hover:bg-[#2D3A4F] border border-[#334155] hover:border-[#5A6BFF] rounded-xl px-4 py-3 transition-all group"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-gradient-to-br from-[#5A6BFF] to-[#8B5CF6] rounded-xl flex items-center justify-center">
                      {account.role === 'admin' && '👑'}
                      {account.role === 'manager' && '💼'}
                      {account.role === 'staff' && '👨‍💼'}
                      {account.role === 'customer' && '👤'}
                    </div>
                    <div className="text-left">
                      <p className="text-white font-medium">{account.label}</p>
                      <p className="text-gray-500 text-xs">{account.email}</p>
                    </div>
                  </div>
                  <span className="text-[#5A6BFF] group-hover:translate-x-1 transition-transform">→</span>
                </button>
              ))}
            </div>
          </>
        )}
      </div>

      {/* Footer */}
      <div className="mt-8 text-center">
        <p className="text-gray-500 text-xs">
          Bằng việc tiếp tục, bạn đồng ý với{' '}
          <span className="text-[#5A6BFF] cursor-pointer hover:underline">Điều khoản dịch vụ</span>
          {' '}và{' '}
          <span className="text-[#5A6BFF] cursor-pointer hover:underline">Chính sách bảo mật</span>
        </p>
      </div>

      {/* Decorative Elements */}
      <div className="fixed top-0 left-0 w-full h-full overflow-hidden pointer-events-none -z-10">
        <div className="absolute top-20 left-10 w-72 h-72 bg-[#5A6BFF]/10 rounded-full blur-3xl" />
        <div className="absolute bottom-20 right-10 w-96 h-96 bg-[#8B5CF6]/10 rounded-full blur-3xl" />
      </div>
    </div>
  );
}
