package ru.nedumayy.networkflow

import kotlinx.coroutines.flow.StateFlow
import ru.nedumayy.networkflow.state.NetworkState

/**
 * Интерфейс для реализация состояния сети
 */
interface NetworkMonitor {
    val networkState: StateFlow<NetworkState>
}