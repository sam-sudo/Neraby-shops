package com.klikin.nearby_shops.presentation.usescases.shopsList

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.klikin.nearby_shops.domain.mapper.toStoreList
import com.klikin.nearby_shops.domain.model.Store
import com.klikin.nearby_shops.domain.model.enums.Categories
import com.klikin.nearby_shops.domain.repository.RemoteStoreRepository
import com.klikin.nearby_shops.framework.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ShopListViewModel
    @Inject
    constructor(
        private val storeRepository: RemoteStoreRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(ShopListViewState())
        val sate = _state.asStateFlow()
        var storeListFromApi = ArrayList<Store>()
        var storesList = ArrayList<Store>()
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
                _state.update {
                    it.copy(isLoading = true)
                }
                try {
                    storeListFromApi = storeRepository.getStores().body()?.toStoreList() ?: ArrayList()
                    storesList = getElementsInGroupsOf20(storeListFromApi, 0)

                    val storeListOrderedByCloseness =
                        sortByDistanceToUser(storesList, locationService.getUserLocation(context))
                    storesList.clear()
                    storesList.addAll(storeListOrderedByCloseness)
                    loadLessThanOneKilometresShops(context)
                } finally {
                    _state.update {
                        it.copy(
                            shopList = storesList,
                            isLoading = false,
                        )
                    }

                    loadCategories()
                }
            }
        }

        suspend fun loadLessThanOneKilometresShops(context: Context) {
            val storeListFilter =
                withContext(Dispatchers.Default) {
                    storesList.filter { store ->
                        val location = store.location
                        var destinationLocation = Location("provider")
                        val userLocation = locationService.getUserLocation(context)

                        if (!location.isNullOrEmpty() && userLocation != null) {
                            destinationLocation.latitude = location[0]
                            destinationLocation.longitude = location[1]
                            Log.d(
                                "TAG",
                                "sortByDistanceToUser: ${userLocation.distanceTo(destinationLocation)}",
                            )

                            userLocation.distanceTo(destinationLocation) <= 2087.0 && store.location.isNotEmpty()
                        } else {
                            false
                        }
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
                    // val storeListByCategory = storesList.filter { it.category == Categories.valueOf(categorySelected) }
                    val storeListByCategory =
                        ArrayList(
                            storeListFromApi.filter {
                                it.category == Categories.valueOf(categorySelected)
                            },
                        )
                    val storeListByCategoryToShow =
                        getElementsInGroupsOf20ByCategory(
                            storeListByCategory,
                            Categories.valueOf(categorySelected),
                            0,
                        )

                    val storeListOrderedByCloseness =
                        sortByDistanceToUser(
                            storeListByCategoryToShow,
                            locationService.getUserLocation(context),
                        )
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
                    val storeListByCategory =
                        storesListLessThan1km.filter {
                            it.category == Categories.valueOf(categorySelected)
                        }

                    val storeListOrderedByCloseness =
                        sortByDistanceToUser(
                            storeListByCategory,
                            locationService.getUserLocation(context),
                        )

                    _state.update {
                        it.copy(shopList = storeListOrderedByCloseness)
                    }
                }
            }
        }

        fun loadCategories() {
            val colors = rainbowColorsArgb
            // this was to make dinamics list
            // val uniqueCategories = _state.value.shopList.map { it.category }.toSet()
            val uniqueCategories = Categories.entries.toTypedArray()
            val categoriesMap = mutableMapOf<String, Int>()

            uniqueCategories.forEachIndexed { index, category ->
                val colorIndex = index % colors.size
                val color = colors[colorIndex]
                categoriesMap[category.toString()] = color
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
                    Log.d(
                        "TAG",
                        "sortByDistanceToUser: ${userLocation.distanceTo(destinationLocation)}",
                    )

                    userLocation.distanceTo(destinationLocation)
                } else {
                    Float.MAX_VALUE
                }
            }
        }

        fun getElementsInGroupsOf20(
            list: ArrayList<Store>,
            page: Int,
        ): ArrayList<Store> {
            val startIndex = page * 20
            val endIndex = startIndex + 20
            return if (startIndex < list.size) {
                ArrayList(list.subList(startIndex, Math.min(endIndex, list.size)))
            } else {
                ArrayList()
            }
        }

        fun getElementsInGroupsOf20ByCategory(
            list: ArrayList<Store>,
            category: Categories,
            page: Int,
        ): ArrayList<Store> {
            val startIndex = page * 20
            val endIndex = startIndex + 20
            return if (startIndex < list.size) {
                ArrayList(list.subList(startIndex, Math.min(endIndex, list.size)))
            } else {
                ArrayList()
            }
        }
    }
