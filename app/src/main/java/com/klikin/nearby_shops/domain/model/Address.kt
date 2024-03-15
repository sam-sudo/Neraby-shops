package com.klikin.nearby_shops.domain.model

data class Address(
    val street: String? = UNKNOWN_TEXT,
    val city: String? = UNKNOWN_TEXT,
    val state: String? = UNKNOWN_TEXT,
    val zip: String? = UNKNOWN_TEXT,
    val country: String? = UNKNOWN_TEXT,
)
