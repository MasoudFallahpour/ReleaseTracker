package ir.fallahpoor.releasetracker.features.libraries.ui

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
        composeSearchBar(hint = "hint")

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
        composeSearchBar(hint = hint, query = query)

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
        composeSearchBar(onClearClick = onClearClick)

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
        composeSearchBar(onCloseClick = onCloseClick)

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
        composeSearchBar(onQueryChange = onQueryChange)

        // When
        composeTestRule.onNodeWithTag(SearchBarTags.QUERY_TEXT_FIELD)
            .performTextInput("Coroutines")

        // Then
        Mockito.verify(onQueryChange).invoke("Coroutines")

    }

    // TODO Test if the correct callback is called when query is submitted

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

    private fun composeSearchBar(
        hint: String = "",
        query: String = "",
        onQueryChange: (String) -> Unit = {},
        onQuerySubmit: (String) -> Unit = {},
        onClearClick: () -> Unit = {},
        onCloseClick: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            SearchBar(
                hint = hint,
                query = query,
                onQueryChange = onQueryChange,
                onQuerySubmit = onQuerySubmit,
                onClearClick = onClearClick,
                onCloseClick = onCloseClick
            )
        }
    }

}