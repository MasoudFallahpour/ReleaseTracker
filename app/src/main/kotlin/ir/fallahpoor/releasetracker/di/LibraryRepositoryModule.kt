package ir.fallahpoor.releasetracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.fallahpoor.releasetracker.BuildConfig
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.repository.LibraryRepositoryImpl
import ir.fallahpoor.releasetracker.data.webservice.GithubWebservice
import ir.fallahpoor.releasetracker.data.webservice.WebserviceFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LibraryRepositoryModule {

    @Provides
    fun provideLibraryRepository(libraryRepositoryImpl: LibraryRepositoryImpl): LibraryRepository =
        libraryRepositoryImpl

    @Provides
    @Singleton
    fun provideGithubWebservice(webserviceFactory: WebserviceFactory): GithubWebservice =
        webserviceFactory.createGithubService(
            serviceClass = GithubWebservice::class.java,
            isDebugBuild = BuildConfig.DEBUG
        )

}