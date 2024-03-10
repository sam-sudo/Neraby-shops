package com.klikin.nearby_shops.presentation.usescases.shopsList

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
            0xFFFF0000.toInt(), // Rojo
            0xFFFFA500.toInt(), // Naranja
            0xFF8B4513.toInt(), // Marrón
            0xFF008000.toInt(), // Verde
            0xFF0000FF.toInt(), // Azul
            0xFF4B0082.toInt(), // Índigo
            0xFFEE82EE.toInt(), // Violeta
        )

    fun loadShops() {
        val storeListOrderedBycloseness =
            sortByDistanceToUser(storeList, listOf(37.168476142495834F, -3.6040761719512906F))
        _state.update {
            it.copy(shopList = storeListOrderedBycloseness)
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
        userLocation: List<Float>,
    ): List<Store> {
        return storeList.sortedBy { store ->
            distanceBetweenPoints(store.location!!, userLocation)
        }
    }

    fun distanceBetweenPoints(
        location1: List<Float>,
        location2: List<Float>,
    ): Float {
        val deltaX = location1[0] - location2[0]
        val deltaY = location1[1] - location2[1]
        return kotlin.math.sqrt(deltaX * deltaX + deltaY * deltaY)
    }
}
