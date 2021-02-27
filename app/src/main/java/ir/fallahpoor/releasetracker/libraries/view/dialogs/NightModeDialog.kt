package ir.fallahpoor.releasetracker.libraries.view.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.common.SPACE_SMALL

@Composable
fun NightModeDialog(
    currentNightMode: NightModeManager.Mode,
    onNightModeSelected: (NightModeManager.Mode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(
                text = stringResource(R.string.select_night_mode)
            )
        },
        text = {
            NightModeScreen(currentNightMode, onNightModeSelected)
        },
        confirmButton = {}
    )
}

@Composable
private fun NightModeScreen(
    currentNightMode: NightModeManager.Mode,
    onNightModeClick: (NightModeManager.Mode) -> Unit
) {
    Column {
        val currentNightModeState = mutableStateOf(currentNightMode)
        NightModeItem(
            text = R.string.off,
            mode = NightModeManager.Mode.OFF,
            currentNightMode = currentNightModeState,
            onNightModeClick = onNightModeClick
        )
        NightModeItem(
            text = R.string.on,
            mode = NightModeManager.Mode.ON,
            currentNightMode = currentNightModeState,
            onNightModeClick = onNightModeClick
        )
        NightModeItem(
            text = R.string.auto,
            mode = NightModeManager.Mode.AUTO,
            currentNightMode = currentNightModeState,
            onNightModeClick = onNightModeClick
        )
    }
}

@Composable
private fun NightModeItem(
    @StringRes text: Int,
    mode: NightModeManager.Mode,
    currentNightMode: MutableState<NightModeManager.Mode>,
    onNightModeClick: (NightModeManager.Mode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    currentNightMode.value = mode
                    onNightModeClick(mode)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = mode == currentNightMode.value,
            onClick = {
                currentNightMode.value = mode
                onNightModeClick(mode)
            },
            modifier = Modifier.padding(
                top = SPACE_SMALL.dp,
                end = SPACE_SMALL.dp,
                bottom = SPACE_SMALL.dp
            )
        )
        Text(
            text = stringResource(text)
        )
    }
}