package com.hamza.networkconnection

import android.os.Handler
import android.os.Looper

abstract class ConnectionManagerBase: ConnectionManager {

    private val handler = Handler(Looper.getMainLooper())
    private val internetListeners = mutableSetOf<ConnectionManager.InternetStatusListener>()
    private var subscribed = false

    override fun addManager(listener: ConnectionManager.InternetStatusListener) {
        internetListeners.add(listener)
        listener.onStateChange(getNetworkState()) // propagate an initial state
        verifySubscription()
    }

    override fun removeManager(listener: ConnectionManager.InternetStatusListener) {
        internetListeners.remove(listener)
        verifySubscription()
    }


    private fun verifySubscription() {
        if (!subscribed && internetListeners.isNotEmpty()) {
            subscribe()
            subscribed = true
        } else if (subscribed && internetListeners.isEmpty()) {
            unsubscribe()
            subscribed = false
        }
    }

    protected fun notifyChange(state: ConnectionManager.NetworkState) {
        handler.post {
            for (listener in internetListeners) {
                listener.onStateChange(state)
            }
        }
    }

    protected abstract fun subscribe()
    protected abstract fun unsubscribe()
}
