// DTO 1:1 с backend (см. ErrorResponse.java и dto/*)

export interface NewsEvent {
  id: string;
  title: string;
  message: string;
}

export interface NewsEventList {
  events: NewsEvent[];
}

export interface CreateNewsEventRequest {
  title: string;
  message: string;
  solvedCaptchaId: string;
}

export interface CaptchaResponse {
  id: string;
  imageBase64: string;
}

export interface SolveRequest {
  answer: string;
}

export interface SolveResponse {
  solved: boolean;
  message: string;
}

export interface BackendErrorBody {
  code: string;
  message?: string | null;
}

export class BackendError extends Error {
  code: string;
  status: number;
  backendMessage: string | null;

  constructor(body: BackendErrorBody, status: number) {
    const message = body.message ?? backendErrorMessageByCode(body.code);
    super(message);
    this.code = body.code;
    this.status = status;
    this.backendMessage = body.message ?? null;
    this.name = 'BackendError';
  }
}

export function backendErrorMessageByCode(code: string): string {
  const messages: Record<string, string> = {
    'bad.request': 'Некорректный запрос',
    'internal.server.error': 'Внутренняя ошибка сервера',
    'news.not.found': 'Новость не найдена',
    'captcha.not.found.or.expired': 'Капча не найдена или истекла',
    'captcha.not.solved': 'Капча не пройдена',
    'captcha.check.failed': 'Не удалось проверить капчу',
  };
  return messages[code] ?? 'Запрос завершился ошибкой';
}
