package ir.fallahpoor.releasetracker.libraries.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.repository.library.Library
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import ir.fallahpoor.releasetracker.fakes.FakeStorageRepository
import ir.fallahpoor.releasetracker.libraries.LibrariesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

// TODO: Use Hilt to inject the dependencies instead of creating them manually.

@OptIn(ExperimentalCoroutinesApi::class)
class LibrariesListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeLibraryRepository: FakeLibraryRepository
    private lateinit var fakeStorageRepository: FakeStorageRepository

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val searchText = context.getString(R.string.search)

    @Test
    fun screen_is_initialized_correctly() {

        // Given
        composeLibrariesListScreen()

        // Then
        with(composeRule) {
            onNodeWithTag(LibrariesListScreenTags.TOOLBAR)
                .assertIsDisplayed()
            onNodeWithTag(LibrariesListScreenTags.CONTENT)
                .assertIsDisplayed()
        }

    }

    @Test
    fun all_libraries_are_displayed() {

        // Given
        composeLibrariesListScreen()

        // Then
        with(composeRule) {
            fakeLibraryRepository.libraries.forEach {
                onNodeWithTag(
                    LibraryItemTags.LIBRARY_ITEM + it.name,
                    useUnmergedTree = true
                ).assertIsDisplayed()
            }
            onNodeWithText(LibrariesListTags.NO_LIBRARIES_TEXT)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListContentTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    @Test
    fun delete_library_only_deletes_the_selected_library() {

        // Given
        val libraryName = FakeLibraryRepository.Coil.name
        composeLibrariesListScreen()

        // When
        with(composeRule) {
            onNodeWithText(libraryName, useUnmergedTree = true).performTouchInput {
                swipeRight()
            }
        }

        // Then
        fakeLibraryRepository.libraries.forEach { library ->
            if (library.name == libraryName) {
                composeRule.onNodeWithTag(
                    LibraryItemTags.LIBRARY_ITEM + library.name,
                    useUnmergedTree = true
                ).assertDoesNotExist()
            } else {
                composeRule.onNodeWithTag(
                    LibraryItemTags.LIBRARY_ITEM + library.name,
                    useUnmergedTree = true
                ).assertIsDisplayed()
            }
        }

    }

    @Test
    fun search() {

        // Given
        composeLibrariesListScreen()

        // When
        with(composeRule) {
            onNodeWithContentDescription(searchText, useUnmergedTree = true)
                .performClick()
            onNodeWithTag(SearchBarTags.QUERY_TEXT_FIELD)
                .performTextInput("ko")
        }

        // Then
        fakeLibraryRepository.libraries.forEach { library ->
            if (library.name.contains("ko", ignoreCase = true)) {
                composeRule.onNodeWithTag(
                    LibraryItemTags.LIBRARY_ITEM + library.name,
                    useUnmergedTree = true
                ).assertIsDisplayed()
            } else {
                composeRule.onNodeWithTag(
                    LibraryItemTags.LIBRARY_ITEM + library.name,
                    useUnmergedTree = true
                ).assertDoesNotExist()
            }
        }

    }

    @Test
    fun all_libraries_are_displayed_when_search_bar_is_closed() {

        // Given
        composeLibrariesListScreen()
        with(composeRule) {
            onNodeWithContentDescription(searchText, useUnmergedTree = true)
                .performClick()
            onNodeWithTag(SearchBarTags.QUERY_TEXT_FIELD)
                .performTextInput("ko")
        }

        // When
        composeRule.onNodeWithTag(SearchBarTags.CLOSE_BUTTON)
            .performClick()

        // Then
        with(composeRule) {
            fakeLibraryRepository.libraries.forEach {
                onNodeWithTag(
                    LibraryItemTags.LIBRARY_ITEM + it.name,
                    useUnmergedTree = true
                ).assertIsDisplayed()
            }
            onNodeWithText(LibrariesListTags.NO_LIBRARIES_TEXT)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListContentTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    @Test
    fun all_libraries_are_displayed_when_search_bar_is_cleared() {

        // Given
        composeLibrariesListScreen()
        with(composeRule) {
            onNodeWithContentDescription(searchText, useUnmergedTree = true)
                .performClick()
            onNodeWithTag(SearchBarTags.QUERY_TEXT_FIELD)
                .performTextInput("ko")
        }

        // When
        composeRule.onNodeWithTag(SearchBarTags.CLEAR_BUTTON)
            .performClick()

        // Then
        with(composeRule) {
            fakeLibraryRepository.libraries.forEach {
                onNodeWithTag(
                    LibraryItemTags.LIBRARY_ITEM + it.name,
                    useUnmergedTree = true
                ).assertIsDisplayed()
            }
            onNodeWithText(LibrariesListTags.NO_LIBRARIES_TEXT)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListContentTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    @Test
    fun correct_callback_is_called_when_a_night_mode_is_selected() {

        // Given
        val onNightModeChange: (NightMode) -> Unit = mock()
        composeLibrariesListScreen(onNightModeChange = onNightModeChange)

        // When
        with(composeRule) {
            onNodeWithContentDescription(
                context.getString(R.string.more_options),
                useUnmergedTree = true
            ).performClick()
            onNodeWithText(context.getString(R.string.night_mode))
                .performClick()
            onNodeWithText(context.getString(NightMode.ON.label))
                .performClick()
        }

        // Then
        Mockito.verify(onNightModeChange).invoke(NightMode.ON)

    }

    @Test
    fun correct_callback_is_called_when_a_library_is_clicked() = runTest {

        // Given
        val onLibraryClick: (Library) -> Unit = mock()
        composeLibrariesListScreen(onLibraryClick = onLibraryClick)

        // When
        composeRule.onNodeWithTag(LibraryItemTags.LIBRARY_ITEM + FakeLibraryRepository.Kotlin.name)
            .performClick()

        // Then
        Mockito.verify(onLibraryClick).invoke(FakeLibraryRepository.Kotlin.library)

    }

    @Test
    fun correct_callback_is_called_when_add_library_button_is_clicked() {

        // Given
        val onAddLibraryClick: () -> Unit = mock()
        composeLibrariesListScreen(onAddLibraryClick = onAddLibraryClick)

        // When
        composeRule.onNodeWithTag(LibrariesListTags.ADD_LIBRARY_BUTTON)
            .performClick()

        // Then
        Mockito.verify(onAddLibraryClick).invoke()

    }

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

    private fun composeLibrariesListScreen(
        onNightModeChange: (NightMode) -> Unit = {},
        onLibraryClick: (Library) -> Unit = {},
        onAddLibraryClick: () -> Unit = {}
    ) {
        fakeLibraryRepository = FakeLibraryRepository()
        fakeStorageRepository = FakeStorageRepository()
        composeRule.setContent {
            LibrariesListScreen(
                librariesViewModel = LibrariesViewModel(
                    fakeLibraryRepository,
                    fakeStorageRepository
                ),
                currentNightMode = NightMode.AUTO,
                onNightModeChange = onNightModeChange,
                isNightModeSupported = true,
                onLibraryClick = onLibraryClick,
                onAddLibraryClick = onAddLibraryClick
            )
        }
    }

}