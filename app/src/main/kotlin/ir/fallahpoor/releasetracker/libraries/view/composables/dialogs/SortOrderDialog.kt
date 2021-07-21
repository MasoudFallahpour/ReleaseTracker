package ir.fallahpoor.releasetracker.libraries.view.composables.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun SortOrderDialog(
    currentSortOrder: SortOrder,
    onSortOrderClick: (SortOrder) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = stringResource(R.string.select_sorting_order))
        },
        text = {
            SortOrderScreen(currentSortOrder, onSortOrderClick)
        },
        confirmButton = {}
    )
}

@Composable
private fun SortOrderScreen(currentSortOrder: SortOrder, onSortOrderClick: (SortOrder) -> Unit) {
    Column {
        SortItem(
            text = stringResource(R.string.a_to_z),
            sortOrder = SortOrder.A_TO_Z,
            currentSortOrder = currentSortOrder,
            onSortOrderClick = onSortOrderClick
        )
        SortItem(
            text = stringResource(R.string.z_to_a),
            sortOrder = SortOrder.Z_TO_A,
            currentSortOrder = currentSortOrder,
            onSortOrderClick = onSortOrderClick
        )
        SortItem(
            text = stringResource(R.string.pinned_first),
            sortOrder = SortOrder.PINNED_FIRST,
            currentSortOrder = currentSortOrder,
            onSortOrderClick = onSortOrderClick
        )
    }
}

@Composable
private fun SortItem(
    text: String,
    sortOrder: SortOrder,
    currentSortOrder: SortOrder,
    onSortOrderClick: (SortOrder) -> Unit
) {
    val textColor = if (currentSortOrder == sortOrder) {
        MaterialTheme.colors.secondary
    } else {
        Color.Unspecified
    }
    Text(
        text = text,
        color = textColor,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onSortOrderClick(sortOrder)
                }
            )
            .padding(SPACE_NORMAL.dp)
    )
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