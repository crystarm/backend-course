<script setup lang="ts">
import { computed, onBeforeUnmount, ref } from 'vue';
import { useRouter } from 'vue-router';

import Card from 'primevue/card';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Textarea from 'primevue/textarea';
import Message from 'primevue/message';
import ProgressSpinner from 'primevue/progressspinner';

import { useDraftStore } from '@/stores/draft';
import { useCaptchaStore } from '@/stores/captcha';
import { useNewsStore } from '@/stores/news';
import { useErrorToast } from '@/composables/useErrorToast';
import { solveCaptcha } from '@/api/captcha';
import { BackendError } from '@/types';

const TITLE_MAX = 100;
const MESSAGE_MAX = 4000;

const draft = useDraftStore();
const captcha = useCaptchaStore();
const newsStore = useNewsStore();
const router = useRouter();
const toast = useErrorToast();

const captchaAnswer = ref('');
const submitting = ref(false);

// Валидация зеркалит бэкенд: title 1..100, message 1..4000, NotBlank.
const titleTrimmed = computed(() => draft.title.trim());
const messageTrimmed = computed(() => draft.message.trim());

const titleError = computed(() => {
  if (!titleTrimmed.value) return 'Заголовок не может быть пустым';
  if (titleTrimmed.value.length > TITLE_MAX) return `Не более ${TITLE_MAX} символов`;
  return null;
});

const messageError = computed(() => {
  if (!messageTrimmed.value) return 'Сообщение не может быть пустым';
  if (messageTrimmed.value.length > MESSAGE_MAX) return `Не более ${MESSAGE_MAX} символов`;
  return null;
});

const formValid = computed(() => titleError.value === null && messageError.value === null);

const captchaMinutes = computed(() => Math.floor(captcha.remainingSeconds / 60));
const captchaSecs = computed(() => (captcha.remainingSeconds % 60).toString().padStart(2, '0'));

const canSubmitCaptcha = computed(
  () => captcha.isActive && captchaAnswer.value.trim().length > 0 && !submitting.value,
);

async function requestCaptcha() {
  if (!formValid.value) {
    toast.warn('Проверьте поля формы', titleError.value ?? messageError.value ?? undefined);
    return;
  }
  try {
    captchaAnswer.value = '';
    await captcha.generate();
  } catch (error) {
    toast.show(error);
  }
}

async function regenerateCaptcha() {
  try {
    captchaAnswer.value = '';
    await captcha.generate();
  } catch (error) {
    toast.show(error);
  }
}

async function submit() {
  if (!captcha.id) {
    await requestCaptcha();
    return;
  }
  if (!formValid.value) {
    toast.warn('Проверьте поля формы');
    return;
  }

  submitting.value = true;
  const captchaId = captcha.id;
  const answer = captchaAnswer.value.trim();

  try {
    // 1) solve
    const solveResponse = await solveCaptcha(captchaId, answer);
    if (!solveResponse.solved) {
      toast.warn('Капча не пройдена', solveResponse.message || 'Введите символы ещё раз');
      // бэк уже удалил запись — получаем новую
      await regenerateCaptcha();
      return;
    }

    // 2) сразу создаём новость, пока 10-секундный SOLVED-TTL не истёк
    await newsStore.create(titleTrimmed.value, messageTrimmed.value, captchaId);

    // успех — чистим черновик и капчу, уходим на ленту
    draft.clear();
    captcha.clear();
    captchaAnswer.value = '';
    toast.success('Новость опубликована');
    router.push({ name: 'news-list' });
  } catch (error) {
    toast.show(error);
    // На captcha-ошибках регенерируем капчу, чтобы пользователь мог повторить.
    if (error instanceof BackendError) {
      if (error.code === 'captcha.not.found.or.expired' || error.code === 'captcha.not.solved') {
        await regenerateCaptcha();
      }
    }
  } finally {
    submitting.value = false;
  }
}

function cancelCaptcha() {
  captcha.clear();
  captchaAnswer.value = '';
}

onBeforeUnmount(() => {
  // Останавливаем таймер, чтобы не тикал после ухода со страницы.
  captcha.clear();
  captchaAnswer.value = '';
});
</script>

<template>
  <section class="create-news">
    <h1>Создать новость</h1>
    <p class="create-news__hint">
      Черновик сохраняется автоматически — перезагрузка страницы ничего не потеряет.
    </p>

    <Card class="create-news__card">
      <template #content>
        <div class="field">
          <label for="title">Заголовок</label>
          <InputText
            id="title"
            :model-value="draft.title"
            @update:model-value="draft.setTitle(($event as string | undefined) ?? '')"
            :maxlength="TITLE_MAX"
            :invalid="!!titleError"
            :disabled="captcha.isActive || submitting"
            placeholder="О чём новость?"
            fluid
          />
          <small class="field__meta">
            <span :class="{ 'field__error': titleError }">
              {{ titleError ?? `${titleTrimmed.length}/${TITLE_MAX}` }}
            </span>
          </small>
        </div>

        <div class="field">
          <label for="message">Текст</label>
          <Textarea
            id="message"
            :model-value="draft.message"
            @update:model-value="draft.setMessage(($event as string | undefined) ?? '')"
            :maxlength="MESSAGE_MAX"
            :invalid="!!messageError"
            :disabled="captcha.isActive || submitting"
            rows="8"
            auto-resize
            placeholder="Расскажите подробности…"
            fluid
          />
          <small class="field__meta">
            <span :class="{ 'field__error': messageError }">
              {{ messageError ?? `${messageTrimmed.length}/${MESSAGE_MAX}` }}
            </span>
          </small>
        </div>

        <!-- Капча появляется только после нажатия «хочу отправить» -->
        <div v-if="captcha.isActive" class="captcha-block">
          <Message severity="info" :closable="false">
            Решите капчу и нажмите «Отправить». Осталось
            <strong>{{ captchaMinutes }}:{{ captchaSecs }}</strong>.
          </Message>

          <div class="captcha-image">
            <img v-if="captcha.imageSrc" :src="captcha.imageSrc" alt="captcha" />
            <Button
              icon="pi pi-refresh"
              severity="secondary"
              aria-label="Сгенерировать новую капчу"
              text
              :disabled="submitting"
              @click="regenerateCaptcha"
            />
          </div>

          <InputText
            v-model="captchaAnswer"
            placeholder="Введите символы с картинки"
            autocomplete="off"
            :disabled="submitting"
            fluid
            @keyup.enter="canSubmitCaptcha && submit()"
          />
        </div>
      </template>

      <template #footer>
        <div class="actions">
          <Button
            v-if="!captcha.isActive"
            label="Хочу отправить"
            icon="pi pi-send"
            :disabled="!formValid || submitting"
            @click="requestCaptcha"
          />
          <template v-else>
            <Button
              label="Отмена"
              icon="pi pi-times"
              severity="secondary"
              text
              :disabled="submitting"
              @click="cancelCaptcha"
            />
            <Button
              label="Отправить"
              icon="pi pi-check"
              :disabled="!canSubmitCaptcha"
              @click="submit"
            />
          </template>
        </div>
      </template>
    </Card>

    <div v-if="submitting" class="overlay">
      <ProgressSpinner />
    </div>
  </section>
</template>

<style scoped>
.create-news h1 {
  margin: 0 0 0.5rem;
  font-size: 1.5rem;
}

.create-news__hint {
  margin: 0 0 1rem;
  color: var(--p-surface-600);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  margin-bottom: 1.25rem;
}

.field label {
  font-weight: 500;
}

.field__meta {
  color: var(--p-surface-500);
}

.field__error {
  color: var(--p-red-500, #dc2626);
}

.captcha-block {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding: 1rem;
  background: var(--p-surface-100);
  border-radius: var(--p-border-radius, 6px);
  margin-top: 0.5rem;
}

.captcha-image {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.captcha-image img {
  border-radius: 6px;
  background: #fff;
  padding: 4px;
  border: 1px solid var(--p-surface-300);
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.overlay {
  position: fixed;
  inset: 0;
  display: grid;
  place-items: center;
  background: rgba(15, 23, 42, 0.12);
  z-index: 1000;
}
</style>
