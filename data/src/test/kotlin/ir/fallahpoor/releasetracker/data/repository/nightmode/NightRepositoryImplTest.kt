package ir.fallahpoor.releasetracker.data.repository.nightmode

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.MainDispatcherRule
import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.fakes.FakeStorage
import ir.fallahpoor.releasetracker.data.storage.LocalStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NightRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var nightModeRepository: NightModeRepository
    private lateinit var fakeStorage: FakeStorage

    @Before
    fun runBeforeEachTest() {
        fakeStorage = FakeStorage()
        nightModeRepository = NightModeRepositoryImpl(fakeStorage)
    }

    @Test
    fun `night mode is saved correctly`() = runTest {

        // Given
        val expectedNightMode = NightMode.OFF

        // When
        nightModeRepository.saveNightMode(expectedNightMode)

        // Then
        val actualNightMode = fakeStorage.getNightMode()
        Truth.assertThat(actualNightMode).isEqualTo(expectedNightMode)

    }

    @Test
    fun `saved night mode is returned correctly`() = runTest {

        // Given
        val expectedNightMode = NightMode.ON
        fakeStorage.saveNightMode(expectedNightMode)

        // When
        val actualNightMode = nightModeRepository.getNightMode()

        // Then
        Truth.assertThat(actualNightMode).isEqualTo(expectedNightMode)

    }

    @Test
    fun `default night is returned when there is no saved night mode`() = runTest {

        // When
        val actualNightMode = nightModeRepository.getNightMode()

        // Then
        Truth.assertThat(actualNightMode).isEqualTo(LocalStorage.DEFAULT_NIGHT_MODE)

    }

    @Test
    fun `night mode flow emits the saved night mode`() = runTest {

        // Given
        val actualNightModes = mutableListOf<NightMode>()
        val job = launch(UnconfinedTestDispatcher()) {
            nightModeRepository.getNightModeAsFlow().toList(actualNightModes)
        }

        // When
        with(nightModeRepository) {
            saveNightMode(NightMode.OFF)
            saveNightMode(NightMode.ON)
        }

        // Then
        val expectedNightModes = listOf(
            LocalStorage.DEFAULT_NIGHT_MODE,
            NightMode.OFF,
            NightMode.ON
        )
        Truth.assertThat(actualNightModes).isEqualTo(expectedNightModes)

        job.cancel()

    }

}