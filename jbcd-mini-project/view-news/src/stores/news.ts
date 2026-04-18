import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { NewsEvent } from '@/types';
import { createNewsEvent, getAllNews, getNewsById } from '@/api/news';

/**
 * Простой стор ленты новостей. Без кэша и пагинации — каждый заход
 * перечитывает список из бэка. Детали грузятся отдельно по id.
 */
export const useNewsStore = defineStore('news', () => {
  const list = ref<NewsEvent[]>([]);
  const current = ref<NewsEvent | null>(null);
  const loadingList = ref(false);
  const loadingDetail = ref(false);

  async function fetchAll() {
    loadingList.value = true;
    try {
      const response = await getAllNews();
      list.value = response.events;
    } finally {
      loadingList.value = false;
    }
  }

  async function fetchById(id: string) {
    loadingDetail.value = true;
    current.value = null;
    try {
      current.value = await getNewsById(id);
    } finally {
      loadingDetail.value = false;
    }
  }

  async function create(title: string, message: string, solvedCaptchaId: string) {
    return createNewsEvent({ title, message, solvedCaptchaId });
  }

  return {
    list,
    current,
    loadingList,
    loadingDetail,
    fetchAll,
    fetchById,
    create,
  };
});
