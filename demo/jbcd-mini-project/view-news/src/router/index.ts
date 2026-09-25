import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'news-list',
    component: () => import('@/views/NewsListView.vue'),
  },
  {
    path: '/news/:id',
    name: 'news-detail',
    component: () => import('@/views/NewsDetailView.vue'),
    props: true,
  },
  {
    path: '/create',
    name: 'news-create',
    component: () => import('@/views/NewsCreateView.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: { name: 'news-list' },
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});
