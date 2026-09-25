import { apiRequest } from './client';
import type { CaptchaResponse, SolveRequest, SolveResponse } from '@/types';

export function generateCaptcha(): Promise<CaptchaResponse> {
  return apiRequest<CaptchaResponse>('/captcha/generate', { method: 'POST' });
}

export function solveCaptcha(id: string, answer: string): Promise<SolveResponse> {
  const body: SolveRequest = { answer };
  return apiRequest<SolveResponse>(`/captcha/${encodeURIComponent(id)}/solve`, {
    method: 'POST',
    body,
  });
}
