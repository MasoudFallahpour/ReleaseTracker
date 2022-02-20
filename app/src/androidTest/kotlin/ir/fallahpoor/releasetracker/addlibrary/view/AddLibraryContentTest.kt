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
        composeAddLibraryContent(libraryName = "Coil", libraryUrlPath = "coil-kt/coil")

        // Then
        with(composeTestRule) {
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
                .assertIsEnabled()
            onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON_TEXT, useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.PROGRESS_INDICATOR, useUnmergedTree = true)
                .assertDoesNotExist()
            onNodeWithTag(AddLibraryTags.LIBRARY_NAME_TEXT_FIELD, useUnmergedTree = true)
                .assertTextEquals("Coil")
            onNodeWithTag(AddLibraryTags.LIBRARY_URL_TEXT_FIELD, useUnmergedTree = true)
                .assertTextEquals(GITHUB_BASE_URL + "coil-kt/coil")
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
        composeAddLibraryContent(state = AddLibraryState.EmptyLibraryName)

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
        composeAddLibraryContent(state = AddLibraryState.EmptyLibraryUrl)

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
        composeAddLibraryContent(state = AddLibraryState.InvalidLibraryUrl)

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
        composeAddLibraryContent(state = AddLibraryState.InProgress)

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
        composeAddLibraryContent(
            state = AddLibraryState.LibraryAdded,
            snackbarHostState = snackbarHostState
        )

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
        composeAddLibraryContent(
            state = AddLibraryState.Error("Something went wrong"),
            snackbarHostState = snackbarHostState
        )

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
        Truth.assertThat(actualSnackbarText).isEqualTo("Something went wrong")

    }

    @Test
    fun correct_callback_is_called_when_library_name_is_changed() {

        // Given
        val onLibraryNameChange: (String) -> Unit = mock()
        composeAddLibraryContent(onLibraryNameChange = onLibraryNameChange)

        // When
        composeTestRule.onNodeWithTag(AddLibraryTags.LIBRARY_NAME_TEXT_FIELD)
            .performTextInput("Coil")

        // Then
        Mockito.verify(onLibraryNameChange).invoke("Coil")

    }

    @Test
    fun correct_callback_is_called_when_library_url_is_changed() {

        // Given
        val onLibraryUrlPathChange: (String) -> Unit = mock()
        composeAddLibraryContent(onLibraryUrlPathChange = onLibraryUrlPathChange)

        // When
        composeTestRule.onNodeWithTag(AddLibraryTags.LIBRARY_URL_TEXT_FIELD)
            .performTextInput("coil-kt/coil")

        // Then
        Mockito.verify(onLibraryUrlPathChange).invoke("coil-kt/coil")

    }

    @Test
    fun correct_callback_is_called_when_add_library_button_is_clicked() {

        // Given
        val onAddLibraryClick: () -> Unit = mock()
        composeAddLibraryContent(
            libraryName = "Coil",
            libraryUrlPath = "coil-kt/coil",
            onAddLibraryClick = onAddLibraryClick
        )

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
        composeAddLibraryContent(
            state = AddLibraryState.Error("Something went wrong"),
            snackbarHostState = snackbarHostState,
            onErrorDismissed = onErrorDismissed
        )

        // When
        snackbarHostState.currentSnackbarData?.dismiss()

        // Then
        Mockito.verify(onErrorDismissed).invoke()

    }

    private fun composeAddLibraryContent(
        state: AddLibraryState = AddLibraryState.Initial,
        snackbarHostState: SnackbarHostState = SnackbarHostState(),
        libraryName: String = "",
        onLibraryNameChange: (String) -> Unit = {},
        libraryUrlPath: String = "",
        onLibraryUrlPathChange: (String) -> Unit = {},
        onAddLibraryClick: () -> Unit = {},
        onErrorDismissed: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AddLibraryContent(
                snackbarHostState = snackbarHostState,
                state = state,
                libraryName = libraryName,
                onLibraryNameChange = onLibraryNameChange,
                libraryUrlPath = libraryUrlPath,
                onLibraryUrlPathChange = onLibraryUrlPathChange,
                onAddLibraryClick = onAddLibraryClick,
                onErrorDismissed = onErrorDismissed
            )
        }
    }

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

}