package com.klikin.nearby_shops.domain.mapper

fun Float.handlerMetresText(): String {
    return if (this >= 1000) {
        String.format("%.2f Km.", this / 1000)
    } else {
        String.format("%.2f m.", this)
    }
}
