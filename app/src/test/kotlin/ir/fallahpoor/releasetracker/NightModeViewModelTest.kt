@file:OptIn(ExperimentalCoroutinesApi::class)

package ir.fallahpoor.releasetracker

import androidx.appcompat.app.AppCompatDelegate
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.fakes.FakeStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NightModeViewModelTest {

    @JvmField
    @Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    private lateinit var nightModeViewModel: NightModeViewModel
    private val fakeStorage = FakeStorage()

    @Before
    fun runBeforeEachTest() {
        MockitoAnnotations.openMocks(this)
        nightModeViewModel = NightModeViewModel(storage = fakeStorage)
    }

    @After
    fun runAfterEachTest() {
        Mockito.validateMockitoUsage()
    }

    @Test
    fun `change night mode`() {

        // When
        nightModeViewModel.handleEvent(Event.ChangeNightMode(NightMode.ON))

        // Then
        Truth.assertThat(nightModeViewModel.state.value).isEqualTo(NightMode.ON)
        Truth.assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_YES)

    }

    // TODO Find a way to assert the sequence of the values of a StateFlow
//    @Test
//    fun `night modes are emitted correctly`() = runTest {
//
//        // Given
//        val nightModes = mutableListOf<NightMode>()
//
//        fakeStorage.setNightMode(NightMode.OFF)
//        fakeStorage.setNightMode(NightMode.ON)
//
//        // Then
//        Truth.assertThat(nightModes)
//            .isEqualTo(listOf(NightMode.AUTO, NightMode.OFF, NightMode.ON))
//
//    }

}