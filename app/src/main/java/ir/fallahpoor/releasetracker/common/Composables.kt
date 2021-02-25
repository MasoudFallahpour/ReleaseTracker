package ir.fallahpoor.releasetracker.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
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
                snackbarData = snackbarData,
                modifier = Modifier.padding(
                    bottom = SPACE_NORMAL.dp,
                    start = SPACE_NORMAL.dp,
                    end = SPACE_NORMAL.dp
                )
            )
        },
        modifier = modifier
    )
}