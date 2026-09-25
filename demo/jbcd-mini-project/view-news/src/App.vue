<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import Menubar from 'primevue/menubar';
import Toast from 'primevue/toast';

const router = useRouter();

const items = computed(() => [
  {
    label: 'Новости',
    icon: 'pi pi-list',
    command: () => router.push({ name: 'news-list' }),
  },
  {
    label: 'Создать новость',
    icon: 'pi pi-plus',
    command: () => router.push({ name: 'news-create' }),
  },
]);
</script>

<template>
  <div class="app-shell">
    <Menubar :model="items" class="app-menubar">
      <template #start>
        <div class="app-brand">
          <i class="pi pi-megaphone" />
          <span>jbcd</span>
        </div>
      </template>
    </Menubar>

    <main class="app-main">
      <router-view v-slot="{ Component }">
        <component :is="Component" />
      </router-view>
    </main>

    <Toast position="top-right" />
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-menubar {
  border-radius: 0;
  border-left: 0;
  border-right: 0;
  border-top: 0;
}

.app-brand {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  font-size: 1.1rem;
  padding: 0 0.75rem;
}

.app-main {
  flex: 1;
  width: 100%;
  max-width: 920px;
  margin: 0 auto;
  padding: 1.5rem 1rem;
  box-sizing: border-box;
}
</style>
