package ir.fallahpoor.releasetracker.addlibrary

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
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
class AddLibraryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var addLibraryViewModel: AddLibraryViewModel
    private lateinit var fakeLibraryRepository: FakeLibraryRepository

    @Before
    fun runBeforeEachTest() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeLibraryRepository = FakeLibraryRepository()
        addLibraryViewModel = AddLibraryViewModel(
            libraryRepository = fakeLibraryRepository,
            exceptionParser = ExceptionParser()
        )
    }

    @After
    fun runAfterEachTest() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state is correctly updated when library name changes`() {

        // When
        addLibraryViewModel.handleEvent(Event.UpdateLibraryName(libraryName = "abc"))

        // Then
        Truth.assertThat(addLibraryViewModel.uiState.value)
            .isEqualTo(
                AddLibraryScreenUiState(
                    libraryName = "abc",
                    libraryUrlPath = "",
                    addLibraryState = AddLibraryState.Initial
                )
            )

    }

    @Test
    fun `state is correctly updated when library URL path changes`() {

        // When
        addLibraryViewModel.handleEvent(Event.UpdateLibraryUrlPath(libraryUrlPath = "abc/def"))

        // Then
        Truth.assertThat(addLibraryViewModel.uiState.value)
            .isEqualTo(
                AddLibraryScreenUiState(
                    libraryName = "",
                    libraryUrlPath = "abc/def",
                    addLibraryState = AddLibraryState.Initial
                )
            )

    }

    @Test
    fun `state is correctly updated when library name is empty`() =
        runTest {

            // Given
            fakeLibraryRepository.deleteLibraries()

            // When
            addLibraryViewModel.handleEvent(
                Event.AddLibrary(
                    libraryName = "",
                    libraryUrlPath = "does not matter"
                )
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries()).isEmpty()
            Truth.assertThat(addLibraryViewModel.uiState.value)
                .isEqualTo(
                    AddLibraryScreenUiState(
                        libraryName = "",
                        libraryUrlPath = "",
                        addLibraryState = AddLibraryState.EmptyLibraryName
                    )
                )

        }

    @Test
    fun `state is correctly updated when library URL is empty`() =
        runTest {

            // Given
            fakeLibraryRepository.deleteLibraries()

            // When
            addLibraryViewModel.handleEvent(
                Event.AddLibrary(
                    libraryName = "does not matter",
                    libraryUrlPath = ""
                )
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries()).isEmpty()
            Truth.assertThat(addLibraryViewModel.uiState.value)
                .isEqualTo(
                    AddLibraryScreenUiState(
                        libraryName = "",
                        libraryUrlPath = "",
                        addLibraryState = AddLibraryState.EmptyLibraryUrl
                    )
                )

        }

    @Test
    fun `state is correctly updated when library URL is invalid`() =
        runTest {

            // Given
            fakeLibraryRepository.deleteLibraries()

            // When
            addLibraryViewModel.handleEvent(
                Event.AddLibrary(
                    libraryName = "does not matter",
                    libraryUrlPath = "This is an invalid library URL path"
                )
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries()).isEmpty()
            Truth.assertThat(addLibraryViewModel.uiState.value)
                .isEqualTo(
                    AddLibraryScreenUiState(
                        libraryName = "",
                        libraryUrlPath = "",
                        addLibraryState = AddLibraryState.InvalidLibraryUrl
                    )
                )

        }

    @Test
    fun `state is correctly updated when library name already exists`() =
        runTest {

            // Given
            fakeLibraryRepository.deleteLibraries()
            fakeLibraryRepository.addLibrary(
                libraryName = "coil",
                libraryUrl = "https://github.com/coil-kt/coil",
                libraryVersion = "1.0.0"
            )

            // When
            addLibraryViewModel.handleEvent(
                Event.AddLibrary(
                    libraryName = "coil",
                    libraryUrlPath = "coil-kt/coil"
                )
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().size).isEqualTo(1)
            Truth.assertThat(addLibraryViewModel.uiState.value)
                .isEqualTo(
                    AddLibraryScreenUiState(
                        libraryName = "",
                        libraryUrlPath = "",
                        addLibraryState = AddLibraryState.Error("Library already exists")
                    )
                )

        }

    @Test
    fun `state is correctly updated when a library is added successfully`() = runTest {

        // TODO don't just assert the final state. Assert that the correct sequence of state updates
        //  happens

        // When
        addLibraryViewModel.handleEvent(
            Event.AddLibrary(
                libraryName = "abc",
                libraryUrlPath = "abc/def"
            )
        )

        // Then
        Truth.assertThat(fakeLibraryRepository.getLibrary("abc")).isNotNull()
        Truth.assertThat(addLibraryViewModel.uiState.value)
            .isEqualTo(
                AddLibraryScreenUiState(
                    libraryName = "",
                    libraryUrlPath = "",
                    addLibraryState = AddLibraryState.LibraryAdded
                )
            )

    }

    @Test
    fun `state is correctly updated when there is an unexpected error`() =
        runTest {

            // Given
            val libraryName: String = FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING

            // When
            addLibraryViewModel.handleEvent(
                Event.AddLibrary(
                    libraryName = libraryName,
                    libraryUrlPath = "abc/def"
                )
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibrary(libraryName)).isNull()
            Truth.assertThat(addLibraryViewModel.uiState.value)
                .isEqualTo(
                    AddLibraryScreenUiState(
                        libraryName = "",
                        libraryUrlPath = "",
                        addLibraryState = AddLibraryState.Error("Internet not connected.")
                    )
                )

        }

    @Test
    fun `state is correctly updated when an error is dismissed`() {

        // Given the current state is Error
        addLibraryViewModel.handleEvent(
            Event.AddLibrary(
                libraryName = FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING,
                libraryUrlPath = "abc/def"
            )
        )

        // When
        addLibraryViewModel.handleEvent(Event.ErrorDismissed)

        // Then
        Truth.assertThat(addLibraryViewModel.uiState.value)
            .isEqualTo(
                AddLibraryScreenUiState(
                    libraryName = "",
                    libraryUrlPath = "",
                    addLibraryState = AddLibraryState.Initial
                )
            )

    }

}