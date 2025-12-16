package ru.nedumayy.networkflow.state

/**
 * Состояния подключения к сети
 */
sealed interface NetworkState {
    data object Online : NetworkState
    data object Offline : NetworkState
    data object CaptivePortal : NetworkState
    data class Limited(val capabilities: android.net.NetworkCapabilities) : NetworkState
    data object Unknown : NetworkState
}