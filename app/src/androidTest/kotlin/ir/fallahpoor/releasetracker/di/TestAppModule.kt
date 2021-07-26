package ir.fallahpoor.releasetracker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.afollestad.rxkprefs.RxkPrefs
import com.afollestad.rxkprefs.rxkPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.storage.LocalStorage
import ir.fallahpoor.releasetracker.data.utils.storage.Storage
import ir.fallahpoor.releasetracker.testfakes.FakeLibraryRepository

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    fun provideRxkPrefs(sharedPreferences: SharedPreferences): RxkPrefs =
        rxkPrefs(sharedPreferences)

    @Provides
    fun provideLibraryRepository(fakeLibraryRepository: FakeLibraryRepository): LibraryRepository {
        return fakeLibraryRepository
    }

    @Provides
    fun provideStorage(localStorage: LocalStorage): Storage {
        return localStorage
    }

}