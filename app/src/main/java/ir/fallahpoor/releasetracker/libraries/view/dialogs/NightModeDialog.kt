package ir.fallahpoor.releasetracker.libraries.view.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
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
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun NightModeDialog(showDialog: MutableState<Boolean>, nightModeManager: NightModeManager) {
    if (showDialog.value) {
        var nightMode: NightModeManager.Mode? = null
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text(
                    text = stringResource(R.string.select_night_mode)
                )
            },
            text = {
                NightModeScreen(nightModeManager) {
                    nightMode = it
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        nightMode?.let {
                            nightModeManager.setNightMode(it)
                        }
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            }
        )
    }
}

@Composable
private fun NightModeScreen(
    nightModeManager: NightModeManager,
    itemClickListener: (NightModeManager.Mode) -> Unit
) {
    ReleaseTrackerTheme(darkTheme = nightModeManager.isDarkTheme()) {
        Surface {
            Column {
                val currentNightMode = mutableStateOf(nightModeManager.getCurrentNightMode())
                NightModeItem(
                    text = R.string.off,
                    mode = NightModeManager.Mode.OFF,
                    currentNightMode = currentNightMode,
                    itemClickListener = itemClickListener
                )
                NightModeItem(
                    text = R.string.on,
                    mode = NightModeManager.Mode.ON,
                    currentNightMode = currentNightMode,
                    itemClickListener = itemClickListener
                )
                NightModeItem(
                    text = R.string.auto,
                    mode = NightModeManager.Mode.AUTO,
                    currentNightMode = currentNightMode,
                    itemClickListener = itemClickListener
                )
            }
        }
    }
}

@Composable
private fun NightModeItem(
    @StringRes text: Int,
    mode: NightModeManager.Mode,
    currentNightMode: MutableState<NightModeManager.Mode>,
    itemClickListener: (NightModeManager.Mode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                currentNightMode.value = mode
                itemClickListener.invoke(mode)
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = mode == currentNightMode.value,
            onClick = {
                currentNightMode.value = mode
                itemClickListener.invoke(mode)
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