package ir.fallahpoor.releasetracker.libraries.view.composables.dialogs

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import org.junit.Rule
import org.junit.Test

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
            onNodeWithText(context.getString(R.string.a_to_z), useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(context.getString(R.string.z_to_a), useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(context.getString(R.string.pinned_first), useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(context.getString(R.string.z_to_a))
                .assertIsSelected()
        }

    }

    @Test
    fun when_sort_order_is_selected_correct_callback_is_called() {

        // Given
        var sortOrder = SortOrder.A_TO_Z
        composeTestRule.setContent {
            SortOrderDialog(
                currentSortOrder = SortOrder.Z_TO_A,
                onSortOrderClick = { sortOrder = it },
                onDismiss = {}
            )
        }

        // When
        composeTestRule.onNodeWithText(
            context.getString(R.string.pinned_first),
            useUnmergedTree = true
        ).performClick()

        // Then
        Truth.assertThat(sortOrder).isEqualTo(SortOrder.PINNED_FIRST)

    }

    @Test
    fun when_SortOrderDialog_is_dismissed_correct_callback_is_called() {

        // Given
        var dismissed = false
        composeTestRule.setContent {
            SortOrderDialog(
                currentSortOrder = SortOrder.Z_TO_A,
                onSortOrderClick = {},
                onDismiss = { dismissed = true }
            )
        }

        // When
        Espresso.pressBack()

        // Then
        Truth.assertThat(dismissed).isTrue()

    }

}