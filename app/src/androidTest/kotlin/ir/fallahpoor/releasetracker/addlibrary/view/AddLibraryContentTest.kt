package ir.fallahpoor.releasetracker.addlibrary.view

import android.content.Context
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.GITHUB_BASE_URL
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class AddLibraryContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val emptyLibraryNameErrorText = context.getString(R.string.library_name_empty)
    private val emptyLibraryUrlErrorText = context.getString(R.string.library_url_empty)
    private val invalidLibraryUrlErrorText = context.getString(R.string.library_url_invalid)

    @Test
    fun test_Initial_state() {

        // Given
        val libraryName = "Coil"
        val libraryUrlPath = "coil-kt/coil"
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = SnackbarHostState(),
                state = AddLibraryState.Initial,
                libraryName = libraryName,
                onLibraryNameChange = {},
                libraryUrlPath = libraryUrlPath,
                onLibraryUrlPathChange = {},
                onAddLibraryClick = {},
                onErrorDismissed = {}
            )
        }

        // Then
        with(composeTestRule) {
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
                .assertIsEnabled()
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON_TEXT, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.PROGRESS_INDICATOR, useUnmergedTree = true)
                .assertDoesNotExist()
            onNodeWithTag(AddLibraryTags.LIBRARY_NAME_TEXT_FIELD, useUnmergedTree = true)
                .assertTextEquals(libraryName)
            onNodeWithTag(AddLibraryTags.LIBRARY_URL_TEXT_FIELD, useUnmergedTree = true)
                .assertTextEquals(GITHUB_BASE_URL + libraryUrlPath)
            onNodeWithText(emptyLibraryNameErrorText)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryUrlErrorText)
                .assertDoesNotExist()
            onNodeWithText(invalidLibraryUrlErrorText)
                .assertDoesNotExist()
        }

    }

    @Test
    fun test_EmptyLibraryName_state() {

        // Given
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = SnackbarHostState(),
                state = AddLibraryState.EmptyLibraryName,
                libraryName = "",
                onLibraryNameChange = {},
                libraryUrlPath = "",
                onLibraryUrlPathChange = {},
                onAddLibraryClick = {},
                onErrorDismissed = {}
            )
        }

        // Then
        with(composeTestRule) {
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
                .assertIsEnabled()
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON_TEXT, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.PROGRESS_INDICATOR, useUnmergedTree = true)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryNameErrorText)
                .assertIsDisplayed()
            onNodeWithText(emptyLibraryUrlErrorText)
                .assertDoesNotExist()
            onNodeWithText(invalidLibraryUrlErrorText)
                .assertDoesNotExist()
        }

    }

    @Test
    fun test_EmptyLibraryUrl_state() {

        // Given
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = SnackbarHostState(),
                state = AddLibraryState.EmptyLibraryUrl,
                libraryName = "",
                onLibraryNameChange = {},
                libraryUrlPath = "",
                onLibraryUrlPathChange = {},
                onAddLibraryClick = {},
                onErrorDismissed = {}
            )
        }

        // Then
        with(composeTestRule) {
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
                .assertIsEnabled()
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON_TEXT, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.PROGRESS_INDICATOR, useUnmergedTree = true)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryNameErrorText)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryUrlErrorText)
                .assertIsDisplayed()
            onNodeWithText(invalidLibraryUrlErrorText)
                .assertDoesNotExist()
        }

    }

    @Test
    fun test_InvalidLibraryUrl_state() {

        // Given
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = SnackbarHostState(),
                state = AddLibraryState.InvalidLibraryUrl,
                libraryName = "",
                onLibraryNameChange = {},
                libraryUrlPath = "",
                onLibraryUrlPathChange = {},
                onAddLibraryClick = {},
                onErrorDismissed = {}
            )
        }

        // Then
        with(composeTestRule) {
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
                .assertIsEnabled()
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON_TEXT, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.PROGRESS_INDICATOR, useUnmergedTree = true)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryNameErrorText)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryUrlErrorText)
                .assertDoesNotExist()
            onNodeWithText(invalidLibraryUrlErrorText)
                .assertIsDisplayed()
        }

    }

    @Test
    fun test_InProgress_state() {

        // Given
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = SnackbarHostState(),
                state = AddLibraryState.InProgress,
                libraryName = "",
                onLibraryNameChange = {},
                libraryUrlPath = "",
                onLibraryUrlPathChange = {},
                onAddLibraryClick = {},
                onErrorDismissed = {}
            )
        }

        // Then
        with(composeTestRule) {
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
                .assertIsNotEnabled()
            onNodeWithTag(AddLibraryTags.PROGRESS_INDICATOR, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON_TEXT, useUnmergedTree = true)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryNameErrorText)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryUrlErrorText)
                .assertDoesNotExist()
            onNodeWithText(invalidLibraryUrlErrorText)
                .assertDoesNotExist()
        }

    }

    @Test
    fun test_LibraryAdded_state() = runTest {

        // Given
        val snackbarHostState = SnackbarHostState()
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = snackbarHostState,
                state = AddLibraryState.LibraryAdded,
                libraryName = "",
                onLibraryNameChange = {},
                libraryUrlPath = "",
                onLibraryUrlPathChange = {},
                onAddLibraryClick = {},
                onErrorDismissed = {}
            )
        }

        // Then
        with(composeTestRule) {
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
                .assertIsEnabled()
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON_TEXT, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryNameErrorText)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryUrlErrorText)
                .assertDoesNotExist()
            onNodeWithText(invalidLibraryUrlErrorText)
                .assertDoesNotExist()
        }
        val actualSnackbarText = snapshotFlow { snackbarHostState.currentSnackbarData }
            .filterNotNull().first().message
        val expectedSnackbarText = context.getString(R.string.library_added)
        Truth.assertThat(actualSnackbarText).isEqualTo(expectedSnackbarText)

    }

    @Test
    fun test_Error_state() = runTest {

        // Given
        val snackbarHostState = SnackbarHostState()
        val errorMessage = "Something went wrong"
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = snackbarHostState,
                state = AddLibraryState.Error(errorMessage),
                libraryName = "",
                onLibraryNameChange = {},
                libraryUrlPath = "",
                onLibraryUrlPathChange = {},
                onAddLibraryClick = {},
                onErrorDismissed = {}
            )
        }

        // Then
        with(composeTestRule) {
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
                .assertIsEnabled()
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON_TEXT, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.PROGRESS_INDICATOR)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryNameErrorText)
                .assertDoesNotExist()
            onNodeWithText(emptyLibraryUrlErrorText)
                .assertDoesNotExist()
            onNodeWithText(invalidLibraryUrlErrorText)
                .assertDoesNotExist()
        }
        val actualSnackbarText = snapshotFlow { snackbarHostState.currentSnackbarData }
            .filterNotNull().first().message
        Truth.assertThat(actualSnackbarText).isEqualTo(errorMessage)

    }

    @Test
    fun correct_callback_is_called_when_library_name_is_changed() {

        // Given
        val onLibraryNameChange: (String) -> Unit = mock()
        val libraryName = "Coil"
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = SnackbarHostState(),
                state = AddLibraryState.Initial,
                libraryName = "",
                onLibraryNameChange = onLibraryNameChange,
                libraryUrlPath = "",
                onLibraryUrlPathChange = {},
                onAddLibraryClick = {},
                onErrorDismissed = {}
            )
        }

        // When
        composeTestRule.onNodeWithTag(AddLibraryTags.LIBRARY_NAME_TEXT_FIELD)
            .performTextInput(libraryName)

        // Then
        Mockito.verify(onLibraryNameChange).invoke(libraryName)

    }

    @Test
    fun correct_callback_is_called_when_library_url_is_changed() {

        // Given
        val onLibraryUrlPathChange: (String) -> Unit = mock()
        val libraryUrl = "coil-kt/coil"
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = SnackbarHostState(),
                state = AddLibraryState.Initial,
                libraryName = "",
                onLibraryNameChange = {},
                libraryUrlPath = "",
                onLibraryUrlPathChange = onLibraryUrlPathChange,
                onAddLibraryClick = {},
                onErrorDismissed = {}
            )
        }

        // When
        composeTestRule.onNodeWithTag(AddLibraryTags.LIBRARY_URL_TEXT_FIELD)
            .performTextInput(libraryUrl)

        // Then
        Mockito.verify(onLibraryUrlPathChange).invoke(libraryUrl)

    }

    @Test
    fun correct_callback_is_called_when_add_library_button_is_clicked() {

        // Given
        val onAddLibraryClick: () -> Unit = mock()
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = SnackbarHostState(),
                state = AddLibraryState.Initial,
                libraryName = "Coil",
                onLibraryNameChange = {},
                libraryUrlPath = "coil-kt/coil",
                onLibraryUrlPathChange = {},
                onAddLibraryClick = onAddLibraryClick,
                onErrorDismissed = {}
            )
        }

        // When
        composeTestRule.onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
            .performClick()

        // Then
        Mockito.verify(onAddLibraryClick).invoke()

    }

    @Test
    fun correct_callback_is_called_when_error_is_dismissed() {

        // Given
        val snackbarHostState = SnackbarHostState()
        val onErrorDismissed: () -> Unit = mock()
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = snackbarHostState,
                state = AddLibraryState.Error("Something went wrong"),
                libraryName = "",
                onLibraryNameChange = {},
                libraryUrlPath = "",
                onLibraryUrlPathChange = {},
                onAddLibraryClick = {},
                onErrorDismissed = onErrorDismissed
            )
        }

        // When
        snackbarHostState.currentSnackbarData?.dismiss()

        // Then
        Mockito.verify(onErrorDismissed).invoke()

    }

}