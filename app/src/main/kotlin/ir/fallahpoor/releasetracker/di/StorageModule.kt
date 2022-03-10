package ir.fallahpoor.releasetracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.fallahpoor.releasetracker.data.utils.storage.LocalStorage
import ir.fallahpoor.releasetracker.data.utils.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    fun provideStorage(localStorage: LocalStorage): Storage = localStorage

    @Provides
    @Singleton
    fun provideDataStore(context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile("settings")
            }
        )

}