package com.eviort.cabedriver.NTHelper

import android.location.Location
import android.os.Handler
import android.os.SystemClock
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.eviort.cabedriver.NTListeners.LatLngInterpolator
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class MarkerAnimationHelper {
    var isMarkerRotating = true;
    fun animateMarkerToGB(marker: Marker, finalPosition: LatLng, latLngInterpolator: LatLngInterpolator) {
        val startPosition = marker.position
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = 2000f
        val hideMarker = false
        handler.post(object : Runnable {
            var elapsed: Long = 0
            var t: Float = 0.toFloat()
            var v: Float = 0.toFloat()
            override fun run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                v = interpolator.getInterpolation(t)
                marker.position = latLngInterpolator.interpolate(v, startPosition, finalPosition)
                marker.rotation = bearingBetweenLocations(startPosition, LatLng(finalPosition.latitude, finalPosition.longitude))
                rotateMarker(marker, marker.rotation)
                // marker.rotation = bearingBetweenLocations(startPosition, LatLng(finalPosition.latitude, finalPosition.longitude)) * (180.0f / Math.PI.toFloat())
                //  marker.rotation = bearing
                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                    // marker.isVisible = true
                } else {
                    // marker.isVisible = true
                    if (hideMarker) {
                        marker.isVisible = false
                    } else {
                        marker.isVisible = true
                    }
                }
            }
        })
    }


    fun moveVechile(myMarker: Marker, finalPosition: Location) {
        val startPosition = myMarker.position
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val interpolator: Interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = 3000f
        val hideMarker = false
        handler.post(object : Runnable {
            var elapsed: Long = 0
            var t = 0f
            var v = 0f
            override fun run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                v = interpolator.getInterpolation(t)
                val currentPosition = LatLng(
                        startPosition.latitude * (1 - t) + finalPosition.getLatitude() * t,
                        startPosition.longitude * (1 - t) + finalPosition.getLongitude() * t)
                myMarker.setPosition(currentPosition)
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.isVisible = false
                    } else {
                        myMarker.isVisible = true
                    }
                }
            }
        })
    }


    private fun getBearing(begin: LatLng, end: LatLng): Float {
        val lat = Math.abs(begin.latitude - end.latitude)
        val lng = Math.abs(begin.longitude - end.longitude)
        if (begin.latitude < end.latitude && begin.longitude < end.longitude) return Math.toDegrees(Math.atan(lng / lat)).toFloat() else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) return (90 - Math.toDegrees(Math.atan(lng / lat)) + 90).toFloat() else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) return (Math.toDegrees(Math.atan(lng / lat)) + 180).toFloat() else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) return (90 - Math.toDegrees(Math.atan(lng / lat)) + 270).toFloat()
        return (-1).toFloat()
    }

    private fun rotateMarker(marker: Marker, toRotation: Float) {

        if (!isMarkerRotating) {
            val handler = Handler()
            val start = SystemClock.uptimeMillis()
            val startRotation = marker.rotation
            val duration: Long = 1000
            val interpolator: Interpolator = LinearInterpolator()
            handler.post(object : Runnable {
                override fun run() {
                    isMarkerRotating = true
                    val elapsed = SystemClock.uptimeMillis() - start
                    val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                    val rot = t * toRotation + (1 - t) * startRotation
                    marker.rotation = if (-rot > 180) rot / 2 else rot
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16)
                    } else {
                        isMarkerRotating = false
                    }
                }
            })
        }
    }

    private fun bearingBetweenLocations(latLng1: LatLng, latLng2: LatLng): Float {
        val PI = 3.14159
        val lat1 = latLng1.latitude * PI / 180
        val long1 = latLng1.longitude * PI / 180
        val lat2 = latLng2.latitude * PI / 180
        val long2 = latLng2.longitude * PI / 180
        val dLon = long2 - long1
        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon))
        var brng = Math.atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        return brng.toFloat()
    }
}

