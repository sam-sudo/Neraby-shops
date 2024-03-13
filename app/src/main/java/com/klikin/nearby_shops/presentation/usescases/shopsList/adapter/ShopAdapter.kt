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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ShopAdapter(
    private var storeList: List<Store>,
    private var categoriesMap: Map<String, Int>,
) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {
    fun updateData(
        newStoreList: List<Store>,
        newCategoriesMap: Map<String, Int>,
    ) {
        storeList = newStoreList
        categoriesMap = newCategoriesMap
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShopViewHolder {
        val binding =
            ItemStoreBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopViewHolder(binding, storeList, categoriesMap)
    }

    override fun onBindViewHolder(
        holder: ShopViewHolder,
        position: Int,
    ) {
        holder.bind(storeList[position])
    }

    override fun getItemCount() = storeList.size

    class ShopViewHolder(
        private val binding: ItemStoreBinding,
        private var storeList: List<Store>,
        private var categoriesMap: Map<String, Int>,
    ) : RecyclerView.ViewHolder(
            binding.root,
        ) {
        private val locationService = LocationService()
        private var job: Job? = null

        fun bind(store: Store) {
            val colorBlanco = 0xFFFFFFFF.toInt()
            val backGroundColor: Int = categoriesMap[store.category] ?: colorBlanco

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
                        if (!store.location.isNullOrEmpty()) {
                            val storePosition =
                                android.location.Location("provider").apply {
                                    latitude = (store.location?.get(0) ?: 0.0)
                                    longitude = (store.location?.get(1) ?: 0.0)
                                }

                            val distanceInMeters = actualLocation?.distanceTo(storePosition)

                            if (distanceInMeters != null) {
                                if (distanceInMeters > 10000.0F) {
                                    Handler(Looper.getMainLooper()).post {
                                        Handler(Looper.getMainLooper()).post {
                                            binding.tvDistance.setText(R.string.more_than_10_km)
                                        }
                                    }
                                }
                                Handler(Looper.getMainLooper()).post {
                                    binding.tvDistance.text =
                                        String.format("%.2fm.", distanceInMeters)
                                }
                            } else {
                                Handler(Looper.getMainLooper()).post {
                                    binding.tvDistance.setText(R.string.unknown_distance)
                                }
                            }
                        } else {
                            Handler(Looper.getMainLooper()).post {
                                binding.tvDistance.setText(R.string.unknown_distance)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("TAG", "bind: ${e.message}")
                    }
                }
        }
    }
}
