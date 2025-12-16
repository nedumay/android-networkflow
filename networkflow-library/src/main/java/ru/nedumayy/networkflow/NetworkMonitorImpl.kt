package ru.nedumayy.network_flow

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.nedumayy.network_flow.state.NetworkState

/**
 * Класс реализации мониторинга сети
 * @param context - Контекст приложения
 * @param lifecycle - Жизненный цикл
 */
class NetworkMonitorImpl(private val lifecycleOwner: LifecycleOwner) : NetworkMonitor {

    private val context = lifecycleOwner as Context
    private val lifecycle = lifecycleOwner.lifecycle
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Unknown)
    override val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            _networkState.tryEmit(mapCapabilitiesToState(capabilities))
        }

        override fun onLost(network: Network) {
            _networkState.tryEmit(NetworkState.Offline)
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            _networkState.tryEmit(NetworkState.Offline)
        }

        override fun onUnavailable() {
            _networkState.tryEmit(NetworkState.Offline)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            if(network == connectivityManager.activeNetwork){
                _networkState.tryEmit(mapCapabilitiesToState(networkCapabilities))
            }
        }
    }

    init {
        //Запоминаем текущее состояние сети и устанавливаем в параметр.
        val currentNetwork = connectivityManager.activeNetwork
        val currentCaps = currentNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        _networkState.value = mapCapabilitiesToState(currentCaps)

        //Регситрация callback
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        //Отписка при onDestroy
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    connectivityManager.unregisterNetworkCallback(networkCallback)
                }
            }
        })
    }

    private fun mapCapabilitiesToState(capabilities: NetworkCapabilities?): NetworkState {
        if (capabilities == null) return NetworkState.Offline

        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val validated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val captivePortal = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)

        return when {
            hasInternet && validated -> {
                // Если подключение к точке и интернет есть - Online
                NetworkState.Online
            }

            hasInternet && captivePortal -> {
                // Есть подключение, но пользователь должен открыть браузер, чтобы авторизоваться.
                NetworkState.CaptivePortal
            }

            hasInternet -> {
                // Есть только подключение к точке - Ограниченное подключение
                NetworkState.Limited(capabilities)
            }

            else -> {
                // Остальные случаи - нет подключения
                NetworkState.Offline
            }
        }
    }

}