# NetworkFlow

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

### 2. Добавьте зависимость в app/build.gradle.kts
```kotlin
dependencies {
    implementation("com.github.yourname:networkflow:v1.0.0")
}
```

### Использование в Activity / Fragment (View system):
```kotlin
val monitor = NetworkMonitorFactory.create(this) // this = Activity или Fragment

lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        monitor.networkState.collect { state ->
            when (state) {
                NetworkState.Online -> showContent()
                NetworkState.Offline -> showOfflineMessage()
                NetworkState.CaptivePortal -> showCaptivePortalWarning()
                is NetworkState.Limited -> showLimitedConnectionWarning()
                NetworkState.Unknown -> showLoadingState()
            }
        }
    }
}
```

### Использование в Jetpack Compose:
```kotlin
@Composable
fun MyScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val monitor = remember(lifecycleOwner) {
        NetworkMonitorFactory.create(lifecycleOwner)
    }
    val networkState by monitor.networkState.collectAsState()

    Scaffold { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (networkState) {
                NetworkState.Online -> Text("Всё работает!")
                NetworkState.Offline -> Text("Нет интернета")
                NetworkState.CaptivePortal -> Text("Требуется авторизация в Wi-Fi")
                is NetworkState.Limited -> Text("Ограниченное подключение")
                NetworkState.Unknown -> CircularProgressIndicator()
            }
        }
    }
}
```
