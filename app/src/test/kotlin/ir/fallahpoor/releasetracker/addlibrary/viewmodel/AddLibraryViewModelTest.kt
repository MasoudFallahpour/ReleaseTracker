package ir.fallahpoor.releasetracker.addlibrary.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.MainCoroutineScopeRule
import ir.fallahpoor.releasetracker.addlibrary.view.AddLibraryState
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddLibraryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    private lateinit var addLibraryViewModel: AddLibraryViewModel
    private lateinit var fakeLibraryRepository: FakeLibraryRepository

    @Before
    fun runBeforeEachTest() {
        fakeLibraryRepository = FakeLibraryRepository()
        addLibraryViewModel = AddLibraryViewModel(
            libraryRepository = fakeLibraryRepository,
            exceptionParser = ExceptionParser()
        )
    }

    @Test
    fun `addLibrary() should set the state to EmptyLibraryName when library name is empty`() =
        runTest {

            // Given
            fakeLibraryRepository.deleteLibraries()

            // When
            addLibraryViewModel.addLibrary(
                libraryName = "",
                libraryUrlPath = "does not matter"
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().isEmpty()).isTrue()
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.EmptyLibraryName::class.java)

        }

    @Test
    fun `addLibrary() should set the state to EmptyLibraryUrl when library URL is empty`() =
        runTest {

            // Given
            fakeLibraryRepository.deleteLibraries()

            // When
            addLibraryViewModel.addLibrary(
                libraryName = "does not matter",
                libraryUrlPath = ""
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().isEmpty()).isTrue()
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.EmptyLibraryUrl::class.java)

        }

    @Test
    fun `addLibrary() should set the state to InvalidLibraryUrl when library URL is invalid`() =
        runTest {

            // Given
            fakeLibraryRepository.deleteLibraries()

            // When
            addLibraryViewModel.addLibrary(
                libraryName = "does not matter",
                libraryUrlPath = "This is an invalid library URL path"
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().isEmpty()).isTrue()
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.InvalidLibraryUrl::class.java)

        }

    @Test
    fun `addLibrary() should set the state to Error when library name already exists`() =
        runTest {

            // Given
            fakeLibraryRepository.deleteLibraries()
            fakeLibraryRepository.addLibrary(
                libraryName = "coil",
                libraryUrl = "https://github.com/coil-kt/coil",
                libraryVersion = "1.0.0"
            )

            // When
            addLibraryViewModel.addLibrary(
                libraryName = "coil",
                libraryUrlPath = "coil-kt/coil"
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().size).isEqualTo(1)
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.Error::class.java)

        }

    @Test
    fun `addLibrary() should set the state to LibraryAdded when library is added successfully`() =
        runTest {

            // Given
            val libraryName = "abc"

            // When
            addLibraryViewModel.addLibrary(
                libraryName = libraryName,
                libraryUrlPath = "abc/def"
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibrary(libraryName)).isNotNull()
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.LibraryAdded::class.java)

        }

    @Test
    fun `addLibrary() should set the state to Error when there is an unexpected error`() =
        runTest {

            // Given
            val libraryName: String = FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING

            // When
            addLibraryViewModel.addLibrary(
                libraryName = libraryName,
                libraryUrlPath = "abc/def"
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibrary(libraryName)).isNull()
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.Error::class.java)

        }

}