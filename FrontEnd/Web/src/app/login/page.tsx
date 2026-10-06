'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useSendOTP, useVerifyOTP } from '@/services/hooks/useAuth';
import { useAuthStore } from '@/stores/authStore';

export default function LoginPage() {
  const router = useRouter();
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const [phone, setPhone] = useState('');
  const [otp, setOtp] = useState('');
  const [step, setStep] = useState<'phone' | 'otp'>('phone');
  const [countdown, setCountdown] = useState(0);
  const [debugOTP, setDebugOTP] = useState<string | null>(null);

  const sendOTPMutation = useSendOTP();
  const verifyOTPMutation = useVerifyOTP();

  // Redirect if already logged in
  useEffect(() => {
    if (isAuthenticated) {
      router.push('/');
    }
  }, [isAuthenticated, router]);

  // Countdown timer
  useEffect(() => {
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [countdown]);

  const handleSendOTP = async () => {
    if (!phone || phone.length < 10) {
      alert('Please enter a valid phone number');
      return;
    }

    try {
      const result = await sendOTPMutation.mutateAsync(phone);
      if (result.success && result.data) {
        setStep('otp');
        setCountdown(result.data.resend_available_in || 60);
        if (result.data._debug_otp) {
          setDebugOTP(result.data._debug_otp);
        }
      }
    } catch (error) {
      console.error('Send OTP failed:', error);
    }
  };

  const handleVerifyOTP = async () => {
    if (!otp || otp.length < 6) {
      alert('Please enter the 6-digit code');
      return;
    }

    try {
      await verifyOTPMutation.mutateAsync({ phone, code: otp });
      router.push('/');
    } catch (error) {
      console.error('Verify OTP failed:', error);
    }
  };

  const handleResendOTP = () => {
    if (countdown === 0) {
      setOtp('');
      handleSendOTP();
    }
  };

  return (
    <div className="min-h-screen bg-[#0B0F19] flex flex-col items-center justify-center px-6">
      {/* Logo */}
      <div className="mb-12 text-center">
        <div className="w-20 h-20 bg-gradient-to-br from-[#5A6BFF] to-[#8B5CF6] rounded-3xl mx-auto mb-4 flex items-center justify-center">
          <span className="text-3xl">☕</span>
        </div>
        <h1 className="text-2xl font-bold text-white mb-2">AI Café</h1>
        <p className="text-gray-400 text-sm">Order with AI-powered recommendations</p>
      </div>

      {/* Debug OTP Banner */}
      {debugOTP && (
        <div className="w-full max-w-sm mb-4 p-3 bg-yellow-500/10 border border-yellow-500/30 rounded-xl">
          <p className="text-yellow-400 text-xs text-center">
            DEBUG OTP: <span className="font-mono font-bold">{debugOTP}</span>
          </p>
        </div>
      )}

      {/* Form */}
      <div className="w-full max-w-sm space-y-4">
        {step === 'phone' ? (
          <>
            <div>
              <label className="block text-gray-400 text-sm mb-2">Phone Number</label>
              <input
                type="tel"
                value={phone}
                onChange={(e) => setPhone(e.target.value.replace(/\D/g, ''))}
                placeholder="0912345678"
                className="w-full bg-[#1E293B] border border-[#334155] rounded-xl px-4 py-4 text-white placeholder-gray-500 focus:outline-none focus:border-[#5A6BFF] transition-colors"
              />
            </div>

            <button
              onClick={handleSendOTP}
              disabled={sendOTPMutation.isPending}
              className="w-full bg-[#5A6BFF] hover:bg-[#4855e8] disabled:opacity-50 text-white font-semibold py-4 rounded-xl transition-colors"
            >
              {sendOTPMutation.isPending ? 'Sending...' : 'Continue'}
            </button>
          </>
        ) : (
          <>
            <div className="text-center mb-4">
              <p className="text-gray-400 text-sm">Enter the code sent to</p>
              <p className="text-white font-semibold">{phone}</p>
            </div>

            <div>
              <label className="block text-gray-400 text-sm mb-2">Verification Code</label>
              <input
                type="text"
                value={otp}
                onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                placeholder="• • • • • •"
                className="w-full bg-[#1E293B] border border-[#334155] rounded-xl px-4 py-4 text-white text-center text-2xl tracking-widest placeholder-gray-600 focus:outline-none focus:border-[#5A6BFF] transition-colors"
                maxLength={6}
                autoFocus
              />
            </div>

            {verifyOTPMutation.isError && (
              <p className="text-red-400 text-sm text-center">
                Invalid code. Please try again.
              </p>
            )}

            <button
              onClick={handleVerifyOTP}
              disabled={verifyOTPMutation.isPending || otp.length < 6}
              className="w-full bg-[#5A6BFF] hover:bg-[#4855e8] disabled:opacity-50 text-white font-semibold py-4 rounded-xl transition-colors"
            >
              {verifyOTPMutation.isPending ? 'Verifying...' : 'Verify'}
            </button>

            <div className="text-center">
              <button
                onClick={handleResendOTP}
                disabled={countdown > 0}
                className="text-[#5A6BFF] hover:text-[#4855e8] disabled:text-gray-500 text-sm transition-colors"
              >
                {countdown > 0 ? `Resend in ${countdown}s` : 'Resend code'}
              </button>
            </div>

            <button
              onClick={() => {
                setStep('phone');
                setOtp('');
                setDebugOTP(null);
              }}
              className="w-full text-gray-400 hover:text-white text-sm py-2 transition-colors"
            >
              Change phone number
            </button>
          </>
        )}
      </div>

      {/* Footer */}
      <p className="mt-8 text-gray-500 text-xs text-center">
        By continuing, you agree to our Terms of Service and Privacy Policy
      </p>
    </div>
  );
}
