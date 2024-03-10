package com.klikin.nearby_shops.presentation.usescases.shopsList

import com.klikin.nearby_shops.domain.model.Store

data class ShopListViewState(
    val shopList: List<Store> = listOf(),
    val categories: List<String> = listOf(),
    val isLoading: Boolean = false,
)
