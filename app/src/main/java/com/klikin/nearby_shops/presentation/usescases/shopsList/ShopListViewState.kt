package com.klikin.nearby_shops.presentation.usescases.shopsList

import com.klikin.nearby_shops.domain.model.Store

data class ShopListViewState(
    val shopList: List<Store> = listOf(),
    val lessThan1KmShopList: Int = 0,
    val categoriesMap: MutableMap<String, Int> = mutableMapOf(),
    val isLoading: Boolean = true,
)
