package ir.fallahpoor.releasetracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.repository.LibraryRepositoryImpl

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    fun provideLibraryRepository(libraryRepositoryImpl: LibraryRepositoryImpl): LibraryRepository {
        return libraryRepositoryImpl
    }

}