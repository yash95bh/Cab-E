package com.eviort.cabedriver.NTHelper

import android.content.Context
import android.net.ConnectivityManager

class ConnectionHelper(private val context: Context) {
    //Toast.makeText(context, "Oops ! Connect your Internet", Toast.LENGTH_LONG).show();
    val isConnectingToInternet: Boolean
        get() {
            val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivity.activeNetworkInfo
            return if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
                true
            } else {
                //Toast.makeText(context, "Oops ! Connect your Internet", Toast.LENGTH_LONG).show();
                false
            }
        }
}