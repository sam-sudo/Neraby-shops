package com.klikin.nearby_shops.presentation.usescases.shopsList

import androidx.lifecycle.ViewModel
import com.klikin.nearby_shops.data.local.storeList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ShopListViewModel : ViewModel() {
    private val _state = MutableStateFlow(ShopListViewState())
    val sate = _state.asStateFlow()

    fun loadShops() {
        _state.update {
            it.copy(shopList = storeList)
        }
    }

    fun loadCategories() {
        val categoriesSet = _state.value.shopList.map { it.category }.toSet()
        _state.update { it.copy(categories = categoriesSet.toList()) }
    }
}
