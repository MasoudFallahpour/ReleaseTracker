@file:OptIn(ExperimentalCoroutinesApi::class)

package ir.fallahpoor.releasetracker.features.libraries.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.fakes.FakeData
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import ir.fallahpoor.releasetracker.fakes.FakeStorageRepository
import ir.fallahpoor.releasetracker.features.libraries.Event
import ir.fallahpoor.releasetracker.features.libraries.LibrariesListScreenUiState
import ir.fallahpoor.releasetracker.features.libraries.LibrariesListState
import ir.fallahpoor.releasetracker.features.libraries.LibrariesViewModel
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

class LibrariesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var librariesViewModel: LibrariesViewModel
    private lateinit var fakeLibraryRepository: FakeLibraryRepository
    private lateinit var fakeStorageRepository: FakeStorageRepository

    @Before
    fun runBeforeEachTest() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeLibraryRepository = FakeLibraryRepository()
        fakeStorageRepository = FakeStorageRepository()
        librariesViewModel = LibrariesViewModel(fakeLibraryRepository, fakeStorageRepository)
    }

    @After
    fun runAfterEachTest() {
        Dispatchers.resetMain()
    }

    @Test
    fun `all libraries are returned sorted by given sort order`() = runTest {

        // Given
        val expectedLibraries = fakeLibraryRepository.getLibraries().sortedByDescending { it.name }

        // When
        librariesViewModel.handleEvent(Event.ChangeSortOrder(SortOrder.Z_TO_A))

        // Then
        val librariesListScreenUiState: LibrariesListScreenUiState =
            librariesViewModel.uiState.value
        val librariesLoadedState: LibrariesListState.LibrariesLoaded =
            librariesListScreenUiState.librariesListState as LibrariesListState.LibrariesLoaded
        Truth.assertThat(librariesLoadedState.libraries).isEqualTo(expectedLibraries)

    }

    @Test
    fun `all matched libraries are returned`() = runTest {

        // Given
        val searchQuery = "ko"
        val expectedLibraries = fakeLibraryRepository.getLibraries().filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }.sortedBy {
            it.name
        }

        // When
        librariesViewModel.handleEvent(Event.ChangeSearchQuery(searchQuery))

        // Then
        val librariesListScreenUiState: LibrariesListScreenUiState =
            librariesViewModel.uiState.value
        val librariesLoadedState: LibrariesListState.LibrariesLoaded =
            librariesListScreenUiState.librariesListState as LibrariesListState.LibrariesLoaded
        Truth.assertThat(librariesLoadedState.libraries).isEqualTo(expectedLibraries)

    }

    @Test
    fun `pin library`() = runTest {

        // Given

        // When
        val libraryToPin = fakeLibraryRepository.getLibrary(FakeData.Kotlin.name)!!
        librariesViewModel.handleEvent(Event.PinLibrary(library = libraryToPin, pin = true))

        // Then
        val library = fakeLibraryRepository.getLibrary(FakeData.Kotlin.name)
        Truth.assertThat(library?.isPinned).isTrue()

    }

    @Test
    fun `unpin library`() = runTest {

        // Given
        val libraryToUnpin = fakeLibraryRepository.getLibrary(FakeData.Koin.name)!!

        // When
        librariesViewModel.handleEvent(Event.PinLibrary(library = libraryToUnpin, pin = false))

        // Then
        val library = fakeLibraryRepository.getLibrary(FakeData.Koin.name)
        Truth.assertThat(library?.isPinned).isFalse()

    }

    @Test
    fun `delete library`() = runTest {

        // Given
        val libraryToDelete = fakeLibraryRepository.getLibrary(FakeData.Koin.name)!!

        // When
        librariesViewModel.handleEvent(Event.DeleteLibrary(libraryToDelete))

        // Then
        Truth.assertThat(fakeLibraryRepository.getLibraries().size).isEqualTo(2)
        Truth.assertThat(fakeLibraryRepository.getLibrary(FakeData.Koin.name)).isNull()

    }

}