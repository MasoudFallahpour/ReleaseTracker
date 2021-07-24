package ir.fallahpoor.releasetracker.libraries.view.composables.dialogs

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.R
import org.junit.Rule
import org.junit.Test

class DeleteLibraryDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun deleteLibraryDialog_is_initialized_correctly() {

        // Given
        val libraryName = "Coil"
        composeTestRule.setContent {
            DeleteLibraryDialog(
                libraryName = libraryName,
                onDeleteClicked = {},
                onDismiss = {}
            )
        }

        // Then
        with(composeTestRule) {
            onNodeWithText(
                context.getString(R.string.delete_selected_library, libraryName),
                useUnmergedTree = true
            ).assertIsDisplayed()
            onNodeWithText(context.getString(R.string.delete))
        }

    }

    @Test
    fun when_delete_button_is_clicked_correct_callback_is_called() {

        // Given
        var delete = false
        composeTestRule.setContent {
            DeleteLibraryDialog(
                libraryName = "",
                onDeleteClicked = { delete = true },
                onDismiss = {}
            )
        }

        // When
        composeTestRule.onNodeWithText(context.getString(R.string.delete), useUnmergedTree = true)
            .performClick()

        // Then
        Truth.assertThat(delete).isTrue()

    }

    @Test
    fun when_DeleteLibraryDialog_is_dismissed_correct_callback_is_called() {

        // Given
        var dismissed = false
        composeTestRule.setContent {
            DeleteLibraryDialog(
                libraryName = "",
                onDeleteClicked = {},
                onDismiss = { dismissed = true }
            )
        }

        // When
        Espresso.pressBack()

        // Then
        Truth.assertThat(dismissed).isTrue()

    }

}