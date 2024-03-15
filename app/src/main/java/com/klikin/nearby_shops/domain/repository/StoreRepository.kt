package com.klikin.nearby_shops.domain.repository

import com.klikin.nearby_shops.domain.model.apiModel.StoresApiModel
import retrofit2.Response

interface StoreRepository {
    suspend fun getStores(): Response<StoresApiModel>
}
