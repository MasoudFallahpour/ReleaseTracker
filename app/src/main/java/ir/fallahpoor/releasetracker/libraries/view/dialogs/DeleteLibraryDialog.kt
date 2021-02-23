package ir.fallahpoor.releasetracker.libraries.view.dialogs

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import ir.fallahpoor.releasetracker.R

@Composable
fun DeleteLibraryDialog(
    showDialog: MutableState<Boolean>,
    onDeleteClicked: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            text = {
                Text(text = stringResource(R.string.delete_selected_library))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        onDeleteClicked.invoke()
                    }
                ) {
                    Text(text = stringResource(R.string.delete))
                }
            }
        )
    }
}