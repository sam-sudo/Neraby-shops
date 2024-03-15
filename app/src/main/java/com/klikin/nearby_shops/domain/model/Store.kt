package com.klikin.nearby_shops.domain.model

import com.klikin.nearby_shops.domain.model.enums.Categories

data class Store(
    val id: Int,
    val name: String = UNKNOWN_TEXT,
    val photo: String = UNKNOWN_TEXT,
    val cashback: Double = UNKNOWN_CASHBACK,
    val address: Address? = null,
    val openingHours: String = UNKNOWN_TEXT,
    val category: Categories = Categories.UNKNOWN,
    val location: List<Double>? = null,
)

const val UNKNOWN_TEXT = "Unknown"
const val UNKNOWN_CASHBACK = 0.0
