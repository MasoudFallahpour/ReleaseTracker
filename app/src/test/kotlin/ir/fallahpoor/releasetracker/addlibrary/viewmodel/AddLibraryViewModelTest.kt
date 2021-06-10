package ir.fallahpoor.releasetracker.addlibrary.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.MainCoroutineScopeRule
import ir.fallahpoor.releasetracker.addlibrary.view.AddLibraryState
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.testfakes.FakeLibraryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddLibraryViewModelTest {

    private companion object {
        const val LIBRARY_NAME = "ReleaseTracker"
        const val LIBRARY_URL = "masoodfallahpoor/ReleaseTracker"
    }

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
        runBlockingTest {

            // Given

            // When
            addLibraryViewModel.addLibrary(
                libraryName = "",
                libraryUrlPath = LIBRARY_URL
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().isEmpty()).isTrue()
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.EmptyLibraryName::class.java)

        }

    @Test
    fun `addLibrary() should set the state to EmptyLibraryUrl when library URL is empty`() =
        runBlockingTest {

            // Given

            // When
            addLibraryViewModel.addLibrary(
                libraryName = LIBRARY_NAME,
                libraryUrlPath = ""
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().isEmpty()).isTrue()
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.EmptyLibraryUrl::class.java)

        }

    @Test
    fun `addLibrary() should set the state to InvalidLibraryUrl when library URL is invalid`() =
        runBlockingTest {

            // Given

            // When
            addLibraryViewModel.addLibrary(
                libraryName = LIBRARY_NAME,
                libraryUrlPath = "InvalidLibraryUrl"
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().isEmpty()).isTrue()
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.InvalidLibraryUrl::class.java)

        }

    @Test
    fun `addLibrary() should set the state to Error when library name already exists`() =
        runBlockingTest {

            // Given
            fakeLibraryRepository.addLibrary(
                libraryName = LIBRARY_NAME,
                libraryUrl = LIBRARY_URL,
                libraryVersion = "version1"
            )

            // When
            addLibraryViewModel.addLibrary(
                libraryName = LIBRARY_NAME,
                libraryUrlPath = LIBRARY_URL
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().size).isEqualTo(1)
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.Error::class.java)

        }

    @Test
    fun `addLibrary() should set the state to LibraryAdded when library is added successfully`() =
        runBlockingTest {

            // Given

            // When
            addLibraryViewModel.addLibrary(
                libraryName = LIBRARY_NAME,
                libraryUrlPath = LIBRARY_URL
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibrary(LIBRARY_NAME)).isNotNull()
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.LibraryAdded::class.java)

        }

    @Test
    fun `addLibrary() should set the state to Error when there is an unexpected error`() =
        runBlockingTest {

            // Given
            val libraryName: String = FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR

            // When
            addLibraryViewModel.addLibrary(
                libraryName = libraryName,
                libraryUrlPath = LIBRARY_URL
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibrary(libraryName)).isNull()
            Truth.assertThat(addLibraryViewModel.state.value)
                .isInstanceOf(AddLibraryState.Error::class.java)

        }

}