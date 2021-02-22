package ir.fallahpoor.releasetracker.libraries.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.SPACE_SMALL
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme
import javax.inject.Inject

@AndroidEntryPoint
class DeleteLibraryDialog : BaseBottomSheetDialogFragment() {

    companion object {
        const val TAG = "DeleteDialog"
    }

    @Inject
    lateinit var nightModeManager: NightModeManager

    private var deleteListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            ReleaseTrackerTheme(darkTheme = nightModeManager.isDarkTheme()) {
                DeleteLibraryScreen()
            }
        }
    }

    @Composable
    private fun DeleteLibraryScreen() {
        Column {
            Title()
            ActionButtons()
        }
    }

    @Composable
    private fun Title() {
        Surface {
            Text(
                text = stringResource(R.string.delete_selected_library),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SPACE_NORMAL.dp),
            )
        }
    }


    @Composable
    private fun ActionButtons() {
        Surface {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(text = android.R.string.cancel) {
                    dismiss()
                }
                Button(R.string.delete) {
                    deleteListener?.invoke()
                    dismiss()
                }
            }
        }
    }

    @Composable
    private fun Button(@StringRes text: Int, clickListener: () -> Unit) {
        TextButton(
            onClick = clickListener,
        ) {
            Text(
                text = stringResource(text),
                style = TextStyle(fontWeight = FontWeight.W600),
                modifier = Modifier.padding(SPACE_SMALL.dp),
            )
        }
    }

    fun setListener(deleteListener: () -> Unit) {
        this.deleteListener = deleteListener
    }

    @Composable
    @Preview(name = "Delete Library Dialog")
    private fun DeleteLibraryScreenPreview() {
        DeleteLibraryScreen()
    }

}