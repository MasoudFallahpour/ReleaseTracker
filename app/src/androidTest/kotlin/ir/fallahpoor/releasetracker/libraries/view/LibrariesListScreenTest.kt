package ir.fallahpoor.releasetracker.libraries.view

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.managers.NightModeManager
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.storage.LocalStorage
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import ir.fallahpoor.releasetracker.libraries.view.composables.SearchBarTags
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

// FIXME: Re-enable Hilt. I've disabled it because except the first test all other tests throw an exception
//  with the following message:
//  There are multiple DataStores active for the same file: /data/user/0/ir.fallahpoor.releasetracker/files/datastore/settings.preferences_pb. You should either maintain your DataStore as a singleton or confirm that there is no two DataStore's active on the same file (by confirming that the scope is cancelled).

@OptIn(ExperimentalCoroutinesApi::class)
class LibrariesListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var nightModeManager: NightModeManager
    private lateinit var libraryRepository: FakeLibraryRepository
    private lateinit var librariesViewModel: LibrariesViewModel
    private lateinit var preferencesCoroutineScope: CoroutineScope

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val deleteText = context.getString(R.string.delete)
    private val noLibrariesText = context.getString(R.string.no_libraries)
    private val searchText = context.getString(R.string.search)

    @Before
    fun runBeforeEachTest() {
        val storage = createLocalStorage()
        nightModeManager = NightModeManager(context, storage)
        libraryRepository = FakeLibraryRepository()
        librariesViewModel = LibrariesViewModel(
            libraryRepository = libraryRepository,
            storage = storage,
            exceptionParser = ExceptionParser()
        )
    }

    private fun createLocalStorage(): LocalStorage {
        preferencesCoroutineScope = CoroutineScope(TestCoroutineDispatcher() + Job())
        val dataStore = PreferenceDataStoreFactory.create(scope = preferencesCoroutineScope) {
            context.preferencesDataStoreFile("settings")
        }
        return LocalStorage(dataStore)
    }

    @After
    fun runAfterEachTest() {
        File(context.filesDir, "datastore").deleteRecursively()
        preferencesCoroutineScope.cancel()
    }

    @Test
    fun list_of_libraries_is_displayed() {

        // Given
        initializeLibrariesListScreen()

        // Then
        with(composeRule) {

            onNodeWithText(
                context.getString(
                    R.string.last_check_for_updates,
                    FakeLibraryRepository.LAST_UPDATE_CHECK
                )
            ).assertIsDisplayed()

            onNodeWithText(FakeLibraryRepository.Coil.name, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(FakeLibraryRepository.Coil.url, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(FakeLibraryRepository.Coil.version, useUnmergedTree = true)
                .assertIsDisplayed()
            onNode(
                isToggleableWithSiblingText(FakeLibraryRepository.Coil.name),
                useUnmergedTree = true
            ).assertIsOff()

            onNodeWithText(FakeLibraryRepository.Kotlin.name, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(FakeLibraryRepository.Kotlin.url, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(FakeLibraryRepository.Kotlin.version, useUnmergedTree = true)
                .assertIsDisplayed()
            onNode(
                isToggleableWithSiblingText(FakeLibraryRepository.Kotlin.name),
                useUnmergedTree = true
            ).assertIsOff()

            onNodeWithText(FakeLibraryRepository.Koin.name, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(FakeLibraryRepository.Koin.url, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(FakeLibraryRepository.Koin.version, useUnmergedTree = true)
                .assertIsDisplayed()
            onNode(
                isToggleableWithSiblingText(FakeLibraryRepository.Koin.name),
                useUnmergedTree = true
            ).assertIsOn()

            onNodeWithText(noLibrariesText)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    private fun isToggleableWithSiblingText(text: String): SemanticsMatcher =
        hasAnySibling(hasText(text)) and isToggleable()

    @Test
    fun when_list_of_libraries_is_empty_a_proper_text_is_displayed() {

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
    fun long_clicking_a_library_displays_the_delete_library_dialog() {

        // Given
        initializeLibrariesListScreen()

        // When
        composeRule.onNodeWithText(
            FakeLibraryRepository.Coil.name,
            useUnmergedTree = true
        ).performGesture {
            longClick()
        }

        // Then
        composeRule.onNodeWithText(deleteText)
            .assertIsDisplayed()

    }

    @Test
    fun delete_library_when_successful() {

        // Given
        val libraryName = FakeLibraryRepository.Coil.name
        val snackbarHostState = SnackbarHostState()
        initializeLibrariesListScreen(snackbarHostState)

        // When
        with(composeRule) {
            onNodeWithText(libraryName, useUnmergedTree = true).performGesture {
                longClick()
            }
            onNodeWithText(deleteText).performClick()
        }


        // Then
        composeRule.onNodeWithText(libraryName, useUnmergedTree = true)
            .assertDoesNotExist()
        assertSnackbarIsDisplayedWithMessage(
            snackbarHostState,
            context.getString(R.string.library_deleted)
        )

    }

    private fun assertSnackbarIsDisplayedWithMessage(
        snackbarHostState: SnackbarHostState,
        message: String
    ) = runTest {
        val actualSnackbarText = snapshotFlow { snackbarHostState.currentSnackbarData }
            .filterNotNull().first().message
        Truth.assertThat(actualSnackbarText).isEqualTo(message)
    }

    @Test
    fun delete_library_when_failed() {

        // Given
        val libraryName = FakeLibraryRepository.LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_DELETING
        val snackbarHostState = SnackbarHostState()
        initializeLibrariesListScreen(snackbarHostState)

        // When
        with(composeRule) {
            onNodeWithText(libraryName, useUnmergedTree = true).performGesture {
                longClick()
            }
            onNodeWithText(deleteText).performClick()
        }

        // Then
        composeRule.onNodeWithText(libraryName, useUnmergedTree = true)
            .assertIsDisplayed()
        assertSnackbarIsDisplayedWithMessage(
            snackbarHostState,
            FakeLibraryRepository.ERROR_MESSAGE
        )

    }

    @Test
    fun correct_night_mode_is_set_when_selecting_a_night_mode_from_night_mode_dialog() =
        runTest {

            // Given
            initializeLibrariesListScreen()
            nightModeManager.setNightMode(NightMode.OFF)

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
            Truth.assertThat(nightModeManager.currentNightMode).isEqualTo(NightMode.ON)

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
            onNodeWithText(FakeLibraryRepository.Coil.name, useUnmergedTree = true)
                .assertDoesNotExist()
            onNodeWithText(FakeLibraryRepository.Kotlin.name, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(FakeLibraryRepository.Koin.name, useUnmergedTree = true)
                .assertIsDisplayed()
        }

    }

    @Test
    fun when_there_is_no_search_result_a_proper_text_is_displayed() {

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
            onAllNodesWithTag(LibrariesListTags.LIBRARY_ITEM)
                .assertCountEquals(0)
            onNodeWithText(noLibrariesText)
                .assertIsDisplayed()
        }

    }

    @Test
    fun closing_the_search_bar_displays_all_libraries() {

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
            onNodeWithText(FakeLibraryRepository.Coil.name, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(FakeLibraryRepository.Kotlin.name, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(FakeLibraryRepository.Koin.name, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(noLibrariesText)
                .assertDoesNotExist()
            onNodeWithTag(LibrariesListTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
        }

    }

    @Test
    fun clearing_the_search_bar_displays_all_libraries() {

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
            onNodeWithText(FakeLibraryRepository.Coil.name, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(FakeLibraryRepository.Kotlin.name, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(FakeLibraryRepository.Koin.name, useUnmergedTree = true)
                .assertIsDisplayed()
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

    @OptIn(ExperimentalAnimationApi::class)
    private fun initializeLibrariesListScreen(snackbarHostState: SnackbarHostState = SnackbarHostState()) {
        composeRule.setContent {
            LibrariesListScreen(
                librariesViewModel = librariesViewModel,
                nightModeManager = nightModeManager,
                onLibraryClick = {},
                onAddLibraryClick = {},
                scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
            )
        }

    }

}