package com.klikin.nearby_shops.presentation.usescases.shopsList.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klikin.nearby_shops.R
import com.klikin.nearby_shops.data.mapper.openHoursLittleFormat
import com.klikin.nearby_shops.databinding.ItemStoreBinding
import com.klikin.nearby_shops.domain.model.Store
import com.klikin.nearby_shops.presentation.usescases.shopsList.ShopListViewState
import com.klikin.nearby_shops.presentation.utils.LocationHandler

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
                val distance =
                    LocationHandler.distanceBetweenPositionNowAndPoint(
                        (store.location?.get(0) ?: 0) as Double,
                        (store.location?.get(1) ?: 0) as Double,
                    )

                val distanceText = distance
                binding.tvDistance.text = String.format("%.2fm.", distanceText)
            } catch (e: Exception) {
                binding.tvDistance.text = "UNKNOWN m."
            }
        }
    }
}
