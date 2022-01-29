package ir.fallahpoor.releasetracker.libraries.view.composables.dialogs

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock

class SortOrderDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun sortOrderDialog_is_initialized_correctly() {

        // Given
        composeTestRule.setContent {
            SortOrderDialog(
                currentSortOrder = SortOrder.Z_TO_A,
                onSortOrderClick = {},
                onDismiss = {}
            )
        }

        // Then
        with(composeTestRule) {
            SortOrder.values().forEach {
                onNodeWithText(
                    context.getString(it.label),
                    useUnmergedTree = true
                ).assertIsDisplayed()
            }
            onNodeWithTag(context.getString(SortOrder.Z_TO_A.label))
                .assertIsSelected()
        }

    }

    @Test
    fun when_sort_order_is_selected_correct_callback_is_called() {

        // Given
        val onSortOrderClick: (SortOrder) -> Unit = mock()
        composeTestRule.setContent {
            SortOrderDialog(
                currentSortOrder = SortOrder.Z_TO_A,
                onSortOrderClick = onSortOrderClick,
                onDismiss = {}
            )
        }

        // When
        composeTestRule.onNodeWithText(
            context.getString(SortOrder.PINNED_FIRST.label),
            useUnmergedTree = true
        ).performClick()

        // Then
        Mockito.verify(onSortOrderClick).invoke(SortOrder.PINNED_FIRST)

    }

    @Test
    fun when_SortOrderDialog_is_dismissed_correct_callback_is_called() {

        // Given
        val onDismiss: () -> Unit = mock()
        composeTestRule.setContent {
            SortOrderDialog(
                currentSortOrder = SortOrder.Z_TO_A,
                onSortOrderClick = {},
                onDismiss = onDismiss
            )
        }

        // When
        Espresso.pressBack()

        // Then
        Mockito.verify(onDismiss).invoke()

    }

}