<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Card from 'primevue/card';
import Button from 'primevue/button';
import Skeleton from 'primevue/skeleton';
import Message from 'primevue/message';
import { useNewsStore } from '@/stores/news';
import { useErrorToast } from '@/composables/useErrorToast';

const PREVIEW_LIMIT = 100;

const newsStore = useNewsStore();
const router = useRouter();
const { show: showError } = useErrorToast();

const items = computed(() =>
  newsStore.list.map((event) => ({
    id: event.id,
    title: event.title,
    preview:
      event.message.length > PREVIEW_LIMIT
        ? event.message.slice(0, PREVIEW_LIMIT).trimEnd() + '…'
        : event.message,
  })),
);

onMounted(async () => {
  try {
    await newsStore.fetchAll();
  } catch (error) {
    showError(error);
  }
});

function openDetail(id: string) {
  router.push({ name: 'news-detail', params: { id } });
}

function goCreate() {
  router.push({ name: 'news-create' });
}
</script>

<template>
  <section class="news-list">
    <header class="news-list__header">
      <h1>Лента новостей</h1>
      <Button label="Написать новость" icon="pi pi-plus" severity="primary" @click="goCreate" />
    </header>

    <div v-if="newsStore.loadingList" class="news-list__skeletons">
      <Card v-for="n in 3" :key="n">
        <template #content>
          <div class="skeleton-stack">
            <Skeleton width="60%" height="1.25rem" />
            <Skeleton width="100%" height="1rem" />
            <Skeleton width="90%" height="1rem" />
          </div>
        </template>
      </Card>
    </div>

    <Message
      v-else-if="items.length === 0"
      severity="info"
      :closable="false"
    >
      Новостей пока нет. Станьте первым — нажмите «Написать новость».
    </Message>

    <div v-else class="news-list__items">
      <Card
        v-for="item in items"
        :key="item.id"
        class="news-card"
        @click="openDetail(item.id)"
      >
        <template #title>{{ item.title }}</template>
        <template #content>
          <p class="news-card__preview">{{ item.preview }}</p>
        </template>
        <template #footer>
          <Button label="Читать" icon="pi pi-arrow-right" text @click.stop="openDetail(item.id)" />
        </template>
      </Card>
    </div>
  </section>
</template>

<style scoped>
.news-list__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.news-list__header h1 {
  margin: 0;
  font-size: 1.5rem;
}

.news-list__skeletons,
.news-list__items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.news-card {
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
  /* защита от длинных токенов: не даём карточке растягиваться по содержимому */
  min-width: 0;
  overflow: hidden;
}

.news-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(15, 23, 42, 0.08);
}

/* PrimeVue рендерит заголовок как .p-card-title внутри карточки —
   форсируем перенос и там, иначе длинный title без пробелов вылезет. */
.news-card :deep(.p-card-title) {
  overflow-wrap: anywhere;
  word-break: break-word;
}

.news-card__preview {
  margin: 0;
  color: var(--p-surface-700);
  line-height: 1.5;
  white-space: pre-wrap;
  /* ломаем длинные строки без пробелов (например, 100 подряд латинских букв). */
  overflow-wrap: anywhere;
  word-break: break-word;
}

.skeleton-stack {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
</style>
