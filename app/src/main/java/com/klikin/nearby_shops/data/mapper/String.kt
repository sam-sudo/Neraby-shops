package com.klikin.nearby_shops.data.mapper

import com.klikin.nearby_shops.domain.model.Store

fun Store.openHoursLittleFormat(): List<String> {
    val openingHourText = this.openingHours
    return openingHours.split(";")
}
