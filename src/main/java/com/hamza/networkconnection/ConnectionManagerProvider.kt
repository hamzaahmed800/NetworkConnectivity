package com.hamza.networkconnection

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class ConnectionManagerProvider(private val conMan: ConnectivityManager) :
    ConnectionManagerBase() {

    private val networkCallback = ConnectivityCallback()

    override fun subscribe() {
        conMan.registerDefaultNetworkCallback(networkCallback)
    }

    override fun unsubscribe() {
        conMan.unregisterNetworkCallback(networkCallback)
    }

    override fun getNetworkState(): ConnectionManager.NetworkState {
        val capabilities = conMan.getNetworkCapabilities(conMan.activeNetwork)
        return if (capabilities != null) {
            ConnectionManager.NetworkState.InternetStatus.Connected(capabilities)
        } else {
            ConnectionManager.NetworkState.NotConnectedStatus
        }
    }

    private inner class ConnectivityCallback : ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            notifyChange(ConnectionManager.NetworkState.InternetStatus.Connected(capabilities))
        }

        override fun onLost(network: Network) {
            notifyChange(ConnectionManager.NetworkState.NotConnectedStatus)
        }
    }
}