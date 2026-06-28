package com.track.staff.di

import com.track.data.repository.AndroidAuthRepository
import com.track.data.repository.AndroidFirestoreRepository
import com.track.data.repository.AuthRepository
import com.track.data.repository.FirestoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindAuthRepository(impl: AndroidAuthRepository): AuthRepository

    @Binds
    @Singleton
    fun bindFirestoreRepository(impl: AndroidFirestoreRepository): FirestoreRepository
}
