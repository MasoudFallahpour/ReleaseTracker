package ir.fallahpoor.releasetracker.features.libraries.ui

import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import ir.fallahpoor.releasetracker.data.repository.library.models.Library
import ir.fallahpoor.releasetracker.fakes.FakeData
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class LibraryItemTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun library_is_displayed() {

        // Given
        val library: Library = FakeData.Koin.library
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
        val library: Library = FakeData.Coil.library
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
            library = FakeData.Coil.library,
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
    fun correct_callback_is_called_when_library_is_unpinned() {

        // Given
        val onPinLibrary: (Library, Boolean) -> Unit = mock()
        val library: Library = FakeData.Koin.library
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
        val library: Library = FakeData.Coil.library
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