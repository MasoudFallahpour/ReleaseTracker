package ir.fallahpoor.releasetracker.di

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.fallahpoor.releasetracker.data.Database
import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.database.LibraryDaoImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): Database {
        val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, context, "ReleaseTracker.db")
        return Database(driver)
    }

    @Provides
    @Singleton
    fun provideLibraryDao(libraryDaoImpl: LibraryDaoImpl): LibraryDao = libraryDaoImpl

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

}