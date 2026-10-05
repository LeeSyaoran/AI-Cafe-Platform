// ============================================
// Auth Hooks - TanStack Query hooks cho authentication
// ============================================

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as authApi from '../api/auth';
import { queryKeys } from './queryClient';
import type { OTPRequest, OTPVerifyRequest, UserProfile } from '../../types';

// Get profile
export function useProfile() {
  return useQuery({
    queryKey: queryKeys.profile,
    queryFn: authApi.getProfile,
    enabled: authApi.isLoggedIn(),
    staleTime: 1000 * 60 * 5, // 5 phút
  });
}

// Update profile mutation
export function useUpdateProfile() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: Partial<UserProfile>) => authApi.updateProfile(data),
    onSuccess: (updated) => {
      queryClient.setQueryData(queryKeys.profile, updated);
    },
  });
}

// Send OTP mutation
export function useSendOTP() {
  return useMutation({
    mutationFn: (data: OTPRequest) => authApi.sendOTP(data),
  });
}

// Verify OTP mutation
export function useVerifyOTP() {
  return useMutation({
    mutationFn: (data: OTPVerifyRequest) => authApi.verifyOTP(data),
  });
}

// Logout mutation
export function useLogout() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => authApi.logout(),
    onSuccess: () => {
      // Clear all queries
      queryClient.clear();
    },
  });
}
