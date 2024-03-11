package com.klikin.nearby_shops.presentation.usescases.shopsList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.klikin.nearby_shops.R
import com.klikin.nearby_shops.databinding.ShopListScreenBinding
import com.klikin.nearby_shops.presentation.usescases.shopsList.adapter.CategoryAdapter
import com.klikin.nearby_shops.presentation.usescases.shopsList.adapter.ShopAdapter
import kotlinx.coroutines.launch

class ShopListActivity : AppCompatActivity() {
    private lateinit var binding: ShopListScreenBinding
    private val viewModel: ShopListViewModel = ShopListViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ShopListScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val card1 = binding.cardShops
        val card1Title = binding.tvShopLength
        val card1Text = binding.tvShopText
        val card2 = binding.cardNearShops
        val card2Title = binding.tvShopLengthNear
        val card2Text = binding.tvShopNearText

        viewModel.loadShops()

        lifecycleScope.launch {
            viewModel.sate.collect { state ->
                viewModel.loadCategories()
                card1Title.text = state.shopList.size.toString()
                binding.recyclerViewCategories.adapter = CategoryAdapter(state.categoriesMap)
                binding.recyclerViewShops.adapter = ShopAdapter(state)
            }
        }

        card1.setOnClickListener {
            card1.background.setTint(ContextCompat.getColor(this, R.color.colorCardOnTap))
            card1Title.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextOnTap))
            card1Text.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextOnTap))

            card2.background.setTint(ContextCompat.getColor(this, R.color.colorCardNoTap))
            card2Title.setTextColor(ContextCompat.getColor(this, R.color.colorCardTitleNoTap))
            card2Text.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextNoTap))
        }
        card2.setOnClickListener {
            card1.background.setTint(ContextCompat.getColor(this, R.color.colorCardNoTap))
            card1Title.setTextColor(ContextCompat.getColor(this, R.color.colorCardTitleNoTap))
            card1Text.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextNoTap))

            card2.background.setTint(ContextCompat.getColor(this, R.color.colorCardOnTap))
            card2Title.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextOnTap))
            card2Text.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextOnTap))
        }
    }
}
