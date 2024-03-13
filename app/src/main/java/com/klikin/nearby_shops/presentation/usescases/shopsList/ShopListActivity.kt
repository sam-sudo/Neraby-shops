package com.klikin.nearby_shops.presentation.usescases.shopsList

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
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

        val llShopListScreen = binding.llShopListScreen
        val llSnoPermissionsScreen = binding.llNoPermissionsGranted
        val card1 = binding.cardShops
        val card1Title = binding.tvShopLength
        val card1Text = binding.tvShopText
        val card2 = binding.cardNearShops
        val card2Title = binding.tvShopLengthNear
        val card2Text = binding.tvShopNearText

        val categoriesRecyclerView = binding.recyclerViewCategories
        var adapter = CategoryAdapter(mutableMapOf())
        val shopAdapter = ShopAdapter(ShopListViewState())

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            llShopListScreen.visibility = View.GONE
            llSnoPermissionsScreen.visibility = View.VISIBLE
        } else {
            llShopListScreen.visibility = View.VISIBLE
            llSnoPermissionsScreen.visibility = View.GONE

            viewModel.loadShops(this)

            adapter.setOnItemClickListener(
                object : CategoryAdapter.onItemClickListener {
                    var lastSelectedItemPosition = RecyclerView.NO_POSITION

                    override fun onItemClick(position: Int) {
                        adapter.setSelectedItem(position)
                        val selectedCategory = viewModel.sate.value.categoriesMap.keys.toList()[position]
                        Log.d("TAG", "ITEM TOUCHED -> $selectedCategory")

                        if (position != lastSelectedItemPosition) {
                            viewModel.loadShopsByCategory(this@ShopListActivity, selectedCategory)
                        } else {
                            viewModel.loadShops(this@ShopListActivity)
                        }

                        lastSelectedItemPosition = position
                    }
                },
            )

            categoriesRecyclerView.adapter = adapter
            binding.recyclerViewShops.adapter = shopAdapter

            // Cards
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

            lifecycleScope.launch {
                viewModel.sate.collect { state ->
                    card1Title.text = state.shopList.size.toString()
                    shopAdapter.updateData(state)
                    adapter.updateData(state.categoriesMap)
                }
            }
        }
    }
}
