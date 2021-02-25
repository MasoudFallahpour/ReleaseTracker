package ir.fallahpoor.releasetracker.libraries.view.dialogs

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ir.fallahpoor.releasetracker.R

@Composable
fun DeleteLibraryDialog(
    onDeleteClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        text = {
            Text(
                text = stringResource(R.string.delete_selected_library)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDeleteClicked()
                }
            ) {
                Text(
                    text = stringResource(R.string.delete)
                )
            }
        }
    )
}