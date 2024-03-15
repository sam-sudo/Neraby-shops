package com.klikin.nearby_shops.presentation.usescases.shopsList.adapter

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
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
        return ShopViewHolder(binding, categoriesMap)
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
        private var categoriesMap: Map<String, Int>,
    ) : RecyclerView.ViewHolder(
            binding.root,
        ) {
        private val locationService = LocationService()
        private var job: Job? = null

        fun bind(store: Store) {
            val colorBlanco = 0xFFFFFFFF.toInt()
            val backGroundColor: Int = categoriesMap[store.category.toString()] ?: colorBlanco

            binding.tvStoreName.text = store.name
            binding.tvStoreOpenTime.text =
                store?.openHoursLittleFormat()?.joinToString("\n") { it.trim() }
            binding.llCardHeader.setBackgroundColor(backGroundColor)
            if (!store.photo.isNullOrEmpty()) {
                val shimmer =
                    Shimmer.AlphaHighlightBuilder()
                        .setDuration(1500) // Duración del efecto de shimmer en milisegundos
                        .setBaseAlpha(0.7f) // Intensidad del efecto shimmer
                        .setHighlightAlpha(0.6f) // Intensidad del resplandor
                        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT) // Dirección del efecto shimmer
                        .build()

                val shimmerDrawable =
                    ShimmerDrawable().apply {
                        setShimmer(shimmer)
                    }

                Glide.with(binding.root.context)
                    .load(store.photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(RoundedCorners(20))
                    .placeholder(shimmerDrawable)
                    .error(R.drawable.no_image)
                    .into(binding.imgvShopImg)
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
