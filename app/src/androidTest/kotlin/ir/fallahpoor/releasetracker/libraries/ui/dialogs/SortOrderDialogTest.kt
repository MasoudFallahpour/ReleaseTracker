package ir.fallahpoor.releasetracker.libraries.ui.dialogs

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class SortOrderDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun dialog_is_initialized_correctly() {

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
            onNodeWithText(context.getString(R.string.select_sort_order))
                .assertIsDisplayed()
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
    fun correct_callback_is_called_when_sort_order_is_selected() {

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
    fun correct_callback_is_called_when_dialog_is_dismissed() {

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

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

}