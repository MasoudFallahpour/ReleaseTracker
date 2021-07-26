package ir.fallahpoor.releasetracker.libraries.view

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.managers.NightModeManager
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.data.utils.storage.Storage
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
import ir.fallahpoor.releasetracker.testfakes.FakeLibraryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class LibrariesListScreenTest {

    @get:Rule(order = 0)
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @get:Rule(order = 2)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var nightModeManager: NightModeManager

    @Inject
    lateinit var storage: Storage

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val deleteText = context.getString(R.string.delete)
    private val noLibrariesText = context.getString(R.string.no_libraries)
    private val searchText = context.getString(R.string.search)
    private val progressIndicatorTestTag =
        context.getString(R.string.test_tag_add_library_progress_indicator)
    private val searchBarQueryTextFieldTestTag =
        context.getString(R.string.test_tag_search_bar_query_text_field)
    private val libraryItemTestTag =
        context.getString(R.string.test_tag_libraries_list_library_item)

    private lateinit var libraryRepository: FakeLibraryRepository

    @Before
    fun runBeforeEachTest() {
        hiltAndroidRule.inject()
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
            onNodeWithTag(progressIndicatorTestTag)
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
            onAllNodesWithTag(libraryItemTestTag)
                .assertCountEquals(0)
            onNodeWithText(noLibrariesText)
                .assertIsDisplayed()
            onNodeWithTag(progressIndicatorTestTag)
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

//    @Test
//    fun clicking_a_library_opens_its_url_in_a_browser() {
//    }

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
    ) {
        runBlockingTest {
            val actualSnackbarText = snapshotFlow { snackbarHostState.currentSnackbarData }
                .filterNotNull().first().message
            Truth.assertThat(actualSnackbarText).isEqualTo(message)
        }
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

    // FIXME Find out why the following test fails with exception
    //  JobCancellationException: StandaloneCoroutine was cancelled
//    @Test
//    fun correct_night_mode_is_set_when_selecting_a_night_mode_from_night_mode_dialog() =
//        runBlockingTest {
//
//            // Given
//            initializeLibrariesListScreen()
//            nightModeManager.setNightMode(NightMode.OFF)
//
//            // When
//            with(composeTestRule) {
//                onNodeWithContentDescription(
//                    context.getString(R.string.more_options),
//                    useUnmergedTree = true
//                ).performClick()
//                onNodeWithText(context.getString(R.string.night_mode))
//                    .performClick()
//                onNodeWithText(context.getString(R.string.on))
//                    .performClick()
//            }
//
//            // Then
//            Truth.assertThat(nightModeManager.currentNightMode).isEqualTo(NightMode.ON)
//
//        }

    @Test
    fun search() {

        // Given
        initializeLibrariesListScreen()

        // When
        with(composeRule) {
            onNodeWithContentDescription(searchText, useUnmergedTree = true)
                .performClick()
            onNodeWithTag(searchBarQueryTextFieldTestTag)
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
            onNodeWithTag(context.getString(R.string.test_tag_search_bar_query_text_field))
                .performTextInput("this will not match any library!")
        }

        // Then
        with(composeRule) {
            onAllNodesWithTag(libraryItemTestTag)
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
            onNodeWithTag(searchBarQueryTextFieldTestTag)
                .performTextInput("ko")
        }

        // When
        composeRule.onNodeWithTag(context.getString(R.string.test_tag_search_bar_close_button))
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
            onNodeWithTag(progressIndicatorTestTag)
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

        libraryRepository = FakeLibraryRepository()

        val librariesViewModel = LibrariesViewModel(
            libraryRepository = libraryRepository,
            storage = storage,
            exceptionParser = ExceptionParser()
        )

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