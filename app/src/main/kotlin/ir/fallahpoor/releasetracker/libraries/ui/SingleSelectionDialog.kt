package ir.fallahpoor.releasetracker.libraries.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun <T> SingleSelectionDialog(
    title: String,
    items: List<T>,
    labels: List<String>,
    selectedItem: T,
    onItemSelect: (T) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            DialogContent(
                items = items,
                labels = labels,
                selectedItem = selectedItem,
                onItemSelect = onItemSelect
            )
        },
        confirmButton = {}
    )
}

@Composable
private fun <T> DialogContent(
    items: List<T>,
    labels: List<String>,
    selectedItem: T,
    onItemSelect: (T) -> Unit
) {
    Column {
        labels.forEachIndexed { index, label ->
            Item(
                text = label,
                onItemSelect = { onItemSelect(items[index]) },
                isSelected = items[index] == selectedItem
            )
        }
    }
}

@Composable
private fun Item(
    text: String,
    onItemSelect: () -> Unit,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemSelect),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier.testTag(text),
            selected = isSelected,
            onClick = onItemSelect
        )
        Text(text = text)
    }
}

@Preview
@Composable
private fun NightModeDialogPreview() {
    ReleaseTrackerTheme(darkTheme = false) {
        Surface {
            SingleSelectionDialog(
                title = "Awesome title",
                items = NightMode.values().toList(),
                labels = NightMode.values().map { it.name }.toList(),
                selectedItem = NightMode.ON,
                onItemSelect = {},
                onDismiss = {}
            )
        }
    }
}