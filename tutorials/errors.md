# Возможные ошибки и их решения

## Запуск сборки

```text
Fatal error compiling: error: invalid target release
```
Решение: вы пытаетесь использовать старый JDK.
Перейдите: File -> Project Structure -> Project -> SDK -> любой JDK 21+

```text
Could not find a valid Docker environment. Please see logs and check configuration
```
Решение: вы не запустили docker. Запустите docker демон на компьютере (просто откройте docker desktop).

## Запуск программы

```text
[main] ERROR com.karandashov.jbcd.api.captcha.Main - Не заданы переменные окружения!
```
Решение: вы не подложили local.env для внешнего сервиса.
Перейдите Run/Debug Configurations -> Edit Configurations -> Modify options -> Environment Variables -> + -> Path -> /path/to/local.env

## Просто все красное и не работает

1) Проверь версию JDK: File -> Project Structure -> Project -> SDK -> должен стоять любой JDK 21+
2) Проверь maven: Maven Settings -> maven home path -> установи значение Bundled
3) Нажми Reload All Maven Projects
4) File -> invalidate caches -> выбрать все галочки -> invalidate and restart

![universalErrorsSteps.png](resources/universalErrorsSteps.png)