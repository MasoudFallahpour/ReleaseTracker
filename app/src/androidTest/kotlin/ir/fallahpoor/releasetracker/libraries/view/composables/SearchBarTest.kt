package ir.fallahpoor.releasetracker.libraries.view.composables

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.R
import org.junit.Rule
import org.junit.Test

class SearchBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val clearSearchBarTag = context.getString(R.string.test_tag_search_bar_clear_button)
    private val closeSearchBarTag = context.getString(R.string.test_tag_search_bar_close_button)
    private val queryTextFieldTag = context.getString(R.string.test_tag_search_bar_query_text_field)

    @Test
    fun searchBar_is_initialized_correctly() {

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

        with(composeTestRule) {
            onNodeWithText(hint).assertIsDisplayed()
            onNodeWithTag(closeSearchBarTag)
                .assertIsDisplayed()
            onNodeWithTag(clearSearchBarTag)
                .assertIsDisplayed()
        }

    }

    @Test
    fun hint_not_displayed_when_query_is_not_empty() {

        // Given
        val hint = "hint"
        var query by mutableStateOf("")
        val newQuery = "coil"

        composeTestRule.setContent {
            SearchBar(
                hint = hint,
                query = query,
                onQueryChange = { query = it },
                onQuerySubmit = {},
                onClearClick = {},
                onCloseClick = {}
            )
        }

        // When
        composeTestRule.onNodeWithTag(queryTextFieldTag)
            .performTextInput(newQuery)

        // Then
        with(composeTestRule) {
            onNodeWithText(hint).assertDoesNotExist()
            onNodeWithText(newQuery).assertIsDisplayed()
        }

    }

    @Test
    fun correct_callback_called_when_clear_button_clicked() {

        var query by mutableStateOf("")

        composeTestRule.setContent {
            SearchBar(
                hint = "",
                query = query,
                onQueryChange = { },
                onQuerySubmit = { },
                onClearClick = { query = "" },
                onCloseClick = { }
            )
        }

        // When
        composeTestRule.onNodeWithTag(clearSearchBarTag)
            .performClick()

        // Then
        Truth.assertThat(query).isEmpty()

    }

    @Test
    fun clicking_the_close_button_invokes_the_correct_callback() {

        var query by mutableStateOf("")

        composeTestRule.setContent {
            SearchBar(
                hint = "",
                query = query,
                onQueryChange = { },
                onQuerySubmit = { },
                onClearClick = { },
                onCloseClick = { query = "" }
            )
        }

        // When
        composeTestRule.onNodeWithTag(closeSearchBarTag)
            .performClick()

        // Then
        Truth.assertThat(query).isEmpty()

    }

    @Test
    fun correct_callback_called_when_query_changes() {

        // Given
        var query by mutableStateOf("")
        val newQuery = "coil"

        composeTestRule.setContent {
            SearchBar(
                hint = "",
                query = query,
                onQueryChange = { query = it },
                onQuerySubmit = { },
                onClearClick = { },
                onCloseClick = { }
            )
        }

        // When
        composeTestRule.onNodeWithTag(queryTextFieldTag)
            .performTextInput(newQuery)

        // Then
        Truth.assertThat(query).isEqualTo(newQuery)

    }

    @Test
    fun correct_callback_called_when_query_submitted() {

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
//        Truth.assertThat(query).isEqualTo(newQuery)

    }

}