package ir.fallahpoor.releasetracker.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DefaultSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData: SnackbarData ->
            Snackbar(
                modifier = Modifier.padding(
                    bottom = SPACE_NORMAL.dp,
                    start = SPACE_NORMAL.dp,
                    end = SPACE_NORMAL.dp
                ),
                text = {
                    Text(text = snackbarData.message)
                }
            )
        },
        modifier = modifier
    )
}