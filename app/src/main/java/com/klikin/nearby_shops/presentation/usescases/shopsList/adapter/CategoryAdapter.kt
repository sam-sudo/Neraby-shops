package com.klikin.nearby_shops.presentation.usescases.shopsList.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klikin.nearby_shops.R
import com.klikin.nearby_shops.databinding.ItemCategoryBinding

class CategoryAdapter(private val categories: MutableMap<String, Int>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private lateinit var mListener: onItemClickListener
    private var selectedItemIndex: Int = RecyclerView.NO_POSITION

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    fun updateData(newCategories: MutableMap<String, Int>) {
        categories.clear()
        categories.putAll(newCategories)
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedItemIndex = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding, mListener)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int,
    ) {
        val category: Map.Entry<String, Int> = categories.entries.elementAt(position)
        holder.bind(category, position == selectedItemIndex)
    }

    override fun getItemCount() = categories.size

    fun setSelectedItem(position: Int) {
        if (selectedItemIndex == position) {
            // Si el usuario toca el mismo elemento dos veces seguidas, deseleccionarlo
            selectedItemIndex = RecyclerView.NO_POSITION
        } else {
            selectedItemIndex = position
        }
        notifyDataSetChanged()
    }

    class CategoryViewHolder(
        private val binding: ItemCategoryBinding,
        listener: onItemClickListener,
    ) : RecyclerView.ViewHolder(
            binding.root,
        ) {
        fun bind(
            category: Map.Entry<String, Int>,
            isSelected: Boolean,
        ) {
            binding.categoryText.text = category.key
            binding.categoryText.setTextColor(category.value)

            if (isSelected) {
                binding.cardItemContent.setBackgroundResource(R.color.backgroundOnTapCategory)
            } else {
                // Mantener color blanco para los elementos no seleccionados
                binding.cardItemContent.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        }

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}
