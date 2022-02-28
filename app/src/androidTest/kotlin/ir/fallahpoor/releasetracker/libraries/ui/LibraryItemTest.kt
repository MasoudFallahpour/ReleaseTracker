package ir.fallahpoor.releasetracker.libraries.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class LibraryItemTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun library_is_displayed() {

        // Given
        val library: Library = FakeLibraryRepository.Koin.library
        composeLibraryItem(library = library)

        // Then
        with(composeRule) {
            onNodeWithTag(LibraryItemTags.LIBRARY_NAME, useUnmergedTree = true)
                .assertTextEquals(library.name)
            onNodeWithTag(LibraryItemTags.LIBRARY_URL, useUnmergedTree = true)
                .assertTextEquals(library.url)
            onNodeWithTag(LibraryItemTags.LIBRARY_VERSION, useUnmergedTree = true)
                .assertTextEquals(library.version)
            onNodeWithTag(LibraryItemTags.PIN_BUTTON + library.name)
                .assertIsOn()
        }

    }

    @Test
    fun correct_callback_is_called_when_library_is_clicked() {

        // Given
        val onLibraryClick: (Library) -> Unit = mock()
        val library: Library = FakeLibraryRepository.Coil.library
        composeLibraryItem(
            library = library,
            onLibraryClick = onLibraryClick
        )

        // When
        composeRule.onNodeWithTag(LibraryItemTags.LIBRARY_ITEM + library.name)
            .performClick()

        // Then
        Mockito.verify(onLibraryClick).invoke(library)

    }

    @Test
    fun correct_callback_is_called_when_library_is_pinned() {

        // Given
        val onPinLibrary: (Library, Boolean) -> Unit = mock()
        composeLibraryItem(
            library = FakeLibraryRepository.Coil.library,
            onPinLibraryClick = onPinLibrary
        )

        // When
        composeRule.onNodeWithTag(
            LibraryItemTags.PIN_BUTTON + FakeLibraryRepository.Coil.name
        ).performClick()

        // Then
        Mockito.verify(onPinLibrary).invoke(FakeLibraryRepository.Coil.library, true)

    }

    @Test
    fun correct_callback_is_called_when_library_is_unpinned() {

        // Given
        val onPinLibrary: (Library, Boolean) -> Unit = mock()
        val library: Library = FakeLibraryRepository.Koin.library
        composeLibraryItem(
            library = library,
            onPinLibraryClick = onPinLibrary
        )

        // When
        composeRule.onNodeWithTag(
            LibraryItemTags.PIN_BUTTON + library.name
        ).performClick()

        // Then
        Mockito.verify(onPinLibrary).invoke(library, false)

    }

    @Test
    fun correct_callback_is_called_when_library_is_dismissed() {

        // Given
        val onLibraryDismissed: (Library) -> Unit = mock()
        val library: Library = FakeLibraryRepository.Coil.library
        composeLibraryItem(
            library = library,
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

    private fun composeLibraryItem(
        library: Library,
        onLibraryClick: (Library) -> Unit = {},
        onPinLibraryClick: (Library, Boolean) -> Unit = { _, _ -> },
        onLibraryDismissed: (Library) -> Unit = {}
    ) {
        composeRule.setContent {
            LibraryItem(
                library = library,
                onLibraryClick = onLibraryClick,
                onPinLibraryClick = onPinLibraryClick,
                onLibraryDismissed = onLibraryDismissed
            )
        }
    }

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

}