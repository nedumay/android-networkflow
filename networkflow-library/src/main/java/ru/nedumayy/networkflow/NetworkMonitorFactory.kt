package ru.nedumayy.network_flow


import androidx.lifecycle.LifecycleOwner

object NetworkMonitorFactory {
    fun create(lifecycleOwner: LifecycleOwner): NetworkMonitor {
        return NetworkMonitorImpl(lifecycleOwner)
    }
}