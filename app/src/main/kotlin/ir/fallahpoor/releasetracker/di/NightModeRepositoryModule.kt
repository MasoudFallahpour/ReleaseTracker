package ir.fallahpoor.releasetracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.fallahpoor.releasetracker.data.repository.NightModeRepository
import ir.fallahpoor.releasetracker.data.repository.NightModeRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object NightModeRepositoryModule {

    @Provides
    fun provideNightModeRepository(nightModeRepositoryImpl: NightModeRepositoryImpl): NightModeRepository =
        nightModeRepositoryImpl

}