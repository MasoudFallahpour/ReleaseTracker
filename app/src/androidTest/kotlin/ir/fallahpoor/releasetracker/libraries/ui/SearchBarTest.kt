package ir.fallahpoor.releasetracker.libraries.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class SearchBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun search_bar_is_initialized_correctly() {

        // Given
        val hint = "hint"
        composeTestRule.setContent {
            SearchBar(
                hint = hint,
                query = "",
                onQueryChange = {},
                onQuerySubmit = {},
                onClearClick = {},
                onCloseClick = {}
            )
        }

        // When the composable is freshly composed

        // Then
        with(composeTestRule) {
            onNodeWithText(hint).assertIsDisplayed()
            onNodeWithTag(SearchBarTags.QUERY_TEXT_FIELD)
                .assertTextEquals("")
            onNodeWithTag(SearchBarTags.CLOSE_BUTTON)
                .assertIsDisplayed()
            onNodeWithTag(SearchBarTags.CLEAR_BUTTON)
                .assertIsDisplayed()
        }

    }

    @Test
    fun hint_is_not_displayed_when_search_query_is_not_empty() {

        // Given
        val hint = "Enter library name"
        val query = "Coil"
        composeTestRule.setContent {
            SearchBar(
                hint = hint,
                query = query,
                onQueryChange = {},
                onQuerySubmit = {},
                onClearClick = {},
                onCloseClick = {}
            )
        }

        // When

        // Then
        with(composeTestRule) {
            onNodeWithText(hint)
                .assertDoesNotExist()
            onNodeWithTag(SearchBarTags.QUERY_TEXT_FIELD)
                .assertTextEquals(query)
        }

    }

    @Test
    fun correct_callback_is_called_when_clear_button_is_clicked() {

        // Given
        val onClearClick: () -> Unit = mock()
        composeTestRule.setContent {
            SearchBar(
                hint = "",
                query = "",
                onQueryChange = {},
                onQuerySubmit = {},
                onClearClick = onClearClick,
                onCloseClick = {}
            )
        }

        // When
        composeTestRule.onNodeWithTag(SearchBarTags.CLEAR_BUTTON)
            .performClick()

        // Then
        Mockito.verify(onClearClick).invoke()

    }

    @Test
    fun correct_callback_is_called_when_close_button_is_clicked() {

        // Given
        val onCloseClick: () -> Unit = mock()
        composeTestRule.setContent {
            SearchBar(
                hint = "",
                query = "",
                onQueryChange = {},
                onQuerySubmit = {},
                onClearClick = {},
                onCloseClick = onCloseClick
            )
        }

        // When
        composeTestRule.onNodeWithTag(SearchBarTags.CLOSE_BUTTON)
            .performClick()

        // Then
        Mockito.verify(onCloseClick).invoke()

    }

    @Test
    fun correct_callback_is_called_when_query_is_changed() {

        // Given
        val onQueryChange: (String) -> Unit = mock()
        composeTestRule.setContent {
            SearchBar(
                hint = "",
                query = "",
                onQueryChange = onQueryChange,
                onQuerySubmit = {},
                onClearClick = {},
                onCloseClick = {}
            )
        }

        // When
        composeTestRule.onNodeWithTag(SearchBarTags.QUERY_TEXT_FIELD)
            .performTextInput("Coroutines")

        // Then
        Mockito.verify(onQueryChange).invoke("Coroutines")

    }

    @Test
    fun correct_callback_called_when_query_is_submitted() {

        // Given
        var query by mutableStateOf("")
        val newQuery = "coil"

        composeTestRule.setContent {
            SearchBar(
                hint = "",
                query = query,
                onQueryChange = { },
                onQuerySubmit = { query = it },
                onClearClick = { },
                onCloseClick = { }
            )
        }

        // TODO Find a way to perform the 'search' IME action
        // When
//        composeTestRule.onNodeWithTag(queryTextFieldTag)
//            .performImeAction()

        // Then
// Truth.assertThat(query).isEqualTo(newQuery)

    }

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

}