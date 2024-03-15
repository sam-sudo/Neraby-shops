package com.klikin.nearby_shops.di

import com.klikin.nearby_shops.data.remote.datasource.RemoteStoreDatasource
import com.klikin.nearby_shops.data.remote.datasource.StoreDatasource
import com.klikin.nearby_shops.data.service.StoreService
import com.klikin.nearby_shops.domain.repository.RemoteStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideStoreService(retrofit: Retrofit): StoreService = retrofit.create(StoreService::class.java)

    @Provides
    @Singleton
    fun provideStoreDataSource(): StoreDatasource {
        return RemoteStoreDatasource(provideStoreService(provideRetrofit()))
    }

    @Provides
    @Singleton
    fun provideRemoteStoreRepository(): RemoteStoreRepository {
        return RemoteStoreRepository(provideStoreDataSource())
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_URL_STORES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

private const val API_URL_STORES = "https://waylet-web-export.s3.eu-west-1.amazonaws.com/"
