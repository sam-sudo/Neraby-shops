package com.klikin.nearby_shops.presentation.usescases.shopsList

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.klikin.nearby_shops.R
import com.klikin.nearby_shops.databinding.ShopListScreenBinding
import com.klikin.nearby_shops.presentation.usescases.shopsList.adapter.CategoryAdapter
import com.klikin.nearby_shops.presentation.usescases.shopsList.adapter.ShopAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopListActivity : AppCompatActivity() {
    private lateinit var binding: ShopListScreenBinding
    private lateinit var viewModel: ShopListViewModel
    private var card1Selected = true
    private var card2Selected = false
    var lastSelectedItemPosition = RecyclerView.NO_POSITION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ShopListScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this).get(ShopListViewModel::class.java)

        val llShopListScreen = binding.llShopListScreen
        val llSnoPermissionsScreen = binding.llNoPermissionsGranted
        val card1 = binding.cardShops
        val card1Title = binding.tvShopLength
        val card1Text = binding.tvShopText
        val card2 = binding.cardNearShops
        val card2Title = binding.tvShopLengthNear
        val card2Text = binding.tvShopNearText
        val textViewNoShopS = binding.tvEmptyList

        val shopsRecyclerView = binding.recyclerViewShops
        val categoriesRecyclerView = binding.recyclerViewCategories
        val categoryAdapter = CategoryAdapter(mutableMapOf())
        val shopAdapter = ShopAdapter(listOf(), mapOf())

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

            categoryAdapter.setOnItemClickListener(
                object : CategoryAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        categoryAdapter.setSelectedItem(position)
                        val selectedCategory =
                            viewModel.sate.value.categoriesMap.keys.toList()[position]
                        Log.d("TAG", "ITEM TOUCHED -> $selectedCategory")
                        Log.d("TAG", "lastSelectedItemPosition -> $lastSelectedItemPosition")
                        Log.d("TAG", "lastSelectedItemPosition -> $position")

                        if (card1Selected) {
                            if (position != lastSelectedItemPosition) {
                                viewModel.loadShopsByCategory(
                                    this@ShopListActivity,
                                    selectedCategory,
                                )
                                lastSelectedItemPosition = position
                            } else {
                                viewModel.loadAllShops()
                                lastSelectedItemPosition = -1
                            }
                        } else {
                            if (position != lastSelectedItemPosition) {
                                viewModel.loadShopsByCategoryAndLessThan1km(
                                    this@ShopListActivity,
                                    selectedCategory,
                                )
                                lastSelectedItemPosition = position
                            } else {
                                viewModel.loadsShopsLessThan1Kilometre()
                                lastSelectedItemPosition = -1
                            }
                        }
                    }
                },
            )

            categoriesRecyclerView.adapter = categoryAdapter
            shopsRecyclerView.adapter = shopAdapter

            // Cards
            card1.setOnClickListener {
                categoryAdapter.clearSelection()
                lastSelectedItemPosition = -1
                card1Selected = true
                card2Selected = false
                card1.background.setTint(ContextCompat.getColor(this, R.color.colorCardOnTap))
                card1Title.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextOnTap))
                card1Text.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextOnTap))

                card2.background.setTint(ContextCompat.getColor(this, R.color.colorCardNoTap))
                card2Title.setTextColor(ContextCompat.getColor(this, R.color.colorCardTitleNoTap))
                card2Text.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextNoTap))

                // order items
                viewModel.loadAllShops()
            }
            card2.setOnClickListener {
                categoryAdapter.clearSelection()
                lastSelectedItemPosition = -1

                card1Selected = false
                card2Selected = true
                card1.background.setTint(ContextCompat.getColor(this, R.color.colorCardNoTap))
                card1Title.setTextColor(ContextCompat.getColor(this, R.color.colorCardTitleNoTap))
                card1Text.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextNoTap))

                card2.background.setTint(ContextCompat.getColor(this, R.color.colorCardOnTap))
                card2Title.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextOnTap))
                card2Text.setTextColor(ContextCompat.getColor(this, R.color.colorCardTextOnTap))

                // order items
                viewModel.loadsShopsLessThan1Kilometre()
            }

            lifecycleScope.launch {
                viewModel.sate.collect { state ->

                    if (state.isLoading) {
                        binding.shimmerView.visibility = View.VISIBLE
                        binding.shimmerCategoryView.visibility = View.VISIBLE
                        binding.shimmerShopsCards.startShimmer()
                        binding.shimmerView.startShimmer()
                        binding.shimmerCategoryView.startShimmer()
                    } else {
                        binding.shimmerView.visibility = View.GONE
                        binding.shimmerCategoryView.visibility = View.GONE
                        binding.shimmerShopsCards.visibility = View.GONE
                        binding.shimmerShopsCards.stopShimmer()
                        binding.shimmerView.stopShimmer()
                        binding.shimmerCategoryView.stopShimmer()

                        Log.d("tag", "card1Selected ->> $card1Selected")
                        card1Title.text = viewModel.storesList.size.toString()
                        card2Title.text = viewModel.storesListLessThan1km.size.toString()
                        if (state.shopList.isNullOrEmpty()) {
                            textViewNoShopS.visibility = View.VISIBLE
                            binding.llShopList.visibility = View.GONE
                        } else {
                            textViewNoShopS.visibility = View.GONE
                            binding.llShopList.visibility = View.VISIBLE
                            shopAdapter.updateData(state.shopList, state.categoriesMap)
                            categoryAdapter.updateData(state.categoriesMap)
                        }
                    }
                }
            }
        }
    }
}
