package ir.fallahpoor.releasetracker.libraries.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import ir.fallahpoor.releasetracker.fakes.FakeStorage
import ir.fallahpoor.releasetracker.libraries.view.states.LibrariesListState
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
class LibrariesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var librariesViewModel: LibrariesViewModel
    private lateinit var fakeLibraryRepository: FakeLibraryRepository
    private lateinit var fakeStorage: FakeStorage

    @Before
    fun runBeforeEachTest() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeLibraryRepository = FakeLibraryRepository()
        fakeStorage = FakeStorage()
        librariesViewModel = LibrariesViewModel(
            libraryRepository = fakeLibraryRepository,
            storage = fakeStorage
        )
    }

    @After
    fun runAfterEachTest() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getLibraries() should return all libraries sorted by given order`() =
        runTest {

            // Given
            val expectedLibraries = fakeLibraryRepository.getLibraries()
                .sortedByDescending { it.name }

            // When
            librariesViewModel.getLibraries(sortOrder = SortOrder.Z_TO_A, searchQuery = "")

            // Then
            librariesViewModel.librariesListState.observeForever {}
            val librariesListState: LibrariesListState? =
                librariesViewModel.librariesListState.value
            val librariesLoadedState = librariesListState as LibrariesListState.LibrariesLoaded
            Truth.assertThat(librariesLoadedState.libraries)
                .isEqualTo(expectedLibraries)

        }

    @Test
    fun `getLibraries() should return all matched libraries sorted by given order`() =
        runTest {

            // Given
            val searchQuery = "ko"
            val expectedLibraries = fakeLibraryRepository.getLibraries().filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }.sortedByDescending {
                it.name
            }

            // When
            librariesViewModel.getLibraries(sortOrder = SortOrder.Z_TO_A, searchQuery = searchQuery)

            // Then
            librariesViewModel.librariesListState.observeForever {}
            val librariesListState: LibrariesListState? =
                librariesViewModel.librariesListState.value
            val librariesLoadedState = librariesListState as LibrariesListState.LibrariesLoaded
            Truth.assertThat(librariesLoadedState.libraries)
                .isEqualTo(expectedLibraries)

        }

    @Test
    fun `pinLibrary() should pin the library`() =
        runTest {

            // Given
            val libraryName = "name"
            fakeLibraryRepository.addLibrary(
                libraryName = libraryName,
                libraryUrl = "url",
                libraryVersion = "version"
            )

            // When
            val libraryToPin: Library = fakeLibraryRepository.getLibrary(libraryName)!!
            librariesViewModel.pinLibrary(
                library = libraryToPin,
                pin = true
            )

            // Then
            val library = fakeLibraryRepository.getLibrary(libraryName)
            Truth.assertThat(library?.isPinned()).isTrue()

        }

    @Test
    fun `pinLibrary() should unpin the library`() =
        runTest {

            // Given
            val libraryName = "name"
            fakeLibraryRepository.addLibrary(
                libraryName = libraryName,
                libraryUrl = "url",
                libraryVersion = "version"
            )
            val libraryToPin: Library = fakeLibraryRepository.getLibrary(libraryName)!!
            fakeLibraryRepository.updateLibrary(libraryToPin.copy(pinned = 1))

            // When
            val libraryToUnpin: Library = fakeLibraryRepository.getLibrary(libraryName)!!
            librariesViewModel.pinLibrary(
                library = libraryToUnpin,
                pin = false
            )

            // Then
            val library = fakeLibraryRepository.getLibrary(libraryName)
            Truth.assertThat(library?.isPinned()).isFalse()

        }

    @Test
    fun `deleteLibrary() should delete the library`() =
        runTest {

            // Given
            fakeLibraryRepository.deleteLibraries()
            val libraryName = "name"
            fakeLibraryRepository.addLibrary(
                libraryName = libraryName,
                libraryUrl = "url",
                libraryVersion = "version"
            )
            val libraryToDelete: Library = fakeLibraryRepository.getLibrary(libraryName)!!

            // When
            librariesViewModel.deleteLibrary(libraryToDelete)

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().size).isEqualTo(0)
            Truth.assertThat(fakeLibraryRepository.getLibrary(libraryName)).isNull()

        }

}