package com.klikin.nearby_shops.presentation.usescases.shopsList.adapter

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klikin.nearby_shops.R
import com.klikin.nearby_shops.data.mapper.openHoursLittleFormat
import com.klikin.nearby_shops.databinding.ItemStoreBinding
import com.klikin.nearby_shops.domain.model.Store
import com.klikin.nearby_shops.framework.LocationService
import com.klikin.nearby_shops.presentation.usescases.shopsList.ShopListViewState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ShopAdapter(
    private var state: ShopListViewState,
) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {
    fun updateData(newState: ShopListViewState) {
        state = newState
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShopViewHolder {
        val binding =
            ItemStoreBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopViewHolder(binding, state)
    }

    override fun onBindViewHolder(
        holder: ShopViewHolder,
        position: Int,
    ) {
        holder.bind(state.shopList[position])
    }

    override fun getItemCount() = state.shopList.size

    class ShopViewHolder(
        private val binding: ItemStoreBinding,
        private val state: ShopListViewState,
    ) : RecyclerView.ViewHolder(
            binding.root,
        ) {
        private val locationService = LocationService()
        private var job: Job? = null

        fun bind(store: Store) {
            val colorBlanco = 0xFFFFFFFF.toInt()
            val backGroundColor: Int = state.categoriesMap[store.category] ?: colorBlanco

            binding.tvStoreName.text = store.name
            binding.tvStoreOpenTime.text =
                store?.openHoursLittleFormat()?.joinToString("\n") { it.trim() }
            binding.llCardHeader.setBackgroundColor(backGroundColor)
            if (!store.photo.isNullOrEmpty()) {
                binding.imgvShopImg.setImageURI(Uri.parse(store.photo))
            } else {
                binding.imgvShopImg.setImageResource(R.drawable.no_image)
            }

            job =
                GlobalScope.launch {
                    try {
                        val actualLocation = locationService.getUserLocation(binding.root.context)
                        val storePosition =
                            android.location.Location("provider").apply {
                                latitude = (store.location?.get(0) ?: 0.0)
                                longitude = (store.location?.get(1) ?: 0.0)
                            }

                        val distanceInMeters = actualLocation?.distanceTo(storePosition)

                        // Update UI on the main thread
                        Handler(Looper.getMainLooper()).post {
                            binding.tvDistance.text = String.format("%.2fm.", distanceInMeters)
                        }
                    } catch (e: Exception) {
                        Log.e("TAG", "bind: ${e.message}")
                    }
                }
        }
    }
}
