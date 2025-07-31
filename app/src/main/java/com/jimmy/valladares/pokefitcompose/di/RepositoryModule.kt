package com.jimmy.valladares.pokefitcompose.di

import com.jimmy.valladares.pokefitcompose.data.local.UserPreferences
import com.jimmy.valladares.pokefitcompose.data.repository.MockUserRepository
import com.jimmy.valladares.pokefitcompose.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideUserRepository(userPreferences: UserPreferences): UserRepository {
        return MockUserRepository(userPreferences)
    }
}
