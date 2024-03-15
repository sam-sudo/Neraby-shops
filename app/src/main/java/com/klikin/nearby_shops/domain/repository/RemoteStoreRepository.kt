package com.klikin.nearby_shops.domain.repository

import com.klikin.nearby_shops.data.remote.datasource.StoreDatasource
import com.klikin.nearby_shops.domain.model.apiModel.StoresApiModel
import retrofit2.Response
import javax.inject.Inject

class RemoteStoreRepository
    @Inject
    constructor(private val storeDatasource: StoreDatasource) : StoreRepository {
        override suspend fun getStores(): Response<StoresApiModel> {
            return storeDatasource.getStores()
        }
    }
