package com.foodexpress.di

import com.foodexpress.data.repository.AuthRepository
import com.foodexpress.data.repository.AuthRepositoryImpl
import com.foodexpress.data.repository.OrderRepository
import com.foodexpress.data.repository.OrderRepositoryImpl
import com.foodexpress.data.repository.RestaurantRepository
import com.foodexpress.data.repository.RestaurantRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindRestaurantRepository(impl: RestaurantRepositoryImpl): RestaurantRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository
}
