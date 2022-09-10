package ir.fallahpoor.releasetracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.fallahpoor.releasetracker.data.repository.storage.StorageRepository
import ir.fallahpoor.releasetracker.data.repository.storage.StorageRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object StorageRepositoryModule {

    @Provides
    fun provideStorageRepository(storageRepositoryImpl: StorageRepositoryImpl): StorageRepository =
        storageRepositoryImpl

}