package com.eviort.cabedriver.NTHelper

import android.util.Log
import com.eviort.cabedriver.NTModel.Driver
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class FirebaseHelper constructor(driverId: String) {

    companion object {
        private const val ONLINE_DRIVERS = "online_drivers"
    }

    private val onlineDriverDatabaseReference: DatabaseReference = FirebaseDatabase
            .getInstance("https://dogwood-courier-429509-e9-default-rtdb.firebaseio.com/")
            .reference
            .child(ONLINE_DRIVERS)
            .child(driverId)

    init {
        onlineDriverDatabaseReference
                .onDisconnect()
                .removeValue()
    }

    /* fun updateDriver(driver: Driver) {
         onlineDriverDatabaseReference
                 .setValue(driver)
         Log.e("Driver Info", " Updated")
     }
 */
    fun deleteDriver() {
        onlineDriverDatabaseReference
                .removeValue()
    }

    fun updateDriver(driver: Driver) {
        onlineDriverDatabaseReference
                .setValue(driver)
        Log.e("Driver Info", " Updated")
    }
}