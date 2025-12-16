package ru.nedumayy.networkflow

import kotlinx.coroutines.flow.StateFlow
import ru.nedumayy.networkflow.state.NetworkState

interface NetworkMonitor {
    val networkState: StateFlow<NetworkState>
}