package ir.fallahpoor.releasetracker.libraries.view.composables.dialogs

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import ir.fallahpoor.releasetracker.R
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock

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
                .assertIsDisplayed()
            onNodeWithText(context.getString(R.string.cancel))
                .assertIsDisplayed()
        }

    }

    @Test
    fun when_delete_button_is_clicked_correct_callback_is_called() {

        // Given
        val onDeleteClicked: () -> Unit = mock()
        composeTestRule.setContent {
            DeleteLibraryDialog(
                libraryName = "",
                onDeleteClicked = onDeleteClicked,
                onDismiss = {}
            )
        }

        // When
        composeTestRule.onNodeWithText(context.getString(R.string.delete), useUnmergedTree = true)
            .performClick()

        // Then
        Mockito.verify(onDeleteClicked).invoke()

    }

    @Test
    fun when_cancel_button_is_clicked_correct_callback_is_called() {

        // Given
        val onDismiss: () -> Unit = mock()
        composeTestRule.setContent {
            DeleteLibraryDialog(
                libraryName = "",
                onDeleteClicked = {},
                onDismiss = onDismiss
            )
        }

        // When
        composeTestRule.onNodeWithText(context.getString(R.string.cancel), useUnmergedTree = true)
            .performClick()

        // Then
        Mockito.verify(onDismiss).invoke()

    }

    @Test
    fun when_DeleteLibraryDialog_is_dismissed_correct_callback_is_called() {

        // Given
        val onDismiss: () -> Unit = mock()
        composeTestRule.setContent {
            DeleteLibraryDialog(
                libraryName = "",
                onDeleteClicked = {},
                onDismiss = onDismiss
            )
        }

        // When
        Espresso.pressBack()

        // Then
        Mockito.verify(onDismiss).invoke()

    }

}