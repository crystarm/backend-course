import { apiRequest } from './client';
import type { CreateNewsEventRequest, NewsEvent, NewsEventList } from '@/types';

export function getAllNews(): Promise<NewsEventList> {
  return apiRequest<NewsEventList>('/news');
}

export function getNewsById(id: string): Promise<NewsEvent> {
  return apiRequest<NewsEvent>(`/news/${encodeURIComponent(id)}`);
}

export function createNewsEvent(request: CreateNewsEventRequest): Promise<NewsEvent> {
  return apiRequest<NewsEvent>('/news', {
    method: 'POST',
    body: request,
  });
}
