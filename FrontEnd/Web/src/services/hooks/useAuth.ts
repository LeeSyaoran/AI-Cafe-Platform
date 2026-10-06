'use client';

import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { sendOTP, verifyOTP, getCurrentUser } from '@/services/api/endpoints/auth';
import { useAuthStore } from '@/stores/authStore';

export function useSendOTP() {
  const [debugOTP, setDebugOTP] = useState<string | null>(null);

  return useMutation({
    mutationFn: (phone: string) => sendOTP(phone),
    onSuccess: (data) => {
      if (data.data?._debug_otp) {
        setDebugOTP(data.data._debug_otp);
        console.log('[DEBUG] OTP:', data.data._debug_otp);
      }
    },
  });
}

export function useVerifyOTP() {
  const setAuth = useAuthStore((state) => state.setAuth);

  return useMutation({
    mutationFn: ({ phone, code }: { phone: string; code: string }) =>
      verifyOTP(phone, code),
    onSuccess: async (data) => {
      if (data.data) {
        const { tokens, user } = data.data;
        setAuth(user, tokens.access_token, tokens.refresh_token);
      }
    },
  });
}

export function useLogout() {
  const logout = useAuthStore((state) => state.logout);

  return () => {
    logout();
  };
}
