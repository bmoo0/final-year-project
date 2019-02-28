package com.bitcoinwallet.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by Ben Moore on 11/02/2019.
 */

class NetworkUtilities {
    companion object {
        fun isConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnected == true
            return isConnected
        }

        fun isUsingWifi(context: Context): Boolean {
            return connectionType(context) == "WIFI"
        }

        fun isUsingMobile(context: Context): Boolean {
            return connectionType(context) == "MOBILE"
        }

        private fun connectionType(context: Context): String {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

            return when (activeNetwork?.type) {
                ConnectivityManager.TYPE_WIFI -> "WIFI"
                ConnectivityManager.TYPE_MOBILE -> "MOBILE"
                else -> "Connection unknown"
            }
        }
    }
}