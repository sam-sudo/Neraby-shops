package com.klikin.nearby_shops.domain.model.apiModel


import com.google.gson.annotations.SerializedName

data class StoresApiModelItem(
    @SerializedName("address")
    val address: Address,
    @SerializedName("cashback")
    val cashback: Double,
    @SerializedName("category")
    val category: String,
    @SerializedName("_id")
    val id: Int,
    @SerializedName("location")
    val location: List<Double>,
    @SerializedName("name")
    val name: String,
    @SerializedName("openingHours")
    val openingHours: String,
    @SerializedName("photo")
    val photo: String
)