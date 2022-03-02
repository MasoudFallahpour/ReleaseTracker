package ir.fallahpoor.releasetracker.libraries.ui.dialogs

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

class NightModeDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun dialog_is_initialized_correctly() {

        // Given
        composeNightModeDialog()

        // Then
        with(composeTestRule) {
            onNodeWithText(context.getString(R.string.select_night_mode))
                .assertIsDisplayed()
            NightMode.values().forEach { nightMode: NightMode ->
                onNodeWithText(
                    context.getString(nightMode.label),
                    useUnmergedTree = true
                ).assertIsDisplayed()
            }
            onNodeWithTag(context.getString(NightMode.ON.label))
                .assertIsSelected()
        }

    }

    @Test
    fun correct_callback_is_called_when_night_mode_is_selected() {

        // Given
        val onNightModeClick: (NightMode) -> Unit = mock()
        composeNightModeDialog(
            currentNightMode = NightMode.OFF,
            onNightModeClick = onNightModeClick
        )

        // When
        composeTestRule.onNodeWithText(
            context.getString(NightMode.AUTO.label),
            useUnmergedTree = true
        ).performClick()

        // Then
        Mockito.verify(onNightModeClick).invoke(NightMode.AUTO)

    }

    @Test
    fun correct_callback_is_called_when_dialog_is_dismissed() {

        // Given
        val onDismiss: () -> Unit = mock()
        composeNightModeDialog(onDismiss = onDismiss)

        // When
        Espresso.pressBack()

        // Then
        Mockito.verify(onDismiss).invoke()

    }

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

    private fun composeNightModeDialog(
        currentNightMode: NightMode = NightMode.ON,
        onNightModeClick: (NightMode) -> Unit = {},
        onDismiss: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            NightModeDialog(
                currentNightMode = currentNightMode,
                onNightModeClick = onNightModeClick,
                onDismiss = onDismiss
            )
        }
    }

}