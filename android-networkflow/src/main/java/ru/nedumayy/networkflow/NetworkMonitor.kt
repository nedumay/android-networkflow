package ru.nedumayy.network_flow

import kotlinx.coroutines.flow.StateFlow
import ru.nedumayy.network_flow.state.NetworkState

interface NetworkMonitor {
    val networkState: StateFlow<NetworkState>
}