package com.klikin.nearby_shops.data.remote.datasource

import com.klikin.nearby_shops.data.service.StoreService
import com.klikin.nearby_shops.domain.model.apiModel.StoresApiModel
import retrofit2.Response
import javax.inject.Inject

class RemoteStoreDatasource
    @Inject
    constructor(private val storeService: StoreService) :
    StoreDatasource {
        override suspend fun getStores(): Response<StoresApiModel> {
            return storeService.getStores()
        }
    }
