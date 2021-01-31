package ir.fallahpoor.releasetracker.libraries.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme
import javax.inject.Inject

@AndroidEntryPoint
class DeleteLibraryDialog : BaseBottomSheetDialogFragment() {

    companion object {
        const val TAG = "DeleteDialog"
    }

    interface DeleteListener {

        fun cancelClicked(dialogFragment: DialogFragment)

        fun deleteClicked(dialogFragment: DialogFragment)

    }

    @Inject
    lateinit var nightModeManager: NightModeManager

    private var deleteListener: DeleteListener? = null

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
            Text(
                text = stringResource(R.string.delete_selected_libraries),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SPACE_NORMAL.dp)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(text = android.R.string.cancel) {
                    deleteListener?.cancelClicked(this@DeleteLibraryDialog)
                }
                Button(R.string.delete) {
                    deleteListener?.deleteClicked(this@DeleteLibraryDialog)
                }
            }
        }
    }

    @Composable
    private fun Button(@StringRes text: Int, clickListener: () -> Unit) {
        TextButton(
            onClick = clickListener
        ) {
            Text(text = stringResource(text))
        }
    }

    fun setListener(deleteListener: DeleteListener) {
        this.deleteListener = deleteListener
    }

    @Composable
    @Preview(name = "Delete Library Dialog")
    private fun DeleteLibraryScreenPreview() {
        DeleteLibraryScreen()
    }

}