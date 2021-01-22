package com.hamza.networkconnection

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.annotation.RequiresApi

interface ConnectionManager {

    interface InternetStatusListener {
        fun onStateChange(state: NetworkState)
    }

    fun addManager(listener: InternetStatusListener)
    fun removeManager(listener: InternetStatusListener)

    fun getNetworkState(): NetworkState

    @Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
    sealed class NetworkState {
        object NotConnectedStatus : NetworkState()

        sealed class InternetStatus(val isInternetAvailable: Boolean) : NetworkState() {

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            data class Connected(val capabilities: NetworkCapabilities) : InternetStatus(
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            )

            @Suppress("DEPRECATION")
            data class ConnectedLegacy(val networkInfo: NetworkInfo) : InternetStatus(
                networkInfo.isConnectedOrConnecting
            )
        }
    }

    companion object {
        fun setConnectionManager(context: Context): ConnectionManager{
            val conMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ConnectionManagerProvider(conMan)
            } else {
                ConnectionManagerLegacyProvider(context, conMan)
            }
        }
    }

}