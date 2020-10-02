package ir.fallahpoor.releasetracker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.database.LibraryDatabase
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.repository.LibraryRepositoryImpl
import ir.fallahpoor.releasetracker.data.webservice.GithubWebservice
import ir.fallahpoor.releasetracker.data.webservice.WebserviceFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLibraryDatabase(@ApplicationContext context: Context): LibraryDatabase =
        Room.databaseBuilder(context, LibraryDatabase::class.java, "ReleaseTracker.db")
            .createFromAsset("database/libraries.db")
            .build()

    @Provides
    fun provideLibraryRepository(libraryRepositoryImpl: LibraryRepositoryImpl): LibraryRepository {
        return libraryRepositoryImpl
    }

    @Provides
    fun provideGithubWebservice(webserviceFactory: WebserviceFactory): GithubWebservice {
        return webserviceFactory.createGithubService(GithubWebservice::class.java)
    }

    @Provides
    fun provideLibraryDao(libraryDatabase: LibraryDatabase): LibraryDao {
        return libraryDatabase.libraryDao()
    }

}