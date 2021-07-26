package ir.fallahpoor.releasetracker.common

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.common.managers.NightModeManager
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.fakes.FakeStorage
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class NightModeManagerTest {

    private lateinit var nightModeManager: NightModeManager
    private val fakeStorage = FakeStorage()

    @Before
    fun runBeforeEachTest() {
        val context: Context = ApplicationProvider.getApplicationContext()
        nightModeManager = NightModeManager(context = context, storage = fakeStorage)
    }

    @Test
    fun test_setNightMode() {

        // Given

        // When
        nightModeManager.setNightMode(NightMode.ON)

        // Then
        Truth.assertThat(fakeStorage.getNightMode()).isEqualTo(NightMode.ON)
        Truth.assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_YES)

    }

    @Test
    fun test_getNightMode() {

        // Given
        val expectedNightMode = NightMode.OFF
        fakeStorage.setNightMode(expectedNightMode)

        // When
        val actualNightMode = nightModeManager.currentNightMode

        // Then
        Truth.assertThat(actualNightMode).isEqualTo(expectedNightMode)

    }

    @Test
    fun `isNightModeOn() should return false`() {

        // Given
        fakeStorage.setNightMode(NightMode.OFF)

        // When
        val actualIsNightModeOn = nightModeManager.isNightModeOn

        // Then
        Truth.assertThat(actualIsNightModeOn).isFalse()

    }

    @Test
    fun `isNightModeOn() should return true`() {

        // Given
        fakeStorage.setNightMode(NightMode.ON)

        // When
        val actualIsNightModeOn = nightModeManager.isNightModeOn

        // Then
        Truth.assertThat(actualIsNightModeOn).isTrue()

    }

    @Test
    fun `isNightModeOn() should return false when night mode isn't set explicitly and system isn't in night mode`() {

        // Given
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        // When
        val actualIsNightModeOn = nightModeManager.isNightModeOn

        // Then
        Truth.assertThat(actualIsNightModeOn).isFalse()

    }

//    @Test
//    fun `isNightModeOn() should return true when night mode isn't set explicitly and system is in night mode`() {
//
//        // Given
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//
//        // When
//        val actualIsNightModeOn = nightModeManager.isNightModeOn()
//
//        // Then
//        Truth.assertThat(actualIsNightModeOn).isTrue()
//
//    }

}