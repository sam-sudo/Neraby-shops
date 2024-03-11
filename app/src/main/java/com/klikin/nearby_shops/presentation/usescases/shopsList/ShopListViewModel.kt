package com.klikin.nearby_shops.presentation.usescases.shopsList

import android.location.Location
import androidx.lifecycle.ViewModel
import com.klikin.nearby_shops.data.local.storeList
import com.klikin.nearby_shops.domain.model.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ShopListViewModel : ViewModel() {
    private val _state = MutableStateFlow(ShopListViewState())
    val sate = _state.asStateFlow()

    val rainbowColorsArgb =
        listOf(
            0xFFE67E22.toInt(),
            0xFFF1C40F.toInt(),
            0xFF2ECC71.toInt(),
            0xFFD35400.toInt(),
            0xFF1ABC9C.toInt(),
            0xFF2980B9.toInt(),
            0xFF9B59B6.toInt(),
            0xFFCB4335.toInt(),
        )

    fun loadShops() {
        val storeListOrderedByCloseness =
            sortByDistanceToUser(storeList, getLocation())
        _state.update {
            it.copy(shopList = storeListOrderedByCloseness)
        }
    }

    fun loadShopsByCategory(categorySelected: String) {
        val categories = _state.value.categoriesMap.keys
        if (categories.contains(categorySelected)) {
            val storeListByCategory = storeList.filter { it.category == categorySelected }

            val storeListOrderedByCloseness =
                sortByDistanceToUser(storeListByCategory, getLocation())

            _state.update {
                it.copy(shopList = storeListOrderedByCloseness)
            }
        }
    }

    fun loadCategories() {
        val colors = rainbowColorsArgb
        val uniqueCategories = _state.value.shopList.map { it.category }.toSet()
        val categoriesMap = mutableMapOf<String, Int>()

        uniqueCategories.forEachIndexed { index, category ->
            val colorIndex = index % colors.size
            val color = colors[colorIndex]
            categoriesMap[category] = color
        }

        _state.update { it.copy(categoriesMap = categoriesMap) }
    }

    fun sortByDistanceToUser(
        storeList: List<Store>,
        userLocation: List<Double>,
    ): List<Store> {
        return storeList.sortedBy { store ->
            val location = store.location
            if (location != null && location.isNotEmpty()) {
                val results = FloatArray(1)
                Location.distanceBetween(
                    location[0],
                    location[1],
                    userLocation[0],
                    userLocation[1],
                    results,
                )
                results[0]
            } else {
                Float.MAX_VALUE
            }
        }
    }

    fun getLocation(): List<Double> {
        return listOf(37.168476142495834, -3.6040761719512906)
    }
}
