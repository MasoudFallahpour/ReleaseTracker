package ir.fallahpoor.releasetracker.data.utils.storage

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import com.afollestad.rxkprefs.rxkPrefs
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.data.utils.storage.LocalStorage.Companion.KEY_LAST_UPDATE_CHECK
import ir.fallahpoor.releasetracker.data.utils.storage.LocalStorage.Companion.KEY_NIGHT_MODE
import ir.fallahpoor.releasetracker.data.utils.storage.LocalStorage.Companion.KEY_ORDER
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocalStorageTest {

    private lateinit var localStorage: LocalStorage
    private lateinit var sharedPreferences: SharedPreferences
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun runBeforeEachTest() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val rxkPrefs = rxkPrefs(sharedPreferences)
        localStorage = LocalStorage(sharedPreferences, rxkPrefs)
    }

    @Test
    fun `getNightMode() should return the saved night mode`() {

        // Given
        val expectedNightMode = NightMode.OFF
        sharedPreferences.edit()
            .putString(KEY_NIGHT_MODE, expectedNightMode.name)
            .commit()

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
    fun test_setNightMode() {

        // Given
        val expectedNightMode = NightMode.ON

        // When
        localStorage.setNightMode(expectedNightMode)

        // Then
        val actualNightMode =
            NightMode.valueOf(sharedPreferences.getString(KEY_NIGHT_MODE, null) ?: "")
        Truth.assertThat(actualNightMode).isEqualTo(expectedNightMode)

    }

    @Test
    fun test_setSortOrder() {

        // Given
        val expectedSortOrder = SortOrder.Z_TO_A

        // When
        localStorage.setSortOrder(expectedSortOrder)

        // Then
        val actualSortOrder = SortOrder.valueOf(sharedPreferences.getString(KEY_ORDER, null) ?: "")
        Truth.assertThat(actualSortOrder).isEqualTo(expectedSortOrder)

    }

    @Test
    fun test_getSortOrder() {

        // Given
        val expectedSortOrder = SortOrder.PINNED_FIRST
        sharedPreferences.edit()
            .putString(KEY_ORDER, expectedSortOrder.name)
            .commit()

        // When
        val actualSortOrder = localStorage.getSortOrder()

        // Then
        Truth.assertThat(actualSortOrder).isEqualTo(expectedSortOrder)

    }

    @Test
    fun test_setLastUpdateCheck() {

        // Given
        val expectedLastUpdateCheckDate = "15:30, March"

        // When
        localStorage.setLastUpdateCheck(expectedLastUpdateCheckDate)

        // Then
        val actualLastUpdateCheckDate = sharedPreferences.getString(KEY_LAST_UPDATE_CHECK, null)
        Truth.assertThat(actualLastUpdateCheckDate).isEqualTo(expectedLastUpdateCheckDate)

    }

}