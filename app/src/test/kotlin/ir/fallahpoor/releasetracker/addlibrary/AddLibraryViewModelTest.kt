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

    companion object {
        // I've put some leading and trailing spaces intentionally to make sure that the library name and URL
        // are trimmed.
        private const val LIBRARY_NAME = "  ReleaseTracker "
        private const val LIBRARY_URL_PATH = " masoodfallahpoor/ReleaseTracker   "
    }

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

        // Given
        addLibraryViewModel.handleEvent(Event.UpdateLibraryUrlPath(LIBRARY_URL_PATH))

        // When
        addLibraryViewModel.handleEvent(Event.UpdateLibraryName(libraryName = LIBRARY_NAME))

        // Then
        Truth.assertThat(addLibraryViewModel.uiState.value)
            .isEqualTo(
                AddLibraryScreenUiState(
                    libraryName = LIBRARY_NAME,
                    libraryUrlPath = LIBRARY_URL_PATH,
                    addLibraryState = AddLibraryState.Initial
                )
            )

    }

    @Test
    fun `state is correctly updated when library URL path changes`() {

        // Given
        addLibraryViewModel.handleEvent(Event.UpdateLibraryName(LIBRARY_NAME))

        // When
        addLibraryViewModel.handleEvent(Event.UpdateLibraryUrlPath(libraryUrlPath = LIBRARY_URL_PATH))

        // Then
        Truth.assertThat(addLibraryViewModel.uiState.value)
            .isEqualTo(
                AddLibraryScreenUiState(
                    libraryName = LIBRARY_NAME,
                    libraryUrlPath = LIBRARY_URL_PATH,
                    addLibraryState = AddLibraryState.Initial
                )
            )

    }

    @Test
    fun `state is correctly updated when library name is empty`() =
        runTest {

            // Given
            fakeLibraryRepository.deleteLibraries()
            addLibraryViewModel.handleEvent(Event.UpdateLibraryUrlPath(LIBRARY_URL_PATH))

            // When
            addLibraryViewModel.handleEvent(
                Event.AddLibrary(
                    libraryName = "",
                    libraryUrlPath = LIBRARY_URL_PATH
                )
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries()).isEmpty()
            Truth.assertThat(addLibraryViewModel.uiState.value)
                .isEqualTo(
                    AddLibraryScreenUiState(
                        libraryName = "",
                        libraryUrlPath = LIBRARY_URL_PATH,
                        addLibraryState = AddLibraryState.EmptyLibraryName
                    )
                )

        }

    @Test
    fun `state is correctly updated when library URL is empty`() =
        runTest {

            // Given
            fakeLibraryRepository.deleteLibraries()
            addLibraryViewModel.handleEvent(Event.UpdateLibraryName(LIBRARY_NAME))

            // When
            addLibraryViewModel.handleEvent(
                Event.AddLibrary(
                    libraryName = LIBRARY_NAME,
                    libraryUrlPath = ""
                )
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries()).isEmpty()
            Truth.assertThat(addLibraryViewModel.uiState.value)
                .isEqualTo(
                    AddLibraryScreenUiState(
                        libraryName = LIBRARY_NAME,
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
            addLibraryViewModel.handleEvent(Event.UpdateLibraryName(LIBRARY_NAME))
            addLibraryViewModel.handleEvent(Event.UpdateLibraryUrlPath("this is an invalid URL"))

            // When
            addLibraryViewModel.handleEvent(
                Event.AddLibrary(
                    libraryName = LIBRARY_NAME,
                    libraryUrlPath = "this is an invalid URL"
                )
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries()).isEmpty()
            Truth.assertThat(addLibraryViewModel.uiState.value)
                .isEqualTo(
                    AddLibraryScreenUiState(
                        libraryName = LIBRARY_NAME,
                        libraryUrlPath = "this is an invalid URL",
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
                libraryName = LIBRARY_NAME,
                libraryUrl = "https://github.com/masoodfallahpoor/ReleaseTracker",
                libraryVersion = "1.0"
            )
            addLibraryViewModel.handleEvent(Event.UpdateLibraryName(LIBRARY_NAME))
            addLibraryViewModel.handleEvent(Event.UpdateLibraryUrlPath(LIBRARY_URL_PATH))


            // When
            addLibraryViewModel.handleEvent(
                Event.AddLibrary(
                    libraryName = LIBRARY_NAME,
                    libraryUrlPath = LIBRARY_URL_PATH
                )
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibraries().size).isEqualTo(1)
            Truth.assertThat(addLibraryViewModel.uiState.value)
                .isEqualTo(
                    AddLibraryScreenUiState(
                        libraryName = LIBRARY_NAME,
                        libraryUrlPath = LIBRARY_URL_PATH,
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
                libraryName = LIBRARY_NAME,
                libraryUrlPath = LIBRARY_URL_PATH
            )
        )

        // Then
        Truth.assertThat(fakeLibraryRepository.getLibrary(LIBRARY_NAME)).isNotNull()
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
            addLibraryViewModel.handleEvent(Event.UpdateLibraryName(FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING))
            addLibraryViewModel.handleEvent(Event.UpdateLibraryUrlPath(LIBRARY_URL_PATH))

            // When
            addLibraryViewModel.handleEvent(
                Event.AddLibrary(
                    libraryName = FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING,
                    libraryUrlPath = LIBRARY_URL_PATH
                )
            )

            // Then
            Truth.assertThat(fakeLibraryRepository.getLibrary(FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING))
                .isNull()
            Truth.assertThat(addLibraryViewModel.uiState.value)
                .isEqualTo(
                    AddLibraryScreenUiState(
                        libraryName = FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING,
                        libraryUrlPath = LIBRARY_URL_PATH,
                        addLibraryState = AddLibraryState.Error("Internet not connected.")
                    )
                )

        }

    @Test
    fun `state is correctly updated when an error is dismissed`() {

        // Given the current state is Error
        addLibraryViewModel.handleEvent(Event.UpdateLibraryName(FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING))
        addLibraryViewModel.handleEvent(Event.UpdateLibraryUrlPath(LIBRARY_URL_PATH))
        addLibraryViewModel.handleEvent(
            Event.AddLibrary(
                libraryName = FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING,
                libraryUrlPath = LIBRARY_URL_PATH
            )
        )

        // When
        addLibraryViewModel.handleEvent(Event.ErrorDismissed)

        // Then
        Truth.assertThat(addLibraryViewModel.uiState.value)
            .isEqualTo(
                AddLibraryScreenUiState(
                    libraryName = FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING,
                    libraryUrlPath = LIBRARY_URL_PATH,
                    addLibraryState = AddLibraryState.Initial
                )
            )

    }

}