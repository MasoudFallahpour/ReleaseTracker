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
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import org.junit.Rule
import org.junit.Test

class ToolbarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val searchBarTextFieldTag =
        context.getString(R.string.test_tag_search_bar_query_text_field)
    private val searchBarTag =
        context.getString(R.string.test_tag_search_bar)
    private val appNameText = context.getString(R.string.app_name)
    private val sortText = context.getString(R.string.sort)
    private val searchText = context.getString(R.string.search)
    private val moreOptionsText = context.getString(R.string.more_options)
    private val nightModeText = context.getString(R.string.night_mode)
    private val selectSortOrderText = context.getString(R.string.select_sorting_order)
    private val selectNightMode = context.getString(R.string.select_night_mode)

    @Test
    fun toolbar_initialized_correctly_when_night_mode_is_supported() {

        // Given
        initializeToolbar()

        // Then
        composeTestRule.onNodeWithText(appNameText)
            .assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            sortText,
            useUnmergedTree = true
        ).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            searchText,
            useUnmergedTree = true
        ).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            moreOptionsText,
            useUnmergedTree = true
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(searchBarTag)
            .assertDoesNotExist()

    }

    @Test
    fun toolbar_initialized_correctly_when_night_is_not_mode_supported() {

        // Given
        initializeToolbar(nightModeSupported = false)

        // Then
        composeTestRule.onNodeWithText(appNameText)
            .assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            sortText,
            useUnmergedTree = true
        ).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            searchText,
            useUnmergedTree = true
        ).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            moreOptionsText,
            useUnmergedTree = true
        ).assertDoesNotExist()
        composeTestRule.onNodeWithTag(searchBarTag)
            .assertDoesNotExist()

    }

    @Test
    fun when_sort_button_is_clicked_sort_dialog_is_displayed() {

        // Given
        initializeToolbar()

        // When
        composeTestRule.onNodeWithContentDescription(
            sortText,
            useUnmergedTree = true
        ).performClick()

        // Then
        composeTestRule.onNodeWithText(selectSortOrderText)
            .assertIsDisplayed()

    }

    @Test
    fun when_night_mode_button_is_clicked_night_mode_dialog_is_displayed() {

        // Given
        initializeToolbar()

        // When
        with(composeTestRule) {
            onNodeWithContentDescription(
                moreOptionsText,
                useUnmergedTree = true
            ).performClick()
            onNodeWithText(
                nightModeText,
                useUnmergedTree = true
            ).performClick()
        }

        // Then
        composeTestRule.onNodeWithText(selectNightMode)
            .assertIsDisplayed()

    }

    @Test
    fun when_search_button_is_clicked_search_bar_is_displayed() {

        // Given
        var toolbarMode by mutableStateOf(ToolbarMode.Normal)
        composeTestRule.setContent {
            Toolbar(
                toolbarMode = toolbarMode,
                onToolbarModeChange = { toolbarMode = it },
                currentSortOrder = SortOrder.A_TO_Z,
                onSortOrderChange = {},
                isNightModeSupported = true,
                currentNightMode = NightMode.ON,
                onNightModeChange = {},
                onSearchQueryChange = {},
                onSearchQuerySubmit = {}
            )
        }

        // When
        composeTestRule.onNodeWithContentDescription(
            searchText,
            useUnmergedTree = true
        ).performClick()

        // Then
        composeTestRule.onNodeWithTag(searchBarTag)
            .assertIsDisplayed()

    }

    @Test
    fun when_sort_order_is_selected_sort_order_dialog_is_closed_and_correct_callback_is_called() {

        // Given
        var sortOrder = SortOrder.Z_TO_A
        composeTestRule.setContent {
            Toolbar(
                toolbarMode = ToolbarMode.Normal,
                onToolbarModeChange = {},
                currentSortOrder = sortOrder,
                onSortOrderChange = { sortOrder = it },
                isNightModeSupported = true,
                currentNightMode = NightMode.ON,
                onNightModeChange = {},
                onSearchQueryChange = {},
                onSearchQuerySubmit = {}
            )
        }

        // When
        with(composeTestRule) {
            onNodeWithContentDescription(
                sortText,
                useUnmergedTree = true
            ).performClick()
            onNodeWithText(
                context.getString(R.string.a_to_z),
                useUnmergedTree = true
            ).performClick()
        }

        // Then
        composeTestRule.onNodeWithText(selectSortOrderText)
            .assertDoesNotExist()
        Truth.assertThat(sortOrder).isEqualTo(SortOrder.A_TO_Z)

    }

    @Test
    fun when_night_mode_is_selected_night_mode_dialog_is_closed_and_correct_callback_is_called() {

        // Given
        var nightMode = NightMode.OFF
        composeTestRule.setContent {
            Toolbar(
                toolbarMode = ToolbarMode.Normal,
                onToolbarModeChange = {},
                currentSortOrder = SortOrder.A_TO_Z,
                onSortOrderChange = {},
                isNightModeSupported = true,
                currentNightMode = nightMode,
                onNightModeChange = { nightMode = it },
                onSearchQueryChange = {},
                onSearchQuerySubmit = {}
            )
        }

        // When
        with(composeTestRule) {
            onNodeWithContentDescription(
                moreOptionsText,
                useUnmergedTree = true
            ).performClick()
            onNodeWithText(nightModeText)
                .performClick()
            onNodeWithText(context.getString(R.string.auto))
                .performClick()
        }

        // Then
        composeTestRule.onNodeWithText(selectNightMode)
            .assertDoesNotExist()
        Truth.assertThat(nightMode).isEqualTo(NightMode.AUTO)

    }

    @Test
    fun when_search_query_is_changed_correct_callback_is_called() {

        // Given
        var searchQuery = ""
        composeTestRule.setContent {
            Toolbar(
                toolbarMode = ToolbarMode.Search,
                onToolbarModeChange = {},
                currentSortOrder = SortOrder.A_TO_Z,
                onSortOrderChange = {},
                isNightModeSupported = true,
                currentNightMode = NightMode.ON,
                onNightModeChange = {},
                onSearchQueryChange = { searchQuery = it },
                onSearchQuerySubmit = {}
            )
        }

        // When
        val expectedSearchQuery = "coil"
        composeTestRule.onNodeWithTag(searchBarTextFieldTag)
            .performTextInput(expectedSearchQuery)

        // Then
        Truth.assertThat(searchQuery).isEqualTo(expectedSearchQuery)

    }

    @Test
    fun when_search_bar_is_closed_toolbar_is_set_to_normal_mode() {

        // Given
        var toolbarMode by mutableStateOf(ToolbarMode.Search)
        composeTestRule.setContent {
            Toolbar(
                toolbarMode = toolbarMode,
                onToolbarModeChange = { toolbarMode = it },
                currentSortOrder = SortOrder.A_TO_Z,
                onSortOrderChange = {},
                isNightModeSupported = true,
                currentNightMode = NightMode.ON,
                onNightModeChange = {},
                onSearchQueryChange = {},
                onSearchQuerySubmit = {}
            )
        }

        // When
        composeTestRule.onNodeWithTag(context.getString(R.string.test_tag_search_bar_close_button))
            .performClick()

        // Then
        composeTestRule.onNodeWithTag(searchBarTag)
            .assertDoesNotExist()
        Truth.assertThat(toolbarMode).isEqualTo(ToolbarMode.Normal)

    }

    private fun initializeToolbar(nightModeSupported: Boolean = true) {
        composeTestRule.setContent {
            Toolbar(
                toolbarMode = ToolbarMode.Normal,
                onToolbarModeChange = {},
                currentSortOrder = SortOrder.A_TO_Z,
                onSortOrderChange = {},
                isNightModeSupported = nightModeSupported,
                currentNightMode = NightMode.ON,
                onNightModeChange = {},
                onSearchQueryChange = {},
                onSearchQuerySubmit = {}
            )
        }
    }

}