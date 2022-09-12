package ir.fallahpoor.releasetracker.libraries.ui

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.SortOrder
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
    fun toolbar_is_initialized_correctly_when_night_mode_is_supported() {

        // Given
        composeToolbar()

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
    fun toolbar_is_initialized_correctly_when_night_mode_is_not_supported() {

        // Given
        composeToolbar(isNightModeSupported = false)

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
    fun sort_dialog_is_displayed_when_sort_button_is_clicked() {

        // Given
        composeToolbar()

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
    fun night_mode_dialog_is_displayed_when_night_mode_button_is_clicked() {

        // Given
        composeToolbar()

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
    fun search_bar_is_displayed_when_search_button_is_clicked() {

        // Given
        composeToolbar()

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
    fun correct_callback_is_called_when_sort_order_is_selected() {

        // Given
        val onSortOrderChange: (SortOrder) -> Unit = mock()
        composeToolbar(onSortOrderChange = onSortOrderChange)

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
        Mockito.verify(onSortOrderChange).invoke(SortOrder.A_TO_Z)

    }

    @Test
    fun sort_dialog_is_closed_when_sort_order_is_selected() {

        // Given
        composeToolbar()

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

    }

    @Test
    fun correct_callback_is_called_when_night_mode_is_selected() {

        // Given
        val onNightModeChange: (NightMode) -> Unit = mock()
        composeToolbar(onNightModeChange = onNightModeChange)

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
        Mockito.verify(onNightModeChange).invoke(NightMode.AUTO)

    }

    @Test
    fun night_mode_dialog_is_closed_when_night_mode_is_selected() {

        // Given
        composeToolbar()

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

    }

    @Test
    fun correct_callback_is_called_when_search_query_is_changed() {

        // Given
        val onSearchQueryChange: (String) -> Unit = mock()
        composeToolbar(onSearchQueryChange = onSearchQueryChange)

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
    fun correct_callback_is_called_when_search_query_is_cleared() {

        // Given
        val onSearchQueryChange: (String) -> Unit = mock()
        composeToolbar(onSearchQueryChange = onSearchQueryChange)

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
    fun toolbar_is_set_to_normal_mode_when_search_bar_is_closed() {

        // Given
        composeToolbar()

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

    private fun composeToolbar(
        currentSortOrder: SortOrder = SortOrder.A_TO_Z,
        onSortOrderChange: (SortOrder) -> Unit = {},
        isNightModeSupported: Boolean = true,
        currentNightMode: NightMode = NightMode.ON,
        onNightModeChange: (NightMode) -> Unit = {},
        onSearchQueryChange: (String) -> Unit = {},
        onSearchQuerySubmit: (String) -> Unit = {}
    ) {
        composeTestRule.setContent {
            Toolbar(
                currentSortOrder = currentSortOrder,
                onSortOrderChange = onSortOrderChange,
                isNightModeSupported = isNightModeSupported,
                currentNightMode = currentNightMode,
                onNightModeChange = onNightModeChange,
                onSearchQueryChange = onSearchQueryChange,
                onSearchQuerySubmit = onSearchQuerySubmit
            )
        }
    }

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

}