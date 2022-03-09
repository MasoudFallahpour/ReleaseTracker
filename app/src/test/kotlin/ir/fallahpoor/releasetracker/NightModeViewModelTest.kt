package ir.fallahpoor.releasetracker

import androidx.appcompat.app.AppCompatDelegate
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.fakes.FakeStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NightModeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var nightModeViewModel: NightModeViewModel
    private lateinit var fakeStorage: FakeStorage

    @Before
    fun runBeforeEachTest() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeStorage = FakeStorage()
        nightModeViewModel = NightModeViewModel(storage = fakeStorage)
    }

    @After
    fun runAfterEachTest() {
        Dispatchers.resetMain()
    }

    @Test
    fun `night mode is changed if new night mode is not equal to current night mode`() {

        // When
        nightModeViewModel.handleEvent(Event.ChangeNightMode(NightMode.ON))

        // Then
        Truth.assertThat(nightModeViewModel.state.value).isEqualTo(NightMode.ON)
        Truth.assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_YES)

    }

    @Test
    fun `night mode is not changed if new night mode is equal to current night mode`() = runTest {

        // Given
        fakeStorage.setNightMode(NightMode.OFF)

        // When
        nightModeViewModel.handleEvent(Event.ChangeNightMode(NightMode.OFF))

        // Then
        Truth.assertThat(nightModeViewModel.state.value).isEqualTo(NightMode.OFF)
        Truth.assertThat(fakeStorage.getNightMode()).isEqualTo(NightMode.OFF)

    }

    // TODO Find a way to assert the sequence of NightModes
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