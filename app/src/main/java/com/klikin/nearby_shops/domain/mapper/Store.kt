package com.klikin.nearby_shops.domain.mapper

import com.klikin.nearby_shops.domain.model.Store

fun Store.openHoursLittleFormat(): List<String> {
    return openingHours.split(";")
}
