package ir.fallahpoor.releasetracker.addlibrary.view

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.addlibrary.Intent
import ir.fallahpoor.releasetracker.addlibrary.viewmodel.AddLibraryViewModel
import ir.fallahpoor.releasetracker.common.GITHUB_BASE_URL
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddLibraryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var addLibraryViewModel: AddLibraryViewModel

    @Test
    fun screen_is_initialized_correctly() {

        // Given
        initializeAddLibraryScreen()

        // When the screen is initially displayed

        // Then
        with(composeTestRule) {
            onNodeWithTag(AddLibraryTags.TITLE)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.BACK_BUTTON)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.CONTENT)
                .assertIsDisplayed()
        }

    }

    @Test
    fun correct_library_name_is_displayed() {

        // Given
        initializeAddLibraryScreen(libraryName = "Coil")

        // Then
        composeTestRule.onNodeWithTag(
            AddLibraryTags.LIBRARY_NAME_TEXT_FIELD,
            useUnmergedTree = true
        ).assertTextEquals("Coil")

    }

    @Test
    fun correct_library_url_is_displayed() {

        // Given
        initializeAddLibraryScreen(libraryUrl = "coil-kt/coil")

        // Then
        composeTestRule.onNodeWithTag(
            AddLibraryTags.LIBRARY_URL_TEXT_FIELD,
            useUnmergedTree = true
        ).assertTextEquals(GITHUB_BASE_URL + "coil-kt/coil")

    }

    @Test
    fun correct_intent_is_called_when_library_name_is_entered() {

        // Given
        initializeAddLibraryScreen()

        // When
        composeTestRule.onNodeWithTag(AddLibraryTags.LIBRARY_NAME_TEXT_FIELD)
            .performTextInput("Coil")

        // Then
        Mockito.verify(addLibraryViewModel).handleIntent(Intent.UpdateLibraryName("Coil"))

    }

    @Test
    fun correct_intent_is_called_when_library_URL_path_is_entered() {

        // Given
        initializeAddLibraryScreen()

        // When
        composeTestRule.onNodeWithTag(AddLibraryTags.LIBRARY_URL_TEXT_FIELD)
            .performTextInput("coil-kt/coil")

        // Then
        Mockito.verify(addLibraryViewModel)
            .handleIntent(Intent.UpdateLibraryUrlPath("coil-kt/coil"))

    }

    @Test
    fun correct_intent_is_called_when_adding_a_new_library() {

        // Given
        initializeAddLibraryScreen(libraryName = "Coil", libraryUrl = "coil-kt/coil")

        // When
        composeTestRule.onNodeWithTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
            .performClick()

        // Then
        Mockito.verify(addLibraryViewModel).handleIntent(Intent.AddLibrary("Coil", "coil-kt/coil"))

    }

    // TODO Add a test to assert that AddLibraryViewModel.resetState() is called when dismissing an error

    @Test
    fun correct_callback_is_called_when_pressing_the_back_button() {

        // Given
        val context: Context = ApplicationProvider.getApplicationContext()
        val onBackClick: () -> Unit = mock()
        initializeAddLibraryScreen(onBackClick = onBackClick)

        // When
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back))
            .performClick()

        // Then
        Mockito.verify(onBackClick).invoke()

    }

    private fun initializeAddLibraryScreen(
        state: AddLibraryState = AddLibraryState.Initial,
        libraryName: String = "",
        libraryUrl: String = "",
        onBackClick: () -> Unit = {}
    ) {
        Mockito.`when`(addLibraryViewModel.state).thenReturn(
            MutableStateFlow(
                AddLibraryScreenUiState(
                    libraryName = libraryName,
                    libraryUrlPath = libraryUrl,
                    addLibraryState = state
                )
            )
        )
        composeTestRule.setContent {
            AddLibraryScreen(
                addLibraryViewModel = addLibraryViewModel,
                isDarkTheme = false,
                onBackClick = onBackClick
            )
        }
    }

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

}