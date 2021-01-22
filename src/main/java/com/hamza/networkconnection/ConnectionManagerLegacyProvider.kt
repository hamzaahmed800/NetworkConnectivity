package com.hamza.networkconnection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionManagerLegacyProvider(
    private val context: Context,
    private val conMan: ConnectivityManager
): ConnectionManagerBase() {

    private val receiver = ConnectivityReceiver()

    override fun getNetworkState(): ConnectionManager.NetworkState {
        val activeNetworkInfo = conMan.activeNetworkInfo
        return if (activeNetworkInfo != null) {
            ConnectionManager.NetworkState.InternetStatus.ConnectedLegacy(activeNetworkInfo)
        } else {
            ConnectionManager.NetworkState.NotConnectedStatus
        }
    }

    override fun subscribe() {
        context.registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun unsubscribe() {
        context.unregisterReceiver(receiver)
    }


    private inner class ConnectivityReceiver : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) {
            // on some devices ConnectivityManager.getActiveNetworkInfo() does not provide the correct network state
            val networkInfo = conMan.activeNetworkInfo
            val fallbackNetworkInfo: NetworkInfo? = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO)
            // a set of dirty workarounds
            val state: ConnectionManager.NetworkState =
                if (networkInfo?.isConnectedOrConnecting == true) {
                    ConnectionManager.NetworkState.InternetStatus.ConnectedLegacy(networkInfo)
                } else if (networkInfo != null && fallbackNetworkInfo != null &&
                    networkInfo.isConnectedOrConnecting != fallbackNetworkInfo.isConnectedOrConnecting
                ) {
                    ConnectionManager.NetworkState.InternetStatus.ConnectedLegacy(
                        fallbackNetworkInfo
                    )
                } else {
                    val state = networkInfo ?: fallbackNetworkInfo
                    if (state != null) ConnectionManager.NetworkState.InternetStatus
                        .ConnectedLegacy(state) else ConnectionManager.NetworkState.NotConnectedStatus
                }
            notifyChange(state)
        }
    }

}