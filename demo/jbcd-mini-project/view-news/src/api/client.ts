import { BackendError, type BackendErrorBody } from '@/types';

const API_BASE = '/api';

interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
  body?: unknown;
  signal?: AbortSignal;
}

/**
 * Тонкая обёртка над fetch:
 *  - same-origin путь /api/... (gateway сам маршрутизирует);
 *  - при не-2xx пытается распарсить ErrorResponse {code, message} и бросает BackendError;
 *  - при 204 возвращает undefined.
 */
export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', body, signal } = options;

  const response = await fetch(`${API_BASE}${path}`, {
    method,
    signal,
    headers: body !== undefined
      ? { 'Content-Type': 'application/json', Accept: 'application/json' }
      : { Accept: 'application/json' },
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
    let errorBody: BackendErrorBody | null = null;
    try {
      errorBody = (await response.json()) as BackendErrorBody;
    } catch {
      // пустое / невалидное тело — пойдёт fallback ниже
    }
    throw new BackendError(normalizeErrorBody(errorBody, response), response.status);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

function normalizeErrorBody(errorBody: BackendErrorBody | null, response: Response): BackendErrorBody {
  if (errorBody && typeof errorBody.code === 'string') {
    return {
      code: errorBody.code,
      message: typeof errorBody.message === 'string' ? errorBody.message : null,
    };
  }

  return {
    code: `http.${response.status}`,
    message: response.statusText || 'Request failed',
  };
}
