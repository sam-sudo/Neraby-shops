package com.klikin.nearby_shops.data.remote.datasource

import com.klikin.nearby_shops.domain.model.apiModel.StoresApiModel
import retrofit2.Response

interface StoreDatasource {
    suspend fun getStores(): Response<StoresApiModel>
}
