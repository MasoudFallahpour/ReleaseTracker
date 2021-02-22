package ir.fallahpoor.releasetracker.libraries.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme
import javax.inject.Inject

@AndroidEntryPoint
class NightModeDialog : BaseBottomSheetDialogFragment() {

    companion object {
        const val TAG = "SelectNightModeDialog"
    }

    @Inject
    lateinit var nightModeManager: NightModeManager

    @Inject
    lateinit var localStorage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            ReleaseTrackerTheme(darkTheme = nightModeManager.isDarkTheme()) {
                val currentMode = NightModeManager.Mode.valueOf(
                    localStorage.getNightMode() ?: NightModeManager.Mode.OFF.name
                )
                NightModeScreen(currentMode)
            }
        }
    }

    @Composable
    private fun NightModeScreen(currentMode: NightModeManager.Mode) {
        Surface {
            Column {
                Text(
                    text = stringResource(R.string.select_night_mode),
                    modifier = Modifier.padding(SPACE_NORMAL.dp)
                )
                NightModeItem(R.string.off, NightModeManager.Mode.OFF, currentMode)
                NightModeItem(R.string.on, NightModeManager.Mode.ON, currentMode)
                NightModeItem(R.string.auto, NightModeManager.Mode.AUTO, currentMode)
            }
        }
    }

    @Composable
    private fun NightModeItem(
        @StringRes text: Int,
        mode: NightModeManager.Mode,
        currentMode: NightModeManager.Mode
    ) {
        val clickListener = {
            nightModeManager.setNightMode(mode)
            dismiss()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = clickListener)
        ) {
            RadioButton(
                selected = mode == currentMode,
                onClick = clickListener,
                modifier = Modifier.padding(SPACE_NORMAL.dp)
            )
            Text(
                text = stringResource(text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = SPACE_NORMAL.dp,
                        bottom = SPACE_NORMAL.dp,
                        end = SPACE_NORMAL.dp
                    )
            )
        }
    }

    @Composable
    @Preview(name = "Night Mode Dialog")
    private fun NightModeScreenPreview() {
        NightModeScreen(currentMode = NightModeManager.Mode.OFF)
    }

}