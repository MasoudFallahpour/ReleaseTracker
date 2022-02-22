package ir.fallahpoor.releasetracker.libraries.view.composables.dialogs

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun SortOrderDialog(
    currentSortOrder: SortOrder,
    onSortOrderClick: (SortOrder) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.select_sort_order))
        },
        text = {
            SortOrderScreen(
                currentSortOrder = currentSortOrder,
                onSortOrderClick = onSortOrderClick
            )
        },
        confirmButton = {}
    )
}

@Composable
private fun SortOrderScreen(
    currentSortOrder: SortOrder,
    onSortOrderClick: (SortOrder) -> Unit
) {
    Column {
        SortOrder.values().forEach { sortOrder: SortOrder ->
            SortItem(
                text = stringResource(sortOrder.label),
                sortOrder = sortOrder,
                onSortOrderClick = onSortOrderClick,
                isSelected = currentSortOrder == sortOrder
            )
        }
    }
}

@Composable
private fun SortItem(
    text: String,
    sortOrder: SortOrder,
    onSortOrderClick: (SortOrder) -> Unit,
    isSelected: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onSortOrderClick(sortOrder) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier.testTag(text),
            selected = isSelected,
            onClick = { onSortOrderClick(sortOrder) }
        )
        Text(text = text)
    }
}

@Preview
@Composable
private fun SortOrderDialogPreview() {
    ReleaseTrackerTheme(darkTheme = false) {
        Surface {
            SortOrderDialog(
                currentSortOrder = SortOrder.A_TO_Z,
                onSortOrderClick = {},
                onDismiss = {}
            )
        }
    }
}