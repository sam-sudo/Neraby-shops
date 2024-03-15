package com.klikin.nearby_shops.data.service

import com.klikin.nearby_shops.domain.model.apiModel.StoresApiModel
import retrofit2.Response
import retrofit2.http.GET

interface StoreService {
    @GET("/commerces.json")
    suspend fun getStores(): Response<StoresApiModel>
}
