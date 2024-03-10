package com.klikin.nearby_shops.presentation.usescases.shopsList

import androidx.lifecycle.ViewModel
import com.klikin.nearby_shops.data.local.storeList
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
        _state.update {
            it.copy(shopList = storeList)
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
}
