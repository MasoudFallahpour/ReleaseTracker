package ir.fallahpoor.releasetracker.features.libraries.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ir.fallahpoor.releasetracker.data.repository.library.Library
import ir.fallahpoor.releasetracker.fakes.FakeData
import ir.fallahpoor.releasetracker.features.libraries.LibrariesListState
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class LibrariesListContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val libraries = listOf(
        FakeData.Coil.library,
        FakeData.Koin.library,
        FakeData.Kotlin.library
    )

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
            onNodeWithTag(LibrariesListTags.LIBRARIES_LIST)
                .assertDoesNotExist()
        }

    }

    @Test
    fun list_of_libraries_is_displayed() {

        // Given
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(
                libraries
            )
        )

        // Then
        with(composeRule) {
            onNodeWithTag(LibrariesListContentTags.LAST_UPDATE_CHECK_TEXT)
                .assertIsDisplayed()
            onNodeWithTag(LibrariesListTags.LIBRARIES_LIST)
                .assertIsDisplayed()
            onNodeWithTag(LibrariesListContentTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    @Test
    fun correct_callback_is_called_when_a_library_is_clicked() {

        // Given
        val library: Library = FakeData.Kotlin.library
        val onLibraryClick: (Library) -> Unit = mock()
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(libraries),
            onLibraryClick = onLibraryClick
        )

        // When
        composeRule.onNodeWithTag(LibraryItemTags.LIBRARY_ITEM + library.name)
            .performClick()

        // Then
        Mockito.verify(onLibraryClick).invoke(library)

    }

    @Test
    fun correct_callback_is_called_when_a_library_is_dismissed() {

        // Given
        val library: Library = FakeData.Coil.library
        val onLibraryDismissed: (Library) -> Unit = mock()
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(libraries),
            onLibraryDismissed = onLibraryDismissed
        )

        // When
        composeRule.onNodeWithTag(LibraryItemTags.LIBRARY_ITEM + library.name)
            .performTouchInput {
                swipeRight()
            }

        // Then
        Mockito.verify(onLibraryDismissed).invoke(library)

    }

    @Test
    fun correct_callback_is_called_when_a_library_is_pinned() {

        // Given
        val onPinLibrary: (Library, Boolean) -> Unit = mock()
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(libraries),
            onPinLibraryClick = onPinLibrary
        )

        // When
        composeRule.onNodeWithTag(
            LibraryItemTags.PIN_BUTTON + FakeData.Coil.name
        ).performClick()

        // Then
        Mockito.verify(onPinLibrary).invoke(FakeData.Coil.library, true)

    }

    @Test
    fun correct_callback_is_called_when_a_library_is_unpinned() {

        // Given
        val onPinLibrary: (Library, Boolean) -> Unit = mock()
        composeLibrariesListContent(
            librariesListState = LibrariesListState.LibrariesLoaded(libraries),
            onPinLibraryClick = onPinLibrary
        )

        // When
        composeRule.onNodeWithTag(
            LibraryItemTags.PIN_BUTTON + FakeData.Koin.name
        ).performClick()

        // Then
        Mockito.verify(onPinLibrary).invoke(FakeData.Koin.library, false)

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