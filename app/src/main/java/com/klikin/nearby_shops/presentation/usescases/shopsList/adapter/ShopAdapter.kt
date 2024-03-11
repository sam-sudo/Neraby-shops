package com.klikin.nearby_shops.presentation.usescases.shopsList.adapter

import android.net.Uri
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klikin.nearby_shops.R
import com.klikin.nearby_shops.data.mapper.openHoursLittleFormat
import com.klikin.nearby_shops.databinding.ItemStoreBinding
import com.klikin.nearby_shops.domain.model.Store
import com.klikin.nearby_shops.presentation.usescases.shopsList.ShopListViewState

class ShopAdapter(
    private val state: ShopListViewState,
) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {
    val stores = state.shopList

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
        holder.bind(stores[position])
    }

    override fun getItemCount() = stores.size

    class ShopViewHolder(
        private val binding: ItemStoreBinding,
        private val state: ShopListViewState,
    ) : RecyclerView.ViewHolder(
            binding.root,
        ) {
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

            try {
                val distance = distanceBetweenPoints(store.location!!, getLocation()).toString()
                val distanceText =
                    binding.root.context.getString(R.string.distance_format, distance)
                binding.tvDistance.text = distanceText
            } catch (e: Exception) {
                binding.tvDistance.text = "UNKNOWN m."
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

        fun getLocation(): List<Float> {
            return listOf(37.168476142495834F, -3.6040761719512906F)
        }
    }
}
