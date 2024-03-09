package com.klikin.nearby_shops.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.klikin.nearby_shops.databinding.ActivityMainBinding
import com.klikin.nearby_shops.presentation.usescases.shopsList.ShopListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        screenSplash.setKeepOnScreenCondition { true }

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            openShopList()
        }
    }

    private fun openShopList() {
        val intent = Intent(this, ShopListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
