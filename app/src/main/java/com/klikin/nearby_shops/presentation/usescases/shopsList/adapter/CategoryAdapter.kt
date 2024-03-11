package com.klikin.nearby_shops.presentation.usescases.shopsList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klikin.nearby_shops.databinding.ItemCategoryBinding

class CategoryAdapter(private val categories: MutableMap<String, Int>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    val categoriesList: List<Map.Entry<String, Int>> = categories.entries.toList()

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
        val category: Map.Entry<String, Int> = categoriesList[position]
        holder.bind(category)
    }

    override fun getItemCount() = categories.size

    class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Map.Entry<String, Int>) {
            binding.categoryText.text = category.key
            binding.categoryText.setTextColor(category.value)
        }
    }
}
