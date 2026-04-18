import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { generateCaptcha } from '@/api/captcha';

const CAPTCHA_TTL_MS = 5 * 60 * 1000; // 5 минут — согласно api-captcha captcha.ttl.created

/**
 * Состояние текущей капчи. Не персистится: при перезагрузке пользователь
 * должен снова нажать «хочу отправить», чтобы получить свежую капчу.
 */
export const useCaptchaStore = defineStore('captcha', () => {
  const id = ref<string | null>(null);
  const imageBase64 = ref<string | null>(null);
  const expiresAt = ref<number | null>(null);
  const now = ref<number>(Date.now());
  const loading = ref(false);

  let tickerId: number | null = null;

  function startTicker() {
    stopTicker();
    tickerId = window.setInterval(() => {
      now.value = Date.now();
      if (expiresAt.value !== null && now.value >= expiresAt.value) {
        clear();
      }
    }, 1000);
  }

  function stopTicker() {
    if (tickerId !== null) {
      clearInterval(tickerId);
      tickerId = null;
    }
  }

  const remainingSeconds = computed(() => {
    if (expiresAt.value === null) return 0;
    return Math.max(0, Math.ceil((expiresAt.value - now.value) / 1000));
  });

  const isActive = computed(() => id.value !== null && remainingSeconds.value > 0);

  const imageSrc = computed(() =>
    imageBase64.value ? `data:image/png;base64,${imageBase64.value}` : null,
  );

  async function generate() {
    loading.value = true;
    try {
      const response = await generateCaptcha();
      id.value = response.id;
      imageBase64.value = response.imageBase64;
      now.value = Date.now();
      expiresAt.value = now.value + CAPTCHA_TTL_MS;
      startTicker();
    } finally {
      loading.value = false;
    }
  }

  function clear() {
    id.value = null;
    imageBase64.value = null;
    expiresAt.value = null;
    stopTicker();
  }

  return {
    id,
    imageBase64,
    imageSrc,
    remainingSeconds,
    isActive,
    loading,
    generate,
    clear,
  };
});
