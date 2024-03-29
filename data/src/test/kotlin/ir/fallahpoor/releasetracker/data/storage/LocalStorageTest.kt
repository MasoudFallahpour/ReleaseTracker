package ir.fallahpoor.releasetracker.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.storage.LocalStorage.Companion.KEY_LAST_UPDATE_CHECK
import ir.fallahpoor.releasetracker.data.storage.LocalStorage.Companion.KEY_NIGHT_MODE
import ir.fallahpoor.releasetracker.data.storage.LocalStorage.Companion.KEY_SORT_ORDER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class LocalStorageTest {

    private lateinit var localStorage: LocalStorage
    private lateinit var dataStoreTestScope: TestScope
    private lateinit var dataStore: DataStore<Preferences>
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun runBeforeEachTest() {
        val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        dataStoreTestScope = TestScope(testDispatcher + Job())
        dataStore = PreferenceDataStoreFactory.create(scope = dataStoreTestScope) {
            context.preferencesDataStoreFile(
                "test-preferences-file"
            )
        }
        localStorage = LocalStorage(dataStore)
    }

    @After
    fun runAfterEachTest() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getNightMode() should return the saved night mode`() = runTest {

        // Given
        val expectedNightMode = NightMode.OFF
        putString(KEY_NIGHT_MODE, expectedNightMode.name)

        // When
        val actualNightMode: NightMode = localStorage.getNightMode()

        // Then
        Truth.assertThat(actualNightMode).isEqualTo(expectedNightMode)

    }

    @Test
    fun `getNightMode() should return the default night mode when no night mode is set`() {

        // Given

        // When
        val actualNightMode: NightMode = localStorage.getNightMode()

        // Then
        Truth.assertThat(actualNightMode).isEqualTo(NightMode.AUTO)

    }

    @Test
    fun test_setNightMode() = runTest {

        // Given
        val expectedNightMode = NightMode.ON

        // When
        localStorage.setNightMode(expectedNightMode)

        // Then
        val actualNightMode = NightMode.valueOf(getString(KEY_NIGHT_MODE) ?: "")
        Truth.assertThat(actualNightMode).isEqualTo(expectedNightMode)

    }

    @Test
    fun test_setSortOrder() = runTest {

        // Given
        val expectedSortOrder = SortOrder.Z_TO_A

        // When
        localStorage.setSortOrder(expectedSortOrder)

        // Then
        val actualSortOrder = SortOrder.valueOf(getString(KEY_SORT_ORDER) ?: "")
        Truth.assertThat(actualSortOrder).isEqualTo(expectedSortOrder)

    }

    @Test
    fun test_getSortOrder() = runTest {

        // Given
        val expectedSortOrder = SortOrder.PINNED_FIRST
        putString(KEY_SORT_ORDER, expectedSortOrder.name)

        // When
        val actualSortOrder = localStorage.getSortOrder()

        // Then
        Truth.assertThat(actualSortOrder).isEqualTo(expectedSortOrder)

    }

    @Test
    fun test_setLastUpdateCheck() = runTest {

        // Given
        val expectedLastUpdateCheckDate = "15:30, March"

        // When
        localStorage.setLastUpdateCheck(expectedLastUpdateCheckDate)

        // Then
        val actualLastUpdateCheckDate = getString(KEY_LAST_UPDATE_CHECK)
        Truth.assertThat(actualLastUpdateCheckDate).isEqualTo(expectedLastUpdateCheckDate)

    }

    private suspend fun putString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[prefKey] = value
        }
    }

    private suspend fun getString(key: String): String? {
        val prefKey = stringPreferencesKey(key)
        val flow: Flow<String?> = dataStore.data
            .map { preferences ->
                preferences[prefKey]
            }
        return flow.first()
    }

}