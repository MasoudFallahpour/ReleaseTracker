package ir.fallahpoor.releasetracker.addlibrary.view

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.addlibrary.viewmodel.AddLibraryViewModel
import ir.fallahpoor.releasetracker.common.GITHUB_BASE_URL
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class AddLibraryScreenTest {

    private companion object {
        const val LIBRARY_NAME = "Kotlin Coroutines"
        const val LIBRARY_URL_PATH = "kotlinx/coroutines"
        const val LIBRARY_URL_INVALID = "invalidLibraryUrl"
        const val LIBRARY_VERSION = "1.0"
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var libraryRepository: LibraryRepository

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val addLibraryButtonTag =
        context.getString(R.string.test_tag_add_library_add_library_button)
    private val libraryNameTextFieldTag =
        context.getString(R.string.test_tag_add_library_library_name_text_field)
    private val libraryUrlTextFieldTag =
        context.getString(R.string.test_tag_add_library_library_url_text_field)
    private val progressIndicatorTag =
        context.getString(R.string.test_tag_add_library_progress_indicator)
    private val libraryNameEmptyText = context.getString(R.string.library_name_empty)
    private val libraryUrlEmptyText = context.getString(R.string.library_url_empty)
    private val libraryUrlInvalidText = context.getString(R.string.library_url_invalid)

    @Test
    fun test_fresh_state() {

        // Given
        initializeAddLibraryScreen()

        // When the screen is freshly displayed

        // Then
        with(composeTestRule) {
            onNodeWithTag(progressIndicatorTag)
                .assertDoesNotExist()
            onNodeWithTag(libraryNameTextFieldTag, useUnmergedTree = true)
                .assertTextEquals("")
            onNodeWithTag(libraryUrlTextFieldTag, useUnmergedTree = true)
                .assertTextEquals("")
            onNodeWithTag(addLibraryButtonTag)
                .assertIsEnabled()
            onNodeWithText(libraryNameEmptyText)
                .assertDoesNotExist()
            onNodeWithText(libraryUrlEmptyText)
                .assertDoesNotExist()
            onNodeWithText(libraryUrlInvalidText)
                .assertDoesNotExist()
        }
        verifyZeroInteractions(libraryRepository)

    }

    @Test
    fun test_EmptyLibraryName_state() {

        // Given
        initializeAddLibraryScreen()

        // When
        composeTestRule.onNodeWithTag(addLibraryButtonTag)
            .performClick()

        // Then
        with(composeTestRule) {
            onNodeWithTag(progressIndicatorTag)
                .assertDoesNotExist()
            onNodeWithTag(addLibraryButtonTag)
                .assertIsEnabled()
            onNodeWithText(libraryNameEmptyText)
                .assertIsDisplayed()
            onNodeWithText(libraryUrlEmptyText)
                .assertDoesNotExist()
            onNodeWithText(libraryUrlInvalidText)
                .assertDoesNotExist()
        }
        verifyZeroInteractions(libraryRepository)

    }

    @Test
    fun test_EmptyLibraryUrl_state() {

        // Given
        initializeAddLibraryScreen()

        // When
        with(composeTestRule) {
            onNodeWithTag(libraryNameTextFieldTag)
                .performTextInput(LIBRARY_NAME)
            onNodeWithTag(addLibraryButtonTag)
                .performClick()
        }

        // Then
        with(composeTestRule) {
            onNodeWithTag(progressIndicatorTag)
                .assertDoesNotExist()
            onNodeWithTag(addLibraryButtonTag)
                .assertIsEnabled()
            onNodeWithText(libraryNameEmptyText)
                .assertDoesNotExist()
            onNodeWithText(libraryUrlEmptyText)
                .assertIsDisplayed()
            onNodeWithText(libraryUrlInvalidText)
                .assertDoesNotExist()
        }
        verifyZeroInteractions(libraryRepository)

    }

    @Test
    fun test_InvalidLibraryUrl_state() {

        // Given
        initializeAddLibraryScreen()

        // When
        with(composeTestRule) {
            onNodeWithTag(libraryNameTextFieldTag)
                .performTextInput(LIBRARY_NAME)
            onNodeWithTag(libraryUrlTextFieldTag)
                .performTextInput(LIBRARY_URL_INVALID)
            onNodeWithTag(addLibraryButtonTag)
                .performClick()
        }

        // Then
        with(composeTestRule) {
            onNodeWithTag(progressIndicatorTag)
                .assertDoesNotExist()
            onNodeWithTag(addLibraryButtonTag)
                .assertIsEnabled()
            onNodeWithText(libraryNameEmptyText)
                .assertDoesNotExist()
            onNodeWithText(libraryUrlEmptyText)
                .assertDoesNotExist()
            onNodeWithText(libraryUrlInvalidText)
                .assertIsDisplayed()
        }
        verifyZeroInteractions(libraryRepository)

    }

    @Test
    fun test_error_library_already_exists() = runTest {

        // Given
        val snackbarHostState = SnackbarHostState()
        whenever(libraryRepository.getLibrary(any())).thenReturn(Library("", "", "", 0))
        initializeAddLibraryScreen(snackbarHostState)

        // When
        with(composeTestRule) {
            onNodeWithTag(libraryNameTextFieldTag)
                .performTextInput(LIBRARY_NAME)
            onNodeWithTag(libraryUrlTextFieldTag)
                .performTextInput(LIBRARY_URL_PATH)
            onNodeWithTag(addLibraryButtonTag)
                .performClick()
        }

        // Then
        with(composeTestRule) {
            onNodeWithTag(progressIndicatorTag)
                .assertDoesNotExist()
            onNodeWithTag(addLibraryButtonTag)
                .assertIsEnabled()
            onNodeWithText(libraryNameEmptyText)
                .assertDoesNotExist()
            onNodeWithText(libraryUrlEmptyText)
                .assertDoesNotExist()
            onNodeWithText(libraryUrlInvalidText)
                .assertDoesNotExist()
        }

        val snackbarText = snapshotFlow { snackbarHostState.currentSnackbarData }
            .filterNotNull().first().message
        Truth.assertThat(snackbarText).isEqualTo("Library already exists")
        verify(libraryRepository).getLibrary(any())
        verifyNoMoreInteractions(libraryRepository)

    }

    @Test
    fun test_LibraryAdded_state() = runTest {

        // Given
        val snackbarHostState = SnackbarHostState()
        whenever(libraryRepository.getLibrary(LIBRARY_NAME)).thenReturn(null)
        whenever(libraryRepository.getLibraryVersion(LIBRARY_NAME, LIBRARY_URL_PATH)).thenReturn(
            LIBRARY_VERSION
        )
        initializeAddLibraryScreen(snackbarHostState)

        // When
        with(composeTestRule) {
            onNodeWithTag(libraryNameTextFieldTag)
                .performTextInput(LIBRARY_NAME)
            onNodeWithTag(libraryUrlTextFieldTag)
                .performTextInput(LIBRARY_URL_PATH)
            onNodeWithTag(addLibraryButtonTag)
                .performClick()
        }

        // Then
        with(composeTestRule) {
            onNodeWithTag(progressIndicatorTag)
                .assertDoesNotExist()
            onNodeWithTag(libraryNameTextFieldTag, useUnmergedTree = true)
                .assertTextEquals("")
            onNodeWithTag(libraryUrlTextFieldTag, useUnmergedTree = true)
                .assertTextEquals("")
            onNodeWithTag(addLibraryButtonTag)
                .assertIsEnabled()
            onNodeWithText(libraryNameEmptyText)
                .assertDoesNotExist()
            onNodeWithText(libraryUrlEmptyText)
                .assertDoesNotExist()
            onNodeWithText(libraryUrlInvalidText)
                .assertDoesNotExist()
        }

        val actualSnackbarText = snapshotFlow { snackbarHostState.currentSnackbarData }
            .filterNotNull().first().message
        val expectedSnackbarText = context.getString(R.string.library_added)
        Truth.assertThat(actualSnackbarText).isEqualTo(expectedSnackbarText)
        verify(libraryRepository).getLibrary(LIBRARY_NAME)
        verify(libraryRepository).getLibraryVersion(LIBRARY_NAME, LIBRARY_URL_PATH)
        verify(libraryRepository).addLibrary(
            LIBRARY_NAME,
            GITHUB_BASE_URL + LIBRARY_URL_PATH,
            LIBRARY_VERSION
        )

    }

    private fun initializeAddLibraryScreen(snackbarHostState: SnackbarHostState = SnackbarHostState()) {
        val addLibraryViewModel = AddLibraryViewModel(libraryRepository, ExceptionParser())
        composeTestRule.setContent {
            AddLibraryScreen(
                addLibraryViewModel = addLibraryViewModel,
                isDarkTheme = false,
                onBackClick = {},
                scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
            )
        }
    }

}