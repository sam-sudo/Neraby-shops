package com.klikin.nearby_shops.presentation.usescases.shopsList

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klikin.nearby_shops.domain.mapper.toStoreList
import com.klikin.nearby_shops.domain.model.Store
import com.klikin.nearby_shops.domain.model.enums.Categories
import com.klikin.nearby_shops.domain.repository.RemoteStoreRepository
import com.klikin.nearby_shops.framework.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopListViewModel
    @Inject
    constructor(
        private val storeRepository: RemoteStoreRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(ShopListViewState())
        val sate = _state.asStateFlow()

        private var locationService: LocationService = LocationService()
        private var actualPage = 0
        private var storesList = ArrayList<Store>()
        var storeListFromApi = ArrayList<Store>()
        val storesListLessThan1km = ArrayList<Store>()

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
            viewModelScope.launch {
                _state.update {
                    it.copy(isLoading = true)
                }
                try {
                    storeListFromApi = storeRepository.getStores().body()?.toStoreList() ?: ArrayList()
                    storesList = getElementsInGroupsOf20(context, storeListFromApi, 0)
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
            viewModelScope.launch {
                storesList = getElementsInGroupsOf20(context, storeListFromApi, pageIndex)
                _state.update {
                    it.copy(shopList = storesList)
                }
            }
        }

        fun showShopsNextPage(context: Context) {
            viewModelScope.launch {
                actualPage += 20
                val newPageItems = getElementsInGroupsOf20(context, storeListFromApi, actualPage)
                val updatedList = mutableListOf<Store>()
                updatedList.addAll(_state.value.shopList)
                updatedList.addAll(newPageItems)
                storesList = ArrayList(updatedList)
                _state.update {
                    it.copy(shopList = storesList)
                }
            }
        }

        fun showShopsNextPageByCategory(
            context: Context,
            category: Categories,
        ) {
            viewModelScope.launch {
                actualPage += 20
                val newPageItems = getElementsInGroupsOf20ByCategory(context, category, actualPage)
                val updatedList = mutableListOf<Store>()
                updatedList.addAll(_state.value.shopList)
                updatedList.addAll(newPageItems)
                storesList = ArrayList(updatedList)
                _state.update {
                    it.copy(shopList = storesList)
                }
            }
        }

        private suspend fun loadLessThanOneKilometresShops(context: Context) {
            viewModelScope.launch {
                val userLocation = locationService.getUserLocation(context)
                storesListLessThan1km.clear()
                val tempOrderShops = getElementsInGroupsOf20(context, storeListFromApi, 0)
                tempOrderShops.forEach { store ->
                    val location = store.location

                    if (!location.isNullOrEmpty() && userLocation != null) {
                        val latitude = location[1]
                        val longitude = location[0]
                        val userLatitude = userLocation.latitude
                        val userLongitude = userLocation.longitude
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
            viewModelScope.launch {
                storesList = getElementsInGroupsOf20(context, storesListLessThan1km, 0)
                _state.update {
                    it.copy(shopList = storesList)
                }
            }
        }

        fun loadShopsByCategoryAndLessThan1km(
            context: Context,
            categorySelected: Categories,
        ) {
            viewModelScope.launch {
                val categories = _state.value.categoriesMap.keys
                if (categories.contains(categorySelected.toString())) {
                    val storeListByCategory =
                        storesListLessThan1km.filter {
                            it.category == categorySelected
                        }
                    storesList = getElementsInGroupsOf20(context, ArrayList(storeListByCategory), 0)
                    _state.update {
                        it.copy(shopList = storesList)
                    }
                }
            }
        }

        fun loadShopsByCategory(
            context: Context,
            categorySelected: Categories,
        ) {
            viewModelScope.launch {
                val categories = _state.value.categoriesMap.keys
                if (categories.contains(categorySelected.toString())) {
                    // val storeListByCategory = storesList.filter { it.category == Categories.valueOf(categorySelected) }
                    val storeListByCategoryToShow =
                        getElementsInGroupsOf20ByCategory(
                            context,
                            categorySelected,
                            0,
                        )
                    _state.update {
                        it.copy(shopList = storeListByCategoryToShow)
                    }
                }
            }
        }

        private fun loadCategories() {
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

        private suspend fun sortByDistanceToUser(
            context: Context,
            storeList: List<Store>,
        ): List<Store> {
            val userLocation = locationService.getUserLocation(context)
            if (userLocation == null || userLocation.latitude == null || userLocation.longitude == null) {
                return storeList
            }
            val userLatitude = userLocation.latitude
            val userLongitude = userLocation.longitude
            return storeList.sortedBy { store ->
                val location = store.location
                if (!location.isNullOrEmpty()) {
                    val latitude = location[1]
                    val longitude = location[0]
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

        private suspend fun getElementsInGroupsOf20(
            context: Context,
            list: ArrayList<Store>,
            page: Int,
        ): ArrayList<Store> {
            actualPage = page
            val startIndex = page * 20
            val endIndex = startIndex + 20

            if (startIndex < list.size) {
                val tempList = ArrayList(sortByDistanceToUser(context, list))
                return ArrayList(tempList.subList(startIndex, Math.min(endIndex, tempList.size)))
            } else {
                return ArrayList()
            }
        }

        private suspend fun getElementsInGroupsOf20ByCategory(
            context: Context,
            category: Categories,
            page: Int,
            list: ArrayList<Store>? = ArrayList(),
        ): ArrayList<Store> {
            var tempListByCategory = listOf<Store>()

            if (list.isNullOrEmpty() || list.size == 0) {
                tempListByCategory =
                    storeListFromApi.filter {
                        it.category == category
                    }
            } else {
                tempListByCategory =
                    list.filter {
                        it.category == category
                    }
            }

            val startIndex = page * 20
            val endIndex = startIndex + 20

            return if (startIndex < tempListByCategory.size) {
                val tempList = ArrayList(sortByDistanceToUser(context, tempListByCategory))
                ArrayList(
                    tempList.subList(
                        startIndex,
                        Math.min(endIndex, tempList.size),
                    ),
                )
            } else {
                ArrayList()
            }
        }
    }
