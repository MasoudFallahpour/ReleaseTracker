package ir.fallahpoor.releasetracker.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SnackbarState {
    var show by mutableStateOf(true)
}

@Composable
private fun TransientSnackbar(
    modifier: Modifier = Modifier,
    snackbarState: SnackbarState,
    text: String,
    timeout: Long = 4_000
) {

    if (snackbarState.show) {
        Snackbar(modifier = modifier) {
            Text(text = text)
        }
        val coroutineScope = rememberCoroutineScope()
        coroutineScope.launch {
            delay(timeout)
            snackbarState.show = false
        }
    }

}

@Composable
fun Snackbar(snackbarState: SnackbarState, message: String) {
    TransientSnackbar(
        modifier = Modifier.padding(
            start = SPACE_NORMAL.dp,
            end = SPACE_NORMAL.dp,
            bottom = SPACE_NORMAL.dp
        ),
        snackbarState = snackbarState,
        text = message
    )
}