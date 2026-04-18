import { useToast } from 'primevue/usetoast';
import { BackendError } from '@/types';

/**
 * Унифицированный показ ошибки всплывающим сообщением.
 * Для BackendError формат: «Ошибка {code}: {message}».
 */
export function useErrorToast() {
  const toast = useToast();

  function show(error: unknown, fallbackSummary = 'Ошибка'): void {
    if (error instanceof BackendError) {
      toast.add({
        severity: 'error',
        summary: `Ошибка ${error.code}`,
        detail: error.message,
        life: 5000,
      });
      return;
    }

    const detail = error instanceof Error ? error.message : String(error);
    toast.add({
      severity: 'error',
      summary: fallbackSummary,
      detail,
      life: 5000,
    });
  }

  function info(summary: string, detail?: string): void {
    toast.add({ severity: 'info', summary, detail, life: 3000 });
  }

  function success(summary: string, detail?: string): void {
    toast.add({ severity: 'success', summary, detail, life: 3000 });
  }

  function warn(summary: string, detail?: string): void {
    toast.add({ severity: 'warn', summary, detail, life: 4000 });
  }

  return { show, info, success, warn };
}
