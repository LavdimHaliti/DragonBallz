package com.example.dragonballz.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Utility class to check network connectivity.
 */
class NetworkUtils(context: Context) {

    /**
     * Connectivity manager instance.
     */
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    /**
     * Flow that emits the current network connectivity state.
     */
    val isConnected: Flow<Boolean> = callbackFlow {
        // Create a network callback
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Check if the network is the active network
                if (network == connectivityManager.activeNetwork) {
                    trySend(isNetworkValidated(network))
                }
            }

            // Called when the network is lost`
            override fun onLost(network: Network) {
                if (network == connectivityManager.activeNetwork) {
                    trySend(false)
                }
            }

            // Called when the network capabilities change
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                // Check if the network has internet capabilities
                if (network == connectivityManager.activeNetwork) {
                    val hasInternet =
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    trySend(hasInternet)
                }
            }
        }
        // Register the network callback
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Send the current connectivity state
        trySend(getCurrentConnectivity())

        // Close the flow when the callback is unregistered
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    /**
     * Checks if the network has internet capabilities.
     * @return true if the network has internet capabilities, false otherwise.
     */
    private fun getCurrentConnectivity(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Checks if the network has internet capabilities and is validated.
     * @return true if the network has internet capabilities and is validated, false otherwise.
     */
    private fun isNetworkValidated(network: Network): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

}
