package com.klikin.nearby_shops.presentation.usescases.shopsList.detail

import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.google.gson.Gson
import com.klikin.nearby_shops.R
import com.klikin.nearby_shops.databinding.StoreDetailLayoutBinding
import com.klikin.nearby_shops.domain.model.Store
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions.Companion.cameraAnimatorOptions
import com.mapbox.maps.plugin.animation.camera

class ShopDetailActivity : AppCompatActivity() {
    private lateinit var binding: StoreDetailLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StoreDetailLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storeJson = intent.getStringExtra("storeJson")
        val store = Gson().fromJson(storeJson, Store::class.java)

        val btBack = binding.btnBack
        btBack.setOnClickListener {
            onBackPressed()
        }

        val tvName = binding.tvShopName
        tvName.setText(store.name)

        val imgHeader = binding.imgvShopImg
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
                .transform(RoundedCorners(40))
                .placeholder(shimmerDrawable)
                .centerCrop()
                .error(R.drawable.no_image)
                .into(imgHeader)
        } else {
            imgHeader.setImageResource(R.drawable.no_image)
        }

        val latitude = store.location?.get(1) ?: 0.0
        val longitude = store.location?.get(0) ?: 0.0
        val mapView = binding.mapView
        val mapboxMap = mapView.mapboxMap
        val cameraTarget =
            cameraOptions {
                center(Point.fromLngLat(longitude, latitude))
                zoom(1.0)
            }

        mapboxMap.setCamera(cameraTarget)
        mapboxMap.loadStyle(
            Style.STANDARD,
        ) {
            mapView.camera.apply {
                /*val bearing =
                    createBearingAnimator(cameraAnimatorOptions(-45.0)) {
                        duration = 4000
                        interpolator = AccelerateDecelerateInterpolator()
                    }*/
                val zoom =
                    createZoomAnimator(
                        cameraAnimatorOptions(14.0) {
                            startValue(1.0)
                        },
                    ) {
                        duration = 4000
                        interpolator = AccelerateDecelerateInterpolator()
                    }
                /*val pitch =
                    createPitchAnimator(
                        cameraAnimatorOptions(55.0) {
                            startValue(0.0)
                        },
                    ) {
                        duration = 4000
                        interpolator = AccelerateDecelerateInterpolator()
                    }*/
                playAnimatorsSequentially(zoom)
            }
        }

        val btnGoToMap = binding.tvGotToMap
        btnGoToMap.setOnClickListener {
            val targetLocation =
                cameraOptions {
                    center(Point.fromLngLat(longitude, latitude))
                }
            mapboxMap.setCamera(targetLocation)
            mapboxMap.loadStyle(
                Style.STANDARD,
            ) {
                mapView.camera.apply {
                    val zoom =
                        createZoomAnimator(
                            cameraAnimatorOptions(14.0) {
                                // startValue(3.0)
                            },
                        ) {
                            duration = 4000
                            interpolator = AccelerateDecelerateInterpolator()
                        }
                    playAnimatorsSequentially(zoom)
                }
            }
        }

        val tvAboutShop = binding.tvAboutShop
        val tvCountry = binding.tvCountry
        val tvStreet = binding.tvStreet
        val tvCity = binding.tvCity
        val tvState = binding.tvState
        val tvZip = binding.tvZip
        tvAboutShop.text = store.openingHours
        tvCountry.append(store.address?.country ?: "Unknown")
        tvStreet.append(store.address?.street ?: "Unknown")
        tvCity.append(store.address?.city ?: "Unknown")
        tvState.append(store.address?.state ?: "Unknown")
        tvZip.append(store.address?.zip ?: "Unknown")
    }
}
