package com.klikin.nearby_shops.presentation.usescases.shopsList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.klikin.nearby_shops.databinding.ShopListScreenBinding

class ShopListActivity : AppCompatActivity() {
    private lateinit var binding: ShopListScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ShopListScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
