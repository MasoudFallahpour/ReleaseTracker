package ir.fallahpoor.releasetracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [LibraryRepositoryModule::class]
)
object FakeRepositoryModule {

    @Provides
    fun provideLibraryRepository(): LibraryRepository = FakeLibraryRepository()

}