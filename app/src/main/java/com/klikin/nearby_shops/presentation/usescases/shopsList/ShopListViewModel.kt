package com.klikin.nearby_shops.presentation.usescases.shopsList

import android.content.Context
import android.location.Location
import android.util.Log
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
    val storesList = ArrayList<Store>()
    val storesListLessThan1km = ArrayList<Store>()

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
                sortByDistanceToUser(storeList, locationService.getUserLocation(context))
            storesList.clear()
            storesList.addAll(storeListOrderedByCloseness)
            _state.update {
                it.copy(shopList = storesList)
            }
            loadCategories()
        }
    }

    fun loadLessThanOneKilometresShops(context: Context) {
        GlobalScope.launch {
            val storeListFilter =
                storesList.filter { store ->
                    val location = store.location
                    var destinationLocation = Location("provider")
                    val userLocation = locationService.getUserLocation(context)

                    if (!location.isNullOrEmpty() && userLocation != null) {
                        destinationLocation.latitude = location[0]
                        destinationLocation.longitude = location[1]
                        Log.d("TAG", "sortByDistanceToUser: ${userLocation.distanceTo(destinationLocation)}")

                        userLocation.distanceTo(destinationLocation) <= 2087.0 && store.location.isNotEmpty()
                    } else {
                        false
                    }
                }

            storesListLessThan1km.clear()
            storesListLessThan1km.addAll(storeListFilter)
            _state.update {
                it.copy(
                    shopList = storesListLessThan1km,
                )
            }
        }
    }

    fun loadAllShops() {
        _state.update {
            it.copy(shopList = storesList)
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
                    sortByDistanceToUser(storeListByCategory, locationService.getUserLocation(context))
                _state.update {
                    it.copy(shopList = storeListOrderedByCloseness)
                }
            }
        }
    }

    fun loadShopsByCategoryAndLessThan1km(
        context: Context,
        categorySelected: String,
    ) {
        GlobalScope.launch {
            val categories = _state.value.categoriesMap.keys
            if (categories.contains(categorySelected)) {
                val storeListByCategory = storesListLessThan1km.filter { it.category == categorySelected }

                val storeListOrderedByCloseness =
                    sortByDistanceToUser(storeListByCategory, locationService.getUserLocation(context))

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
            var destinationLocation = Location("provider")

            if (!location.isNullOrEmpty() && userLocation != null) {
                destinationLocation.latitude = location[0]
                destinationLocation.longitude = location[1]
                Log.d("TAG", "sortByDistanceToUser: ${userLocation.distanceTo(destinationLocation)}")

                userLocation.distanceTo(destinationLocation)
            } else {
                Float.MAX_VALUE
            }
        }
    }
}
