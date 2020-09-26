package ir.fallahpoor.releasetracker.addlibrary.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.fallahpoor.releasetracker.data.database.LibraryDatabase
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideLibraryDatabase(@ApplicationContext context: Context): LibraryDatabase =
        Room.databaseBuilder(
            context,
            LibraryDatabase::class.java, "ReleaseTracker.db"
        ).build()

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

}