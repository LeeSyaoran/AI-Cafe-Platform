import { api } from '../client';
import type { ApiResponse } from '@/types';
import type {
  SendOTPRequest,
  SendOTPResponse,
  VerifyOTPRequest,
  VerifyOTPResponse,
  User,
} from '@/types';

// Send OTP
export async function sendOTP(
  phone: string
): Promise<ApiResponse<SendOTPResponse>> {
  const response = await api.post<ApiResponse<SendOTPResponse>>(
    '/v1/auth/send-otp',
    { phone } as SendOTPRequest
  );
  return response;
}

// Verify OTP
export async function verifyOTP(
  phone: string,
  code: string
): Promise<ApiResponse<VerifyOTPResponse>> {
  const response = await api.post<ApiResponse<VerifyOTPResponse>>(
    '/v1/auth/verify-otp',
    { phone, code } as VerifyOTPRequest
  );
  return response;
}

// Get current user
export async function getCurrentUser(): Promise<ApiResponse<User>> {
  const response = await api.get<ApiResponse<User>>('/v1/me', true);
  return response;
}

// Refresh token
export async function refreshToken(
  refreshToken: string
): Promise<ApiResponse<{ access_token: string }>> {
  const response = await api.post<ApiResponse<{ access_token: string }>>(
    '/v1/auth/refresh',
    { refresh_token: refreshToken }
  );
  return response;
}

// Update profile
export async function updateProfile(
  data: Partial<User>
): Promise<ApiResponse<User>> {
  const response = await api.put<ApiResponse<User>>('/v1/me', data, true);
  return response;
}
