package com.klikin.nearby_shops.presentation.utils

import android.location.Location

object LocationHandler {
    fun distanceBetweenPositionNowAndPoint(
        lat1: Double,
        long1: Double,
    ): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            lat1,
            long1,
            getLocation()[0],
            getLocation()[1],
            results,
        )
        return if (results[0] < 1) 1.0 else results[0].toDouble()
    }

    fun getLocation(): List<Double> {
        return listOf(37.168391118233785, -3.604079197981948)
    }
}
