package ir.fallahpoor.releasetracker.libraries.ui

import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import ir.fallahpoor.releasetracker.data.utils.NightMode
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class SingleSelectionDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val dialogTitle = "Awesome Dialog"
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun dialog_is_initialized_correctly() {

        // Given
        composeSingleSelectionDialog(currentlySelectedItem = NightMode.OFF)

        // Then
        with(composeTestRule) {
            onNodeWithText(dialogTitle)
                .assertIsDisplayed()
            NightMode.values().forEach { item: NightMode ->
                onNodeWithText(
                    context.getString(item.label),
                    useUnmergedTree = true
                ).assertIsDisplayed()
                if (item == NightMode.OFF) {
                    onNodeWithTag(context.getString(item.label))
                        .assertIsSelected()
                } else {
                    onNodeWithTag(context.getString(item.label))
                        .assertIsNotSelected()
                }
            }
        }

    }

    @Test
    fun correct_callback_is_called_when_an_item_is_selected() {

        // Given
        val onItemSelect: (NightMode) -> Unit = mock()
        composeSingleSelectionDialog(
            currentlySelectedItem = NightMode.ON,
            onItemSelect = onItemSelect
        )

        // When
        composeTestRule.onNodeWithText(
            context.getString(NightMode.AUTO.label),
            useUnmergedTree = true
        ).performClick()

        // Then
        Mockito.verify(onItemSelect).invoke(NightMode.AUTO)

    }

    @Test
    fun correct_callback_is_called_when_dialog_is_dismissed() {

        // Given
        val onDismiss: () -> Unit = mock()
        composeSingleSelectionDialog(onDismiss = onDismiss)

        // When
        Espresso.pressBack()

        // Then
        Mockito.verify(onDismiss).invoke()

    }

    private fun composeSingleSelectionDialog(
        currentlySelectedItem: NightMode = NightMode.OFF,
        onItemSelect: (NightMode) -> Unit = {},
        onDismiss: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            SingleSelectionDialog(
                title = dialogTitle,
                items = NightMode.values().toList(),
                labels = NightMode.values().toList().map { stringResource(it.label) },
                selectedItem = currentlySelectedItem,
                onItemSelect = onItemSelect,
                onDismiss = onDismiss
            )
        }
    }

    private inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

}