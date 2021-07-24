package ir.fallahpoor.releasetracker.libraries.view.composables.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.SPACE_SMALL
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
            Text(text = stringResource(R.string.select_sorting_order))
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
        SortItem(
            text = stringResource(R.string.a_to_z),
            sortOrder = SortOrder.A_TO_Z,
            onSortOrderClick = onSortOrderClick,
            isSelected = currentSortOrder == SortOrder.A_TO_Z
        )
        SortItem(
            text = stringResource(R.string.z_to_a),
            sortOrder = SortOrder.Z_TO_A,
            onSortOrderClick = onSortOrderClick,
            isSelected = currentSortOrder == SortOrder.Z_TO_A
        )
        SortItem(
            text = stringResource(R.string.pinned_first),
            sortOrder = SortOrder.PINNED_FIRST,
            onSortOrderClick = onSortOrderClick,
            isSelected = currentSortOrder == SortOrder.PINNED_FIRST
        )
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
            modifier = Modifier
                .padding(
                    top = SPACE_SMALL.dp,
                    end = SPACE_SMALL.dp,
                    bottom = SPACE_SMALL.dp
                )
                .semantics { testTag = text },
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