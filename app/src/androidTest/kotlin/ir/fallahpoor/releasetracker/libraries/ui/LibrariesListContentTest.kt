package ir.fallahpoor.releasetracker.libraries.ui

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import ir.fallahpoor.releasetracker.libraries.LibrariesListState
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class LibrariesListContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun test_loading_state() {

        // Given
        composeLibrariesListContent(librariesListState = LibrariesListState.Loading)

        // Then
        with(composeRule) {
            composeRule.onNodeWithTag(LibrariesListContentTags.LAST_UPDATE_CHECK_TEXT)
                .assertIsDisplayed()
            onNodeWithTag(LibrariesListContentTags.PROGRESS_INDICATOR)
                .assertIsDisplayed()
            onNodeWithTag(LibrariesListContentTags.LIBRARIES_LIST)
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.no_libraries), useUnmergedTree = true)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListContentTags.ADD_LIBRARY_BUTTON)
                .assertDoesNotExist()
        }

    }

    @Test
    fun list_of_libraries_is_displayed() {

        // Given
        val libraries = listOf(
            FakeLibraryRepository.Coil.library,
            FakeLibraryRepository.Koin.library,
            FakeLibraryRepository.Kotlin.library
        )
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(
                libraries
            )
        )

        // Then
        with(composeRule) {
            onNodeWithTag(LibrariesListContentTags.LAST_UPDATE_CHECK_TEXT)
                .assertIsDisplayed()
            libraries.forEach {
                onNodeWithTag(
                    LibrariesListContentTags.LIBRARY_ITEM + it.name,
                    useUnmergedTree = true
                ).assertIsDisplayed()
            }
            onNodeWithTag(LibrariesListContentTags.ADD_LIBRARY_BUTTON)
                .assertIsDisplayed()
            onNodeWithText(context.getString(R.string.no_libraries), useUnmergedTree = true)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListContentTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    @Test
    fun a_proper_message_is_displayed_when_list_of_libraries_is_empty() {

        // Given
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(
                emptyList()
            )
        )

        // Then
        with(composeRule) {
            composeRule.onNodeWithTag(LibrariesListContentTags.LAST_UPDATE_CHECK_TEXT)
                .assertIsDisplayed()
            onNodeWithTag(LibrariesListContentTags.LIBRARIES_LIST)
                .onChildren()
                .assertCountEquals(2)
            onNodeWithText(context.getString(R.string.no_libraries), useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(LibrariesListContentTags.ADD_LIBRARY_BUTTON)
                .assertIsDisplayed()
            onNodeWithTag(LibrariesListContentTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    @Test
    fun correct_callback_is_called_when_a_library_is_clicked() {

        // Given
        val onLibraryClick: (Library) -> Unit = mock()
        val libraries = listOf(
            FakeLibraryRepository.Coil.library,
            FakeLibraryRepository.Koin.library,
            FakeLibraryRepository.Kotlin.library
        )
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(libraries),
            onLibraryClick = onLibraryClick
        )

        // When
        composeRule.onNodeWithTag(LibrariesListContentTags.LIBRARY_ITEM + FakeLibraryRepository.Kotlin.name)
            .performClick()

        // Then
        Mockito.verify(onLibraryClick).invoke(FakeLibraryRepository.Kotlin.library)

    }

    @Test
    fun correct_callback_is_called_when_a_library_is_dismissed() {

        // Given
        val onLibraryDismissed: (Library) -> Unit = mock()
        val libraries = listOf(
            FakeLibraryRepository.Coil.library,
            FakeLibraryRepository.Koin.library,
            FakeLibraryRepository.Kotlin.library
        )
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(libraries),
            onLibraryDismissed = onLibraryDismissed
        )

        // When
        composeRule.onNodeWithTag(LibrariesListContentTags.LIBRARY_ITEM + FakeLibraryRepository.Coil.name)
            .performTouchInput {
                swipeRight()
            }

        // Then
        Mockito.verify(onLibraryDismissed).invoke(FakeLibraryRepository.Coil.library)

    }

    @Test
    fun correct_callback_is_called_when_a_library_is_pinned() {

        // Given
        val onPinLibrary: (Library, Boolean) -> Unit = mock()
        val libraries = listOf(
            FakeLibraryRepository.Coil.library,
            FakeLibraryRepository.Koin.library,
            FakeLibraryRepository.Kotlin.library
        )
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(libraries),
            onPinLibraryClick = onPinLibrary
        )

        // When
        composeRule.onNodeWithTag(
            LibrariesListContentTags.PIN_BUTTON + FakeLibraryRepository.Coil.name
        ).performClick()

        // Then
        Mockito.verify(onPinLibrary).invoke(FakeLibraryRepository.Coil.library, true)

    }

    @Test
    fun correct_callback_is_called_when_a_library_is_unpinned() {

        // Given
        val onPinLibrary: (Library, Boolean) -> Unit = mock()
        val libraries = listOf(
            FakeLibraryRepository.Coil.library,
            FakeLibraryRepository.Koin.library,
            FakeLibraryRepository.Kotlin.library
        )
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(libraries),
            onPinLibraryClick = onPinLibrary
        )

        // When
        composeRule.onNodeWithTag(
            LibrariesListContentTags.PIN_BUTTON + FakeLibraryRepository.Koin.name
        ).performClick()

        // Then
        Mockito.verify(onPinLibrary).invoke(FakeLibraryRepository.Koin.library, false)

    }

    @Test
    fun correct_callback_is_called_when_add_library_button_is_clicked() {

        // Given
        val onAddLibraryLick: () -> Unit = mock()
        val libraries = listOf(
            FakeLibraryRepository.Coil.library,
            FakeLibraryRepository.Koin.library,
            FakeLibraryRepository.Kotlin.library
        )
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(libraries),
            onAddLibraryClick = onAddLibraryLick
        )

        // When
        composeRule.onNodeWithTag(LibrariesListContentTags.ADD_LIBRARY_BUTTON)
            .performClick()

        // Then
        Mockito.verify(onAddLibraryLick).invoke()

    }

    private fun composeLibrariesListContent(
        librariesListState: LibrariesListState = LibrariesListState.Loading,
        lastUpdateCheck: String = "N/A",
        onLibraryClick: (Library) -> Unit = {},
        onLibraryDismissed: (Library) -> Unit = {},
        onPinLibraryClick: (Library, Boolean) -> Unit = { _, _ -> },
        onAddLibraryClick: () -> Unit = {}
    ) {
        composeRule.setContent {
            LibrariesListContent(
                librariesListState = librariesListState,
                lastUpdateCheck = lastUpdateCheck,
                onLibraryClick = onLibraryClick,
                onLibraryDismissed = onLibraryDismissed,
                onPinLibraryClick = onPinLibraryClick,
                onAddLibraryClick = onAddLibraryClick
            )
        }
    }

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

}