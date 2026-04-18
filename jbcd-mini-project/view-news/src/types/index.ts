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
  code: number;
  message: string;
}

export class BackendError extends Error {
  code: number;
  status: number;

  constructor(body: BackendErrorBody, status: number) {
    super(body.message);
    this.code = body.code;
    this.status = status;
    this.name = 'BackendError';
  }
}
