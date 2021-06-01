package ir.fallahpoor.releasetracker.libraries.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.MainCoroutineScopeRule
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import ir.fallahpoor.releasetracker.fakes.FakeStorage
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

    @Before
    fun runBeforeEachTest() {
        fakeLibraryRepository = FakeLibraryRepository()
        librariesViewModel = LibrariesViewModel(
            libraryRepository = fakeLibraryRepository,
            storage = FakeStorage(),
            exceptionParser = ExceptionParser()
        )
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

    // librariesListState
    // lastUpdateCheckState
    // getLibraries()
    // pinLibrary()

}