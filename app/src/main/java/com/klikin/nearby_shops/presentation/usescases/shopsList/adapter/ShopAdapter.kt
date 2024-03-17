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
import com.klikin.nearby_shops.databinding.ItemStoreBinding
import com.klikin.nearby_shops.domain.mapper.handlerMetresText
import com.klikin.nearby_shops.domain.mapper.openHoursLittleFormat
import com.klikin.nearby_shops.domain.model.Store
import com.klikin.nearby_shops.domain.model.enums.Categories
import com.klikin.nearby_shops.framework.LocationService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ShopAdapter(
    private var storeList: List<Store>,
    private var categoriesMap: Map<String, Int>,
) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {
    private lateinit var mListener: ShopAdapter.onItemClickListener

    interface onItemClickListener {
        fun onItemClick(store: Store)
    }

    fun setOnItemClickListener(listener: ShopAdapter.onItemClickListener) {
        mListener = listener
    }

    fun updateData(
        newStoreList: List<Store>,
        newCategoriesMap: Map<String, Int>,
    ) {
        storeList = emptyList()
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
        return ShopViewHolder(binding, categoriesMap, mListener)
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
        private val listener: ShopAdapter.onItemClickListener,
    ) : RecyclerView.ViewHolder(
            binding.root,
        ) {
        private val locationService = LocationService()
        private var job: Job? = null

        fun bind(store: Store) {
            val MAX_DISTANCE = 50000.0
            val colorBlanco = 0xFFFFFFFF.toInt()
            val backGroundColor: Int = categoriesMap[store.category.toString()] ?: colorBlanco

            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(store)
                }
            }

            binding.tvStoreName.text = store.name
            binding.tvStoreOpenTime.text =
                store?.openHoursLittleFormat()?.joinToString("\n") { it.trim() }
            binding.llCardHeader.setBackgroundColor(backGroundColor)

            val categoryIconMap =
                mapOf(
                    Categories.FOOD to R.drawable.food_ico,
                    Categories.GAS_STATION to R.drawable.gas_station_ico,
                    Categories.SHOPPING to R.drawable.shopping_ico, // Agrega los demás íconos aquí
                    Categories.BEAUTY to R.drawable.beauty_ico,
                    Categories.LEISURE to R.drawable.game_ico,
                    Categories.ELECTRIC_STATION to R.drawable.electric_station_ico,
                    Categories.DIRECT_SALES to R.drawable.gas_station_ico,
                    Categories.UNKNOWN to R.drawable.unknow_ico,
                )

            val iconResource = categoryIconMap.getOrElse(store.category) { R.mipmap.fuel_ico }
            binding.imvCategoryIcon.setBackgroundResource(iconResource)

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
                            val location = store.location
                            val latitude = location[1]
                            val longitude = location[0]
                            val userLatitude = actualLocation?.latitude?.toDouble() ?: 0.0
                            val userLongitude = actualLocation?.longitude?.toDouble() ?: 0.0
                            val distanceInMeters =
                                locationService.calculateDistanceInMeters(
                                    userLatitude,
                                    userLongitude,
                                    latitude,
                                    longitude,
                                )

                            if (distanceInMeters != null) {
                                Log.d("TAG", "distanceInMeters in adapter: $distanceInMeters ")
                                if (distanceInMeters > MAX_DISTANCE) {
                                    Handler(Looper.getMainLooper()).post {
                                        Handler(Looper.getMainLooper()).post {
                                            binding.tvDistance.setText(R.string.more_than_50_km)
                                        }
                                    }
                                }
                                Handler(Looper.getMainLooper()).post {
                                    binding.tvDistance.text = distanceInMeters.handlerMetresText()
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
