package com.klikin.nearby_shops.presentation.usescases.shopsList

import android.content.Context
import android.location.Location
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
        var actualPage = 0

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
                    orderSoreListByNear(context)
                } finally {
                    _state.update {
                        it.copy(
                            shopList = storesList,
                            isLoading = false,
                        )
                    }
                    loadCategories()
                    loadLessThanOneKilometresShops(context)
                }
            }
        }

        fun showShops(
            context: Context,
            pageIndex: Int,
        ) {
            GlobalScope.launch {
                storesList = getElementsInGroupsOf20(storeListFromApi, pageIndex)
                orderSoreListByNear(context)
                _state.update {
                    it.copy(shopList = storesList)
                }
            }
        }

        fun showShopsNextPage(context: Context) {
            GlobalScope.launch {
                actualPage += 20
                val newPageItems = getElementsInGroupsOf20(storeListFromApi, actualPage)
                val updatedList = mutableListOf<Store>()
                updatedList.addAll(_state.value.shopList)
                updatedList.addAll(newPageItems)
                storesList = ArrayList(updatedList)
                orderSoreListByNear(context)
                _state.update {
                    it.copy(shopList = storesList)
                }
            }
        }

        fun showShopsNextPageByCategory(
            context: Context,
            category: Categories,
        ) {
            GlobalScope.launch {
                actualPage += 20
                val newPageItems = getElementsInGroupsOf20ByCategory(category, actualPage)
                val updatedList = mutableListOf<Store>()
                updatedList.addAll(_state.value.shopList)
                updatedList.addAll(newPageItems)
                storesList = ArrayList(updatedList)
                orderSoreListByNear(context)
                _state.update {
                    it.copy(shopList = storesList)
                }
            }
        }

        suspend fun loadLessThanOneKilometresShops(context: Context) {
            withContext(Dispatchers.Default) {
                storesListLessThan1km.clear()
                storeListFromApi.forEach { store ->
                    val location = store.location
                    val userLocation = locationService.getUserLocation(context)

                    if (!location.isNullOrEmpty() && userLocation != null) {
                        val latitude = location[1]
                        val longitude = location[0]
                        val userLatitude = userLocation.latitude.toDouble()
                        val userLongitude = userLocation.longitude.toDouble()
                        val distanceInMeters =
                            locationService.calculateDistanceInMeters(
                                userLatitude,
                                userLongitude,
                                latitude,
                                longitude,
                            )

                        if (distanceInMeters <= 1000.0 && store.location.isNotEmpty()) {
                            storesListLessThan1km.add(store)
                        }
                    }
                }
            }
        }

        fun showLessThan1KilometresShops(context: Context) {
            GlobalScope.launch {
                storesList = getElementsInGroupsOf20(storesListLessThan1km, 0)
                orderSoreListByNear(context)
                _state.update {
                    it.copy(shopList = storesList)
                }
            }
        }

        fun loadShopsByCategory(
            context: Context,
            categorySelected: Categories,
        ) {
            GlobalScope.launch {
                val categories = _state.value.categoriesMap.keys
                if (categories.contains(categorySelected.toString())) {
                    // val storeListByCategory = storesList.filter { it.category == Categories.valueOf(categorySelected) }

                    val storeListByCategoryToShow =
                        getElementsInGroupsOf20ByCategory(
                            categorySelected,
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
            categorySelected: Categories,
        ) {
            GlobalScope.launch {
                val categories = _state.value.categoriesMap.keys
                if (categories.contains(categorySelected.toString())) {
                    val storeListByCategory =
                        storesListLessThan1km.filter {
                            it.category == categorySelected
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

                if (!location.isNullOrEmpty()) {
                    val latitude = location[1]
                    val longitude = location[0]

                    val userLatitude = userLocation?.latitude?.toDouble() ?: 0.0
                    val userLongitude = userLocation?.longitude?.toDouble() ?: 0.0
                    val distanceInMeters =
                        locationService.calculateDistanceInMeters(
                            userLatitude,
                            userLongitude,
                            latitude,
                            longitude,
                        )

                    distanceInMeters
                } else {
                    Float.MAX_VALUE
                }
            }
        }

        fun getElementsInGroupsOf20(
            list: ArrayList<Store>,
            page: Int,
        ): ArrayList<Store> {
            actualPage = page
            val startIndex = page * 20
            val endIndex = startIndex + 20
            return if (startIndex < list.size) {
                ArrayList(list.subList(startIndex, Math.min(endIndex, list.size)))
            } else {
                ArrayList()
            }
        }

        fun getElementsInGroupsOf20ByCategory(
            category: Categories,
            page: Int,
        ): ArrayList<Store> {
            val list =
                ArrayList(
                    storeListFromApi.filter {
                        it.category == category
                    },
                )
            val startIndex = page * 20
            val endIndex = startIndex + 20
            return if (startIndex < list.size) {
                ArrayList(list.subList(startIndex, Math.min(endIndex, list.size)))
            } else {
                ArrayList()
            }
        }

        suspend fun orderSoreListByNear(context: Context) {
            val userLocation = locationService.getUserLocation(context)
            val storeListOrderedByCloseness =
                sortByDistanceToUser(storesList, userLocation)
            storesList.clear()
            storesList.addAll(storeListOrderedByCloseness)
        }
    }
