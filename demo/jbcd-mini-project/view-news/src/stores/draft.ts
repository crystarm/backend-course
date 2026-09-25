import { defineStore } from 'pinia';
import { ref } from 'vue';

/**
 * Черновик новости, сохраняется в localStorage через pinia-plugin-persistedstate.
 * Не теряется при перезагрузке страницы; очищается только после успешного создания новости.
 */
export const useDraftStore = defineStore(
  'draft',
  () => {
    const title = ref('');
    const message = ref('');

    function setTitle(value: string) {
      title.value = value;
    }

    function setMessage(value: string) {
      message.value = value;
    }

    function clear() {
      title.value = '';
      message.value = '';
    }

    return { title, message, setTitle, setMessage, clear };
  },
  {
    persist: true,
  },
);
