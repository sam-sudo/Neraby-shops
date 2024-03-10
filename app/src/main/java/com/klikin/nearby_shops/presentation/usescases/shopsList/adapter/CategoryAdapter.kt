package com.klikin.nearby_shops.presentation.usescases.shopsList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klikin.nearby_shops.databinding.ItemCategoryBinding

class CategoryAdapter(private val categories: List<String>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int,
    ) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount() = categories.size

    class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val rainbowColorsArgb =
            listOf(
                0xFFFF0000.toInt(), // Rojo
                0xFFFFA500.toInt(), // Naranja
                0xFF8B4513.toInt(), // Marrón
                0xFF008000.toInt(), // Verde
                0xFF0000FF.toInt(), // Azul
                0xFF4B0082.toInt(), // Índigo
                0xFFEE82EE.toInt(), // Violeta
            )

        fun bind(category: String) {
            binding.categoryText.text = category
            binding.categoryText.setTextColor(rainbowColorsArgb[adapterPosition % rainbowColorsArgb.size])
        }
    }
}
