package ir.fallahpoor.releasetracker.common

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltTestApplication
import ir.fallahpoor.releasetracker.common.managers.NightModeManager
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.fakes.FakeStorage
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class NightModeManagerTest {

    @JvmField
    @Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    private lateinit var nightModeManager: NightModeManager
    private val fakeStorage = FakeStorage()

    @Mock
    private lateinit var mockObserver: Observer<NightMode>

    @Before
    fun runBeforeEachTest() {
        MockitoAnnotations.openMocks(this)
        val context: Context = ApplicationProvider.getApplicationContext()
        nightModeManager = NightModeManager(context = context, storage = fakeStorage)
    }

    @After
    fun runAfterEachTest() {
        Mockito.validateMockitoUsage()
    }

    @Test
    fun test_setNightMode() {

        // Given

        // When
        nightModeManager.setNightMode(NightMode.ON)

        // Then
        Truth.assertThat(nightModeManager.currentNightMode).isEqualTo(NightMode.ON)
        Truth.assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_YES)

    }

    @Test
    fun test_currentNightMode() {

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
//        val isNightModeOn = nightModeManager.isNightModeOn
//
//        // Then
//        Truth.assertThat(isNightModeOn).isTrue()
//
//    }

    @Test
    fun test_nightModeLiveData() {

        // Given

        // When
        nightModeManager.nightModeLiveData.observeForever(mockObserver)
        fakeStorage.setNightMode(NightMode.OFF)
        fakeStorage.setNightMode(NightMode.ON)

        // TODO Test that the order valued emitted by nightModeLivedata is AUTO, OFF, ON.
        //  Right now the order of values are not taken into account
        // Then
        Mockito.verify(mockObserver).onChanged(NightMode.AUTO)
        Mockito.verify(mockObserver).onChanged(NightMode.OFF)
        Mockito.verify(mockObserver).onChanged(NightMode.ON)
        Mockito.verifyNoMoreInteractions(mockObserver)

    }

}