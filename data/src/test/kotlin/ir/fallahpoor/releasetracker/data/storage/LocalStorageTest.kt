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
import ir.fallahpoor.releasetracker.data.MainDispatcherRule
import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.storage.LocalStorage.Companion.KEY_LAST_UPDATE_CHECK
import ir.fallahpoor.releasetracker.data.storage.LocalStorage.Companion.KEY_NIGHT_MODE
import ir.fallahpoor.releasetracker.data.storage.LocalStorage.Companion.KEY_SORT_ORDER
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class LocalStorageTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var localStorage: LocalStorage
    private lateinit var dataStoreTestScope: TestScope
    private lateinit var dataStore: DataStore<Preferences>
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun runBeforeEachTest() {
        dataStoreTestScope = TestScope(mainDispatcherRule.testDispatcher + Job())
        dataStore = PreferenceDataStoreFactory.create(scope = dataStoreTestScope) {
            context.preferencesDataStoreFile(
                "test-preferences-file"
            )
        }
        localStorage = LocalStorage(dataStore)
    }

    @Test
    fun `sort order is saved correctly`() = runTest {

        // Given
        val expectedSortOrder = SortOrder.Z_TO_A

        // When
        localStorage.saveSortOrder(expectedSortOrder)

        // Then
        val actualSortOrder = SortOrder.valueOf(getString(KEY_SORT_ORDER) ?: "")
        Truth.assertThat(actualSortOrder).isEqualTo(expectedSortOrder)

    }

    @Test
    fun `saved sort order is returned correctly`() = runTest {

        // Given
        val expectedSortOrder = SortOrder.PINNED_FIRST
        putString(KEY_SORT_ORDER, expectedSortOrder.name)

        // When
        val actualSortOrder = localStorage.getSortOrder()

        // Then
        Truth.assertThat(actualSortOrder).isEqualTo(expectedSortOrder)

    }

    @Test
    fun `default sort order is returned when there is no saved sort order`() = runTest {

        // Given

        // When
        val actualSortOrder = localStorage.getSortOrder()

        // Then
        Truth.assertThat(actualSortOrder).isEqualTo(LocalStorage.DEFAULT_SORT_ORDER)

    }

    @Test
    fun `sort order flow emits the saved sort order`() = runTest {

        // Given
        val actualSortOrders = mutableListOf<SortOrder>()
        val job = launch(UnconfinedTestDispatcher()) {
            localStorage.getSortOrderAsFlow().toList(actualSortOrders)
        }

        // When
        putString(KEY_SORT_ORDER, SortOrder.Z_TO_A.name)
        putString(KEY_SORT_ORDER, SortOrder.PINNED_FIRST.name)

        // Then
        val expectedSortOrders = listOf(
            LocalStorage.DEFAULT_SORT_ORDER, SortOrder.Z_TO_A, SortOrder.PINNED_FIRST
        )
        Truth.assertThat(actualSortOrders).isEqualTo(expectedSortOrders)

        job.cancel()

    }

    @Test
    fun `night mode is saved correctly`() = runTest {

        // Given
        val expectedNightMode = NightMode.ON

        // When
        localStorage.saveNightMode(expectedNightMode)

        // Then
        val actualNightMode = NightMode.valueOf(getString(KEY_NIGHT_MODE) ?: "")
        Truth.assertThat(actualNightMode).isEqualTo(expectedNightMode)

    }

    @Test
    fun `saved night mode is returned correctly`() = runTest {

        // Given
        val expectedNightMode = NightMode.OFF
        putString(KEY_NIGHT_MODE, expectedNightMode.name)

        // When
        val actualNightMode: NightMode = localStorage.getNightMode()

        // Then
        Truth.assertThat(actualNightMode).isEqualTo(expectedNightMode)

    }

    @Test
    fun `default night mode is returned when there is no saved night mode`() {

        // Given

        // When
        val actualNightMode: NightMode = localStorage.getNightMode()

        // Then
        Truth.assertThat(actualNightMode).isEqualTo(LocalStorage.DEFAULT_NIGHT_MODE)

    }

    @Test
    fun `night mode flow emits the saved night mode`() = runTest {
        // Given
        val actualNightModes = mutableListOf<NightMode>()
        val job = launch(UnconfinedTestDispatcher()) {
            localStorage.getNightModeAsFlow().toList(actualNightModes)
        }

        // When
        putString(KEY_NIGHT_MODE, NightMode.OFF.name)
        putString(KEY_NIGHT_MODE, NightMode.ON.name)

        // Then
        val expectedNightModes = listOf(
            LocalStorage.DEFAULT_NIGHT_MODE, NightMode.OFF, NightMode.ON
        )
        Truth.assertThat(actualNightModes).isEqualTo(expectedNightModes)

        job.cancel()

    }

    @Test
    fun `last update check date is saved correctly`() = runTest {

        // Given
        val expectedLastUpdateCheckDate = "15:30, March 2022"

        // When
        localStorage.saveLastUpdateCheck(expectedLastUpdateCheckDate)

        // Then
        val actualLastUpdateCheckDate = getString(KEY_LAST_UPDATE_CHECK)
        Truth.assertThat(actualLastUpdateCheckDate).isEqualTo(expectedLastUpdateCheckDate)

    }

    @Test
    fun `last update check date flow emits the saved last update check date`() = runTest {
        // Given
        val actualUpdateCheckDates = mutableListOf<String>()
        val job = launch(UnconfinedTestDispatcher()) {
            localStorage.getLastUpdateCheckAsFlow().toList(actualUpdateCheckDates)
        }

        // When
        putString(KEY_LAST_UPDATE_CHECK, "12:44, January 5 2023")
        putString(KEY_LAST_UPDATE_CHECK, "18:20, January 5 2023")

        // Then
        val expectedUpdateCheckDates = listOf(
            LocalStorage.DEFAULT_LAST_UPDATE_CHECK, "12:44, January 5 2023", "18:20, January 5 2023"
        )
        Truth.assertThat(actualUpdateCheckDates).isEqualTo(expectedUpdateCheckDates)

        job.cancel()

    }

    private suspend fun putString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[prefKey] = value
        }
    }

    private suspend fun getString(key: String): String? {
        val prefKey = stringPreferencesKey(key)
        val flow: Flow<String?> = dataStore.data.map { preferences ->
            preferences[prefKey]
        }
        return flow.first()
    }

}