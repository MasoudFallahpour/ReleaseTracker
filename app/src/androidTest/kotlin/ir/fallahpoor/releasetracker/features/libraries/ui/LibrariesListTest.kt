package ir.fallahpoor.releasetracker.features.libraries.ui

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.repository.library.Library
import ir.fallahpoor.releasetracker.fakes.FakeData
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class LibrariesListTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val libraries = listOf(
        FakeData.Coil.library,
        FakeData.Koin.library,
        FakeData.Kotlin.library
    )

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun list_of_libraries_is_displayed() {

        // Given
        val libraries = listOf(
            FakeData.Coil.library,
            FakeData.Koin.library,
            FakeData.Kotlin.library
        )
        composeLibrariesList(libraries = libraries)

        // Then
        with(composeRule) {
            libraries.forEach {
                onNodeWithTag(
                    LibraryItemTags.LIBRARY_ITEM + it.name,
                    useUnmergedTree = true
                ).assertIsDisplayed()
            }
            onNodeWithTag(LibrariesListTags.ADD_LIBRARY_BUTTON)
                .assertIsDisplayed()
            onNodeWithText(context.getString(R.string.no_libraries), useUnmergedTree = true)
                .assertDoesNotExist()
        }

    }

    @Test
    fun a_proper_message_is_displayed_when_list_of_libraries_is_empty() {

        // Given
        composeLibrariesList(libraries = emptyList())

        // Then
        with(composeRule) {
            onNodeWithTag(LibrariesListTags.LIBRARIES_LIST)
                .onChildren()
                .assertCountEquals(2)
            onNodeWithText(context.getString(R.string.no_libraries), useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(LibrariesListTags.ADD_LIBRARY_BUTTON)
                .assertIsDisplayed()
        }

    }

    @Test
    fun correct_callback_is_called_when_a_library_is_clicked() {

        // Given
        val library: Library = FakeData.Kotlin.library
        val onLibraryClick: (Library) -> Unit = mock()
        composeLibrariesList(
            libraries = libraries,
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
        composeLibrariesList(
            libraries = libraries,
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
        val library: Library = FakeData.Coil.library
        val onPinLibrary: (Library, Boolean) -> Unit = mock()
        composeLibrariesList(
            libraries = libraries,
            onPinLibraryClick = onPinLibrary
        )

        // When
        composeRule.onNodeWithTag(LibraryItemTags.PIN_BUTTON + library.name)
            .performClick()

        // Then
        Mockito.verify(onPinLibrary).invoke(library, true)

    }

    @Test
    fun correct_callback_is_called_when_a_library_is_unpinned() {

        // Given
        val library: Library = FakeData.Koin.library
        val onPinLibrary: (Library, Boolean) -> Unit = mock()
        composeLibrariesList(
            libraries = libraries,
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
    fun correct_callback_is_called_when_add_library_button_is_clicked() {

        // Given
        val onAddLibraryLick: () -> Unit = mock()
        composeLibrariesList(
            libraries = libraries,
            onAddLibraryClick = onAddLibraryLick
        )

        // When
        composeRule.onNodeWithTag(LibrariesListTags.ADD_LIBRARY_BUTTON)
            .performClick()

        // Then
        Mockito.verify(onAddLibraryLick).invoke()

    }

    private fun composeLibrariesList(
        libraries: List<Library>,
        onLibraryClick: (Library) -> Unit = {},
        onLibraryDismissed: (Library) -> Unit = {},
        onPinLibraryClick: (Library, Boolean) -> Unit = { _, _ -> },
        onAddLibraryClick: () -> Unit = {}
    ) {
        composeRule.setContent {
            LibrariesList(
                libraries = libraries,
                onLibraryClick = onLibraryClick,
                onLibraryDismissed = onLibraryDismissed,
                onPinLibraryClick = onPinLibraryClick,
                onAddLibraryClick = onAddLibraryClick
            )
        }
    }

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

}