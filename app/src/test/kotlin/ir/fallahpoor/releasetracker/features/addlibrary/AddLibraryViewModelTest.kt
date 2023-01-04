package ir.fallahpoor.releasetracker.features.addlibrary

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.MainDispatcherRule
import ir.fallahpoor.releasetracker.common.MessageProvider
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var addLibraryViewModel: AddLibraryViewModel
    private lateinit var fakeLibraryRepository: FakeLibraryRepository

    @Before
    fun runBeforeEachTest() {
        fakeLibraryRepository = FakeLibraryRepository()
        addLibraryViewModel = AddLibraryViewModel(
            libraryRepository = fakeLibraryRepository,
            messageProvider = MessageProvider()
        )
    }

    @Test
    fun `state is correctly updated when library name changes`() {

        // Given
        addLibraryViewModel.handleEvent(Action.UpdateLibraryUrlPath(LIBRARY_URL_PATH))

        // When
        addLibraryViewModel.handleEvent(Action.UpdateLibraryName(libraryName = LIBRARY_NAME))

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
        addLibraryViewModel.handleEvent(Action.UpdateLibraryName(LIBRARY_NAME))

        // When
        addLibraryViewModel.handleEvent(Action.UpdateLibraryUrlPath(libraryUrlPath = LIBRARY_URL_PATH))

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
            addLibraryViewModel.handleEvent(Action.UpdateLibraryUrlPath(LIBRARY_URL_PATH))

            // When
            addLibraryViewModel.handleEvent(
                Action.AddLibrary(
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
            addLibraryViewModel.handleEvent(Action.UpdateLibraryName(LIBRARY_NAME))

            // When
            addLibraryViewModel.handleEvent(
                Action.AddLibrary(
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
            addLibraryViewModel.handleEvent(Action.UpdateLibraryName(LIBRARY_NAME))
            addLibraryViewModel.handleEvent(Action.UpdateLibraryUrlPath("this is an invalid URL"))

            // When
            addLibraryViewModel.handleEvent(
                Action.AddLibrary(
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
                name = LIBRARY_NAME,
                url = "https://github.com/masoodfallahpoor/ReleaseTracker",
                version = "1.0"
            )
            addLibraryViewModel.handleEvent(Action.UpdateLibraryName(LIBRARY_NAME))
            addLibraryViewModel.handleEvent(Action.UpdateLibraryUrlPath(LIBRARY_URL_PATH))


            // When
            addLibraryViewModel.handleEvent(
                Action.AddLibrary(
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
            Action.AddLibrary(
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
                    addLibraryState = AddLibraryState.AddLibrarySuccess
                )
            )

    }

    @Test
    fun `state is correctly updated when there is an unexpected error`() =
        runTest {

            // Given
            addLibraryViewModel.handleEvent(Action.UpdateLibraryName(FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING))
            addLibraryViewModel.handleEvent(Action.UpdateLibraryUrlPath(LIBRARY_URL_PATH))

            // When
            addLibraryViewModel.handleEvent(
                Action.AddLibrary(
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
                        addLibraryState = AddLibraryState.Error(MessageProvider.SOMETHING_WENT_WRONG)
                    )
                )

        }

    @Test
    fun `state is correctly updated when an error is dismissed`() {

        // Given the current state is Error
        addLibraryViewModel.handleEvent(Action.UpdateLibraryName(FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING))
        addLibraryViewModel.handleEvent(Action.UpdateLibraryUrlPath(LIBRARY_URL_PATH))
        addLibraryViewModel.handleEvent(
            Action.AddLibrary(
                libraryName = FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING,
                libraryUrlPath = LIBRARY_URL_PATH
            )
        )

        // When
        addLibraryViewModel.handleEvent(Action.ErrorDismissed)

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