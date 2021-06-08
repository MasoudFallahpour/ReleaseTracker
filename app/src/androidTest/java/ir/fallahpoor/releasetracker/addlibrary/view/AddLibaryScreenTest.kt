package ir.fallahpoor.releasetracker.addlibrary.view

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.InstrumentationRegistry
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.R
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalAnimationApi::class)
class AddLibraryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun test_fresh_state() {

        initializeAddLibraryScreen(AddLibraryState.Fresh)

        with(composeTestRule) {
            onNodeWithTag(context.getString(R.string.test_tag_add_library_progress_indicator))
                .assertDoesNotExist()

            // TODO Assert that Library name TextField is empty
            // TODO Assert that Library URL TextField is empty

            onNodeWithTag(context.getString(R.string.test_tag_add_library_add_library_button))
                .assertIsEnabled()

            onNodeWithText(context.getString(R.string.library_name_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_invalid))
                .assertDoesNotExist()
        }

    }

    @Test
    fun test_InProgress_state() {

        initializeAddLibraryScreen(AddLibraryState.InProgress)

        with(composeTestRule) {
            onNodeWithTag(context.getString(R.string.test_tag_add_library_progress_indicator))
                .assertIsDisplayed()

            onNodeWithTag(context.getString(R.string.test_tag_add_library_add_library_button))
                .assertIsNotEnabled()

            onNodeWithText(context.getString(R.string.library_name_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_invalid))
                .assertDoesNotExist()
        }

    }

    @Test
    fun test_EmptyLibraryName_state() {

        initializeAddLibraryScreen(AddLibraryState.EmptyLibraryName)

        with(composeTestRule) {
            onNodeWithTag(context.getString(R.string.test_tag_add_library_progress_indicator))
                .assertDoesNotExist()

            onNodeWithTag(context.getString(R.string.test_tag_add_library_add_library_button))
                .assertIsEnabled()

            onNodeWithText(context.getString(R.string.library_name_empty))
                .assertIsDisplayed()
            onNodeWithText(context.getString(R.string.library_url_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_invalid))
                .assertDoesNotExist()
        }

    }

    @Test
    fun test_EmptyLibraryUrl_state() {

        initializeAddLibraryScreen(AddLibraryState.EmptyLibraryUrl)

        with(composeTestRule) {
            onNodeWithTag(context.getString(R.string.test_tag_add_library_progress_indicator))
                .assertDoesNotExist()

            onNodeWithTag(context.getString(R.string.test_tag_add_library_add_library_button))
                .assertIsEnabled()

            onNodeWithText(context.getString(R.string.library_name_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_empty))
                .assertIsDisplayed()
            onNodeWithText(context.getString(R.string.library_url_invalid))
                .assertDoesNotExist()
        }

    }

    @Test
    fun test_InvalidLibraryUrl_state() {

        initializeAddLibraryScreen(AddLibraryState.InvalidLibraryUrl)

        with(composeTestRule) {
            onNodeWithTag(context.getString(R.string.test_tag_add_library_progress_indicator))
                .assertDoesNotExist()

            onNodeWithTag(context.getString(R.string.test_tag_add_library_add_library_button))
                .assertIsEnabled()

            onNodeWithText(context.getString(R.string.library_name_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_invalid))
                .assertIsDisplayed()
        }

    }

    @Test
    fun test_Error_state() {

        val snackbarHostState = SnackbarHostState()
        val errorMessage = "some error message"

        initializeAddLibraryScreen(
            addLibraryState = AddLibraryState.Error(errorMessage),
            snackbarHostState = snackbarHostState
        )

        with(composeTestRule) {
            onNodeWithTag(context.getString(R.string.test_tag_add_library_progress_indicator))
                .assertDoesNotExist()

            onNodeWithTag(context.getString(R.string.test_tag_add_library_add_library_button))
                .assertIsEnabled()

            onNodeWithText(context.getString(R.string.library_name_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_invalid))
                .assertDoesNotExist()

        }

        runBlocking {
            val snackbarText = snapshotFlow { snackbarHostState.currentSnackbarData }
                .filterNotNull().first().message
            Truth.assertThat(snackbarText).isEqualTo(errorMessage)
        }

    }

    @Test
    fun test_LibraryAdded_state() {

        val snackbarHostState = SnackbarHostState()

        initializeAddLibraryScreen(
            addLibraryState = AddLibraryState.LibraryAdded,
            snackbarHostState = snackbarHostState
        )

        with(composeTestRule) {
            onNodeWithTag(context.getString(R.string.test_tag_add_library_progress_indicator))
                .assertDoesNotExist()

            // TODO Assert that Library name TextField is empty
            // TODO Assert that Library URL TextField is empty

            onNodeWithTag(context.getString(R.string.test_tag_add_library_add_library_button))
                .assertIsEnabled()

            onNodeWithText(context.getString(R.string.library_name_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_empty))
                .assertDoesNotExist()
            onNodeWithText(context.getString(R.string.library_url_invalid))
                .assertDoesNotExist()

        }

        runBlocking {
            val actualSnackbarText = snapshotFlow { snackbarHostState.currentSnackbarData }
                .filterNotNull().first().message
            val expectedSnackbarText = context.getString(R.string.library_added)
            Truth.assertThat(actualSnackbarText).isEqualTo(expectedSnackbarText)
        }

    }

    private fun initializeAddLibraryScreen(
        addLibraryState: AddLibraryState,
        snackbarHostState: SnackbarHostState = SnackbarHostState()
    ) {

        composeTestRule.setContent {
            AddLibraryScreen(
                isDarkTheme = false,
                addLibraryState = addLibraryState,
                libraryName = "",
                onLibraryNameChange = {},
                libraryUrl = "",
                onLibraryUrlChange = {},
                onBackClick = {},
                onAddLibrary = {},
                scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
            )
        }

    }

}