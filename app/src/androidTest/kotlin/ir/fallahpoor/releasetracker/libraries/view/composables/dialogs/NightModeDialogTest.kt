package ir.fallahpoor.releasetracker.libraries.view.composables.dialogs

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.utils.NightMode
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock

class NightModeDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun nightModeDialog_is_initialized_correctly() {

        // Given
        composeTestRule.setContent {
            NightModeDialog(
                currentNightMode = NightMode.ON,
                onNightModeClick = {},
                onDismiss = {}
            )
        }

        // Then
        with(composeTestRule) {
            onNodeWithText(context.getString(R.string.off), useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(context.getString(R.string.on), useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithText(context.getString(R.string.auto), useUnmergedTree = true)
                .assertIsDisplayed()
            onNodeWithTag(context.getString(R.string.on))
                .assertIsSelected()
        }

    }

    @Test
    fun when_night_mode_is_selected_correct_callback_is_called() {

        // Given
        val onNightModeClick: (NightMode) -> Unit = mock()
        composeTestRule.setContent {
            NightModeDialog(
                currentNightMode = NightMode.OFF,
                onNightModeClick = onNightModeClick,
                onDismiss = {}
            )
        }

        // When
        composeTestRule.onNodeWithText(
            context.getString(R.string.auto),
            useUnmergedTree = true
        ).performClick()

        // Then
        Mockito.verify(onNightModeClick).invoke(NightMode.AUTO)

    }

    @Test
    fun when_SortOrderDialog_is_dismissed_correct_callback_is_called() {

        // Given
        val onDismiss: () -> Unit = mock()
        composeTestRule.setContent {
            NightModeDialog(
                currentNightMode = NightMode.OFF,
                onNightModeClick = {},
                onDismiss = onDismiss
            )
        }

        // When
        Espresso.pressBack()

        // Then
        Mockito.verify(onDismiss).invoke()

    }

}