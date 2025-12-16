# NetworkFlow

[![JitPack](https://jitpack.io/v/yourname/networkflow.svg)](https://jitpack.io/#yourname/networkflow)

Простая, безопасная и lifecycle-aware библиотека для отслеживания состояния сети в Android-приложениях.  
Определяет не просто подключение, а **реальное наличие интернета**, включая кейсы вроде **captive portal** (авторизация в отелях, аэропортах и т.д.).

## Особенности

- Современный API: `ConnectivityManager.NetworkCallback` (без устаревших `BroadcastReceiver`)
- Lifecycle-aware: автоматическая отписка при уничтожении Activity/Fragment
- Реактивность: `StateFlow<NetworkState>` для удобного использования в Compose и ViewModel
- Точные состояния:
  - `Online` — интернет есть и проверен ОС
  - `Offline` — нет сети или нет интернета
  - `CaptivePortal` — требуется авторизация в браузере
  - `Limited` — подключение есть, но интернет не подтверждён
  - `Unknown` — начальное состояние

## Установка

### 1. Добавьте репозиторий JitPack в `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // ← эта строка
    }
}
```
