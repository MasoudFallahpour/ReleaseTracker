package ir.fallahpoor.releasetracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import ir.fallahpoor.releasetracker.data.repository.library.LibraryRepository
import ir.fallahpoor.releasetracker.data.repository.library.LibraryRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object LibraryRepositoryModule {

    @Provides
    fun provideLibraryRepository(libraryRepositoryImpl: LibraryRepositoryImpl): LibraryRepository =
        libraryRepositoryImpl

}