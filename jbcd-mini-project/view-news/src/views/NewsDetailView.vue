<script setup lang="ts">
import { onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import Card from 'primevue/card';
import Button from 'primevue/button';
import Skeleton from 'primevue/skeleton';
import { useNewsStore } from '@/stores/news';
import { useErrorToast } from '@/composables/useErrorToast';

const props = defineProps<{ id: string }>();

const newsStore = useNewsStore();
const router = useRouter();
const { show: showError } = useErrorToast();

async function load(id: string) {
  try {
    await newsStore.fetchById(id);
  } catch (error) {
    showError(error);
    router.replace({ name: 'news-list' });
  }
}

onMounted(() => load(props.id));
watch(
  () => props.id,
  (next) => load(next),
);

function goBack() {
  router.push({ name: 'news-list' });
}
</script>

<template>
  <section class="news-detail">
    <Button
      label="К ленте"
      icon="pi pi-arrow-left"
      text
      class="news-detail__back"
      @click="goBack"
    />

    <Card v-if="newsStore.loadingDetail">
      <template #content>
        <div class="skeleton-stack">
          <Skeleton width="40%" height="1.5rem" />
          <Skeleton width="100%" height="1rem" />
          <Skeleton width="95%" height="1rem" />
          <Skeleton width="85%" height="1rem" />
        </div>
      </template>
    </Card>

    <Card v-else-if="newsStore.current">
      <template #title>{{ newsStore.current.title }}</template>
      <template #content>
        <p class="news-detail__body">{{ newsStore.current.message }}</p>
      </template>
    </Card>
  </section>
</template>

<style scoped>
.news-detail__back {
  margin-bottom: 1rem;
}

.news-detail__body {
  margin: 0;
  color: var(--p-surface-800);
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.skeleton-stack {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
</style>
