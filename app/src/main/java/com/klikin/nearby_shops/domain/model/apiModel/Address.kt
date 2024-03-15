package com.klikin.nearby_shops.domain.model.apiModel

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("city")
    val city: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("state")
    val state: String? = "",
    @SerializedName("street")
    val street: String?,
    @SerializedName("zip")
    val zip: String?,
)
