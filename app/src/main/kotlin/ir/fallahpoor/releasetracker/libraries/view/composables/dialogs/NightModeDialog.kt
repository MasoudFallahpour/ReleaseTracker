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
            NightModeContent(
                currentNightMode = currentNightMode,
                onNightModeClick = onNightModeClick
            )
        },
        confirmButton = {}
    )
}

@Composable
private fun NightModeContent(
    currentNightMode: NightMode,
    onNightModeClick: (NightMode) -> Unit
) {
    Column {
        NightMode.values().forEach { nightMode: NightMode ->
            NightModeItem(
                text = stringResource(nightMode.label),
                nightMode = nightMode,
                onNightModeChange = onNightModeClick,
                isSelected = currentNightMode == nightMode
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
            modifier = Modifier.testTag(text),
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