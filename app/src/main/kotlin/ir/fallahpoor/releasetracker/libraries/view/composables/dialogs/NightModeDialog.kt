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
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun NightModeDialog(
    currentNightMode: NightMode,
    onNightModeClick: (NightMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.select_night_mode))
        },
        text = {
            NightModeScreen(
                currentNightMode = currentNightMode,
                onNightModeClick = onNightModeClick
            )
        },
        confirmButton = {}
    )
}

@Composable
private fun NightModeScreen(
    currentNightMode: NightMode,
    onNightModeClick: (NightMode) -> Unit
) {
    Column {
        NightMode.values().forEach {
            NightModeItem(
                text = it.value,
                nightMode = it,
                onNightModeChange = { nightMode: NightMode ->
                    onNightModeClick(nightMode)
                },
                isSelected = currentNightMode == it
            )
        }
    }
}

@Composable
private fun NightModeItem(
    text: String,
    nightMode: NightMode,
    onNightModeChange: (NightMode) -> Unit,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onNightModeChange(nightMode) }),
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
            onClick = { onNightModeChange(nightMode) }
        )
        Text(text = text)
    }
}

@Preview
@Composable
private fun NightModeDialogPreview() {
    ReleaseTrackerTheme(darkTheme = false) {
        Surface {
            NightModeDialog(
                currentNightMode = NightMode.OFF,
                onNightModeClick = {},
                onDismiss = {}
            )
        }
    }
}