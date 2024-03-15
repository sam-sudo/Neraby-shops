package com.klikin.nearby_shops.domain.mapper

import com.klikin.nearby_shops.domain.model.Address
import com.klikin.nearby_shops.domain.model.Store
import com.klikin.nearby_shops.domain.model.apiModel.StoresApiModel
import com.klikin.nearby_shops.domain.model.apiModel.StoresApiModelItem
import com.klikin.nearby_shops.domain.model.enums.Categories

fun StoresApiModelItem.toStore(): Store {
    return Store(
        id = this.id,
        name = this.name,
        photo = this.photo,
        cashback = this.cashback,
        address =
            Address(
                street = this.address.street,
                city = this.address.city,
                state = this.address.state,
                zip = this.address.zip,
                country = this.address.country,
            ),
        openingHours = this.openingHours,
        category = Categories.valueOf(this.category.uppercase()),
        location = this.location,
    )
}

fun StoresApiModel.toStoreList(): ArrayList<Store> {
    val storeArrayList = ArrayList<Store>()
    storeArrayList.addAll(this.map { it.toStore() })
    return storeArrayList
}
