package com.klikin.nearby_shops.presentation.usescases.shopsList

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import com.klikin.nearby_shops.data.local.storeList
import com.klikin.nearby_shops.domain.model.Store
import com.klikin.nearby_shops.framework.LocationService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShopListViewModel : ViewModel() {
    private val _state = MutableStateFlow(ShopListViewState())
    val sate = _state.asStateFlow()

    private val locationService = LocationService()

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

    fun loadShops(context: Context) {
        GlobalScope.launch {
            val storeListOrderedByCloseness =
                sortByDistanceToUser(storeList, getLocation(context))
            _state.update {
                it.copy(shopList = storeListOrderedByCloseness)
            }
            loadCategories()
        }
    }

    fun loadShopsByCategory(
        context: Context,
        categorySelected: String,
    ) {
        GlobalScope.launch {
            val categories = _state.value.categoriesMap.keys
            if (categories.contains(categorySelected)) {
                val storeListByCategory = storeList.filter { it.category == categorySelected }

                val storeListOrderedByCloseness =
                    sortByDistanceToUser(storeListByCategory, getLocation(context))

                _state.update {
                    it.copy(shopList = storeListOrderedByCloseness)
                }
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
        userLocation: Location?,
    ): List<Store> {
        return storeList.sortedBy { store ->
            val location = store.location
            if (!location.isNullOrEmpty() && userLocation != null) {
                val results = FloatArray(1)
                Location.distanceBetween(
                    location[0],
                    location[1],
                    userLocation.latitude,
                    userLocation.longitude,
                    results,
                )
                results[0]
            } else {
                Float.MAX_VALUE
            }
        }
    }

    suspend fun getLocation(context: Context): Location? {
        return locationService.getUserLocation(context)
    }
}
