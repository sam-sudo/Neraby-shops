package com.klikin.nearby_shops.domain.model

data class Store(
    val id: Int,
    val name: String = UNKNOWN_TEXT,
    val photo: String = UNKNOWN_TEXT,
    val cashback: Int = UNKNOWN_CASHBACK,
    val address: Address? = null,
    val openingHours: String = UNKNOWN_TEXT,
    val category: String = UNKNOWN_TEXT,
    val location: List<Double>? = null,
)

const val UNKNOWN_TEXT = "Unknown"
const val UNKNOWN_CASHBACK = 0
