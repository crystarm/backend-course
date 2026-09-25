import { createApp } from 'vue';
import { createPinia } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';

import PrimeVue from 'primevue/config';
import ToastService from 'primevue/toastservice';
import ConfirmationService from 'primevue/confirmationservice';

import App from './App.vue';
import { router } from './router';
import { GrayPreset } from './theme/preset';

import 'primeicons/primeicons.css';
import './assets/main.css';

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

const app = createApp(App);

app.use(pinia);
app.use(router);
app.use(PrimeVue, {
  theme: {
    preset: GrayPreset,
    options: {
      // фиксируем светлую тему — автопереключение на dark нам не нужно
      darkModeSelector: '.app-dark',
    },
  },
  ripple: true,
});
app.use(ToastService);
app.use(ConfirmationService);

app.mount('#app');
