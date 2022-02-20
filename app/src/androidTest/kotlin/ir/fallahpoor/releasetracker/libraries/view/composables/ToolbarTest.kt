package ir.fallahpoor.releasetracker.libraries.view.composables

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class ToolbarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val appNameText = context.getString(R.string.app_name)
    private val sortText = context.getString(R.string.sort)
    private val searchText = context.getString(R.string.search)
    private val moreOptionsText = context.getString(R.string.more_options)
    private val nightModeText = context.getString(R.string.night_mode)
    private val selectSortOrderText = context.getString(R.string.select_sort_order)
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
        composeTestRule.onNodeWithTag(SearchBarTags.SEARCH_BAR)
            .assertDoesNotExist()

    }

    @Test
    fun toolbar_initialized_correctly_when_night_is_not_supported() {

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
        composeTestRule.onNodeWithTag(SearchBarTags.SEARCH_BAR)
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
        composeTestRule.setContent {
            Toolbar(
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
        composeTestRule.onNodeWithTag(SearchBarTags.SEARCH_BAR)
            .assertIsDisplayed()

    }

    @Test
    fun when_sort_order_is_selected_sort_order_dialog_is_closed_and_correct_callback_is_called() {

        // Given
        val onSortOrderChange: (SortOrder) -> Unit = mock()
        composeTestRule.setContent {
            Toolbar(
                currentSortOrder = SortOrder.Z_TO_A,
                onSortOrderChange = onSortOrderChange,
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
                context.getString(SortOrder.A_TO_Z.label),
                useUnmergedTree = true
            ).performClick()
        }

        // Then
        composeTestRule.onNodeWithText(selectSortOrderText)
            .assertDoesNotExist()
        Mockito.verify(onSortOrderChange).invoke(SortOrder.A_TO_Z)

    }

    @Test
    fun when_night_mode_is_selected_night_mode_dialog_is_closed_and_correct_callback_is_called() {

        // Given
        val onNightModeChange: (NightMode) -> Unit = mock()
        composeTestRule.setContent {
            Toolbar(
                currentSortOrder = SortOrder.A_TO_Z,
                onSortOrderChange = {},
                isNightModeSupported = true,
                currentNightMode = NightMode.OFF,
                onNightModeChange = onNightModeChange,
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
            onNodeWithText(context.getString(NightMode.AUTO.label))
                .performClick()
        }

        // Then
        composeTestRule.onNodeWithText(selectNightMode)
            .assertDoesNotExist()
        Mockito.verify(onNightModeChange).invoke(NightMode.AUTO)

    }

    @Test
    fun when_search_query_is_changed_correct_callback_is_called() {

        // Given
        val onSearchQueryChange: (String) -> Unit = mock()
        composeTestRule.setContent {
            Toolbar(
                currentSortOrder = SortOrder.A_TO_Z,
                onSortOrderChange = {},
                isNightModeSupported = true,
                currentNightMode = NightMode.ON,
                onNightModeChange = {},
                onSearchQueryChange = onSearchQueryChange,
                onSearchQuerySubmit = {}
            )
        }

        // When
        composeTestRule.onNodeWithContentDescription(
            searchText,
            useUnmergedTree = true
        ).performClick()
        composeTestRule.onNodeWithTag(SearchBarTags.QUERY_TEXT_FIELD)
            .performTextInput("Coil")

        // Then
        Mockito.verify(onSearchQueryChange).invoke("Coil")

    }

    @Test
    fun when_search_query_is_cleared_correct_callback_is_called() {

        // Given
        val onSearchQueryChange: (String) -> Unit = mock()
        composeTestRule.setContent {
            Toolbar(
                currentSortOrder = SortOrder.A_TO_Z,
                onSortOrderChange = {},
                isNightModeSupported = true,
                currentNightMode = NightMode.ON,
                onNightModeChange = {},
                onSearchQueryChange = onSearchQueryChange,
                onSearchQuerySubmit = {}
            )
        }

        // When
        composeTestRule.onNodeWithContentDescription(
            searchText,
            useUnmergedTree = true
        ).performClick()
        composeTestRule.onNodeWithTag(SearchBarTags.CLEAR_BUTTON)
            .performClick()

        // Then
        Mockito.verify(onSearchQueryChange).invoke("")

    }

    @Test
    fun when_search_bar_is_closed_toolbar_is_set_to_normal_mode() {

        // Given
        composeTestRule.setContent {
            Toolbar(
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
        composeTestRule.onNodeWithTag(SearchBarTags.CLOSE_BUTTON)
            .performClick()

        // Then
        composeTestRule.onNodeWithTag(SearchBarTags.SEARCH_BAR)
            .assertDoesNotExist()

    }

    private fun initializeToolbar(nightModeSupported: Boolean = true) {
        composeTestRule.setContent {
            Toolbar(
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

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

}