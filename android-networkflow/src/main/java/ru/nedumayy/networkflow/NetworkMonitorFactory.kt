package ru.nedumayy.networkflow


import androidx.lifecycle.LifecycleOwner

object NetworkMonitorFactory {
    fun create(lifecycleOwner: LifecycleOwner): NetworkMonitor {
        return NetworkMonitorImpl(lifecycleOwner)
    }
}