package ir.fallahpoor.releasetracker.libraries.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.MainCoroutineScopeRule
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import ir.fallahpoor.releasetracker.fakes.FakeStorage
import ir.fallahpoor.releasetracker.libraries.view.states.LibrariesListState
import ir.fallahpoor.releasetracker.libraries.view.states.LibraryDeleteState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibrariesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    private lateinit var librariesViewModel: LibrariesViewModel
    private lateinit var fakeLibraryRepository: FakeLibraryRepository
    private lateinit var fakeStorage: FakeStorage

    @Before
    fun runBeforeEachTest() {
        fakeLibraryRepository = FakeLibraryRepository()
        fakeStorage = FakeStorage()
        librariesViewModel = LibrariesViewModel(
            libraryRepository = fakeLibraryRepository,
            storage = fakeStorage,
            exceptionParser = ExceptionParser()
        )
    }

    @Test
    fun `getLibraries() should return all libraries sorted by given order`() =
        runBlockingTest {

            // Given
            val expectedLibraries = listOf(
                Library(name = "A", url = "url1", version = "version1", pinned = 0),
                Library(name = "C", url = "url2", version = "version2", pinned = 0),
                Library(name = "B", url = "url3", version = "version3", pinned = 0)
            )
            expectedLibraries.forEach {
                fakeLibraryRepository.addLibrary(
                    libraryName = it.name,
                    libraryUrl = it.url,
                    libraryVersion = it.version
                )
            }
            librariesViewModel.librariesListState.observeForever { }

            // When
            librariesViewModel.getLibraries(sortOrder = SortOrder.Z_TO_A)

            // Then
            val librariesListState: LibrariesListState? =
                librariesViewModel.librariesListState.value
            val librariesLoadedState = librariesListState as LibrariesListState.LibrariesLoaded
            Truth.assertThat(librariesLoadedState.libraries)
                .isEqualTo(expectedLibraries.sortedByDescending { it.name })

        }

    @Test
    fun `getLibraries() should return all libraries whose names contain the search term sorted by given order`() =
        runBlockingTest {

            // Given
            val libraries = listOf(
                Library(name = "GreenDao", url = "url1", version = "version1", pinned = 0),
                Library(name = "ReleaseTracker", url = "url2", version = "version2", pinned = 0),
                Library(name = "Koin", url = "url3", version = "version3", pinned = 0)
            )
            libraries.forEach {
                fakeLibraryRepository.addLibrary(
                    libraryName = it.name,
                    libraryUrl = it.url,
                    libraryVersion = it.version
                )
            }
            val searchTerm = "re"
            val expectedLibraries = libraries.filter {
                it.name.contains(searchTerm, ignoreCase = true)
            }.sortedByDescending {
                it.name
            }
            librariesViewModel.librariesListState.observeForever { }

            // When
            librariesViewModel.getLibraries(sortOrder = SortOrder.Z_TO_A, searchTerm = searchTerm)

            // Then
            val librariesListState: LibrariesListState? =
                librariesViewModel.librariesListState.value
            val librariesLoadedState = librariesListState as LibrariesListState.LibrariesLoaded
            Truth.assertThat(librariesLoadedState.libraries)
                .isEqualTo(expectedLibraries)

        }

    @Test
    fun `pinLibrary() should pin the library`() =
        runBlockingTest {

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
        runBlockingTest {

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
    fun `deleteLibrary() should set the state to Deleted when library is deleted successfully`() =
        runBlockingTest {

            // Given
            val libraryName = "name1"
            fakeLibraryRepository.addLibrary(
                libraryName = libraryName,
                libraryUrl = "url1",
                libraryVersion = "version1"
            )
            fakeLibraryRepository.addLibrary(
                libraryName = "name2",
                libraryUrl = "url2",
                libraryVersion = "version2"
            )
            val libraryToDelete: Library = fakeLibraryRepository.getLibrary(libraryName)!!

            // When
            librariesViewModel.deleteLibrary(libraryToDelete)

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().size).isEqualTo(1)
            Truth.assertThat(fakeLibraryRepository.getLibrary(libraryName)).isNull()
            Truth.assertThat(librariesViewModel.deleteState.value)
                .isInstanceOf(LibraryDeleteState.Deleted::class.java)

        }

    @Test
    fun `deleteLibrary() should set the state to Error when there is an unexpected error`() =
        runBlockingTest {

            // Given
            fakeLibraryRepository.addLibrary(
                libraryName = "name",
                libraryUrl = "url",
                libraryVersion = "version"
            )

            // When
            librariesViewModel.deleteLibrary(
                Library(
                    name = FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR,
                    url = "url",
                    version = "version",
                    pinned = 0
                )
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().size).isEqualTo(1)
            Truth.assertThat(fakeLibraryRepository.getLibrary("name")).isNotNull()
            Truth.assertThat(librariesViewModel.deleteState.value)
                .isInstanceOf(LibraryDeleteState.Error::class.java)

        }

    @Test
    fun test_saveSortOrder() {

        // Given
        val expectedSortOrder = SortOrder.PINNED_FIRST

        // When
        librariesViewModel.saveSortOrder(expectedSortOrder)

        // Then
        val actualSortOrder = fakeStorage.getSortOrder()
        Truth.assertThat(actualSortOrder).isEqualTo(expectedSortOrder)

    }

    // lastUpdateCheckState

}