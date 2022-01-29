package ir.fallahpoor.releasetracker.libraries.view.composables.dialogs

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun DeleteLibraryDialog(
    libraryName: String,
    onDeleteClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = stringResource(R.string.delete_selected_library, libraryName),
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            TextButton(onClick = onDeleteClicked) {
                Text(text = stringResource(R.string.delete))
            }
        }
    )
}

@Preview
@Composable
private fun DeleteLibraryDialogPreview() {
    ReleaseTrackerTheme(darkTheme = false) {
        Surface {
            DeleteLibraryDialog(
                libraryName = "Glide",
                onDeleteClicked = {},
                onDismiss = {}
            )
        }
    }
}