package ir.fallahpoor.releasetracker.libraries.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import ir.fallahpoor.releasetracker.NightModeViewModel
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.storage.LocalStorage
import ir.fallahpoor.releasetracker.data.utils.storage.Storage
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import ir.fallahpoor.releasetracker.libraries.LibrariesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
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

    private lateinit var nightModeViewModel: NightModeViewModel
    private lateinit var libraryRepository: FakeLibraryRepository
    private lateinit var librariesViewModel: LibrariesViewModel
    private lateinit var preferencesCoroutineScope: CoroutineScope

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val noLibrariesText = context.getString(R.string.no_libraries)
    private val searchText = context.getString(R.string.search)

    @After
    fun runAfterEachTest() {
        preferencesCoroutineScope.cancel()
    }

    @Test
    fun screen_is_initialized_correctly() {

        // Given
        initializeLibrariesListScreen()

        // Then
        with(composeRule) {
            onNodeWithTag(LibrariesListScreenTags.TOOLBAR)
                .assertIsDisplayed()
            onNodeWithTag(LibrariesListScreenTags.CONTENT)
                .assertIsDisplayed()
        }

    }

    @Test
    fun list_of_libraries_is_displayed() {

        // Given
        initializeLibrariesListScreen()

        // Then
        with(composeRule) {
            onNodeWithTag(LibrariesListTags.LAST_UPDATE_CHECK_TEXT)
                .assertIsDisplayed()
            libraryRepository.libraries.forEach {
                onNodeWithTag(LibrariesListTags.LIBRARY_ITEM + it.name, useUnmergedTree = true)
                    .assertIsDisplayed()
            }
            onNodeWithText(noLibrariesText)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    private fun isToggleableWithSiblingText(text: String): SemanticsMatcher =
        hasAnySibling(hasText(text)) and isToggleable()

    @Test
    fun a_proper_message_is_displayed_when_list_of_libraries_is_empty() {

        // Given
        initializeLibrariesListScreen()
        libraryRepository.deleteLibraries()

        // Then
        with(composeRule) {
            onAllNodesWithTag(LibrariesListTags.LIBRARY_ITEM)
                .assertCountEquals(0)
            onNodeWithText(noLibrariesText)
                .assertIsDisplayed()
            onNodeWithTag(LibrariesListTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    @Test
    fun delete_library_only_deletes_the_selected_library() {

        // Given
        val libraryName = FakeLibraryRepository.Coil.name
        initializeLibrariesListScreen()

        // When
        with(composeRule) {
            onNodeWithText(libraryName, useUnmergedTree = true).performTouchInput {
                swipeRight()
            }
        }

        // Then
        libraryRepository.libraries.forEach { library ->
            if (library.name == libraryName) {
                composeRule.onNodeWithTag(
                    LibrariesListTags.LIBRARY_ITEM + library.name,
                    useUnmergedTree = true
                ).assertDoesNotExist()
            } else {
                composeRule.onNodeWithTag(
                    LibrariesListTags.LIBRARY_ITEM + library.name,
                    useUnmergedTree = true
                ).assertIsDisplayed()
            }
        }

    }

    @Test
    fun search() {

        // Given
        initializeLibrariesListScreen()

        // When
        with(composeRule) {
            onNodeWithContentDescription(searchText, useUnmergedTree = true)
                .performClick()
            onNodeWithTag(SearchBarTags.QUERY_TEXT_FIELD)
                .performTextInput("ko")
        }

        // Then
        with(composeRule) {
            onNodeWithTag(
                LibrariesListTags.LIBRARY_ITEM + FakeLibraryRepository.Coil.name,
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag(
                LibrariesListTags.LIBRARY_ITEM + FakeLibraryRepository.Kotlin.name,
                useUnmergedTree = true
            ).assertIsDisplayed()
            onNodeWithTag(
                LibrariesListTags.LIBRARY_ITEM + FakeLibraryRepository.Koin.name,
                useUnmergedTree = true
            ).assertIsDisplayed()
        }

    }

    @Test
    fun a_proper_text_is_displayed_when_there_is_no_search_result() {

        // Given
        initializeLibrariesListScreen()

        // When
        with(composeRule) {
            onNodeWithContentDescription(searchText, useUnmergedTree = true)
                .performClick()
            onNodeWithTag(SearchBarTags.QUERY_TEXT_FIELD)
                .performTextInput("this will not match any library!")
        }

        // Then
        with(composeRule) {
            // TODO assert that there is no composable with its test tag starting with LIBRARY_ITEM
            onNodeWithText(noLibrariesText)
                .assertIsDisplayed()
        }

    }

    @Test
    fun all_libraries_are_displayed_when_search_bar_is_closed() {

        // Given
        initializeLibrariesListScreen()
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
            libraryRepository.libraries.forEach {
                onNodeWithTag(LibrariesListTags.LIBRARY_ITEM + it.name, useUnmergedTree = true)
                    .assertIsDisplayed()
            }
            onNodeWithText(noLibrariesText)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
            onNodeWithText(noLibrariesText)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    @Test
    fun all_libraries_are_displayed_when_search_bar_is_cleared() {

        // Given
        initializeLibrariesListScreen()
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
            libraryRepository.libraries.forEach {
                onNodeWithTag(LibrariesListTags.LIBRARY_ITEM + it.name, useUnmergedTree = true)
                    .assertIsDisplayed()
            }
            onNodeWithText(noLibrariesText)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
            onNodeWithText(noLibrariesText)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    @Test
    fun pin_library() {

        // Given
        initializeLibrariesListScreen()

        // When
        composeRule.onNode(
            isToggleableWithSiblingText(FakeLibraryRepository.Coil.name),
            useUnmergedTree = true
        ).performClick()

        // Then
        composeRule.onNode(
            isToggleableWithSiblingText(FakeLibraryRepository.Coil.name),
            useUnmergedTree = true
        ).assertIsOn()

    }

    @Test
    fun unpin_library() {

        // Given
        initializeLibrariesListScreen()

        // When
        composeRule.onNode(
            isToggleableWithSiblingText(FakeLibraryRepository.Koin.name),
            useUnmergedTree = true
        ).performClick()

        // Then
        composeRule.onNode(
            isToggleableWithSiblingText(FakeLibraryRepository.Koin.name),
            useUnmergedTree = true
        ).assertIsOff()

    }

    @Test
    fun correct_callback_is_called_when_a_night_mode_is_selected() {

        // Given
        val onNightModeChange: (NightMode) -> Unit = mock()
        initializeLibrariesListScreen(onNightModeChange = onNightModeChange)

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
        initializeLibrariesListScreen(onLibraryClick = onLibraryClick)
        val library = libraryRepository.getLibrary(FakeLibraryRepository.Kotlin.name)!!

        // When
        with(composeRule) {
            onNodeWithTag(LibrariesListTags.LIBRARY_ITEM + library.name)
                .performClick()
        }

        // Then
        Mockito.verify(onLibraryClick).invoke(library)

    }

    @Test
    fun correct_callback_is_called_when_add_library_button_is_clicked() {

        // Given
        val onAddLibraryClick: () -> Unit = mock()
        initializeLibrariesListScreen(onAddLibraryClick = onAddLibraryClick)

        // When
        with(composeRule) {
            onNodeWithTag(LibrariesListTags.ADD_LIBRARY_BUTTON)
                .performClick()
        }

        // Then
        Mockito.verify(onAddLibraryClick).invoke()

    }

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

    private fun initializeLibrariesListScreen(
        onNightModeChange: (NightMode) -> Unit = {},
        onLibraryClick: (Library) -> Unit = {},
        onAddLibraryClick: () -> Unit = {}
    ) {

        val storage: Storage = createStorage()
        nightModeViewModel = NightModeViewModel(storage)
        libraryRepository = FakeLibraryRepository()
        librariesViewModel = LibrariesViewModel(
            libraryRepository = libraryRepository,
            storage = storage
        )

        composeRule.setContent {
            LibrariesListScreen(
                librariesViewModel = librariesViewModel,
                currentNightMode = NightMode.AUTO,
                onNightModeChange = onNightModeChange,
                isNightModeSupported = true,
                onLibraryClick = onLibraryClick,
                onAddLibraryClick = onAddLibraryClick
            )
        }
    }

    private fun createStorage(): LocalStorage {
        preferencesCoroutineScope = CoroutineScope(UnconfinedTestDispatcher() + Job())
        val dataStore = PreferenceDataStoreFactory.create(scope = preferencesCoroutineScope) {
            context.preferencesDataStoreFile("settings_test")
        }
        return LocalStorage(dataStore)
    }

}