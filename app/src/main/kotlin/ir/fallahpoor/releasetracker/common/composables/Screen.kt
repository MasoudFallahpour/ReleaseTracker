package ir.fallahpoor.releasetracker.common.composables

import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    ReleaseTrackerTheme(darkTheme = isDarkTheme) {
        Scaffold(
            modifier = modifier,
            topBar = topBar,
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) {
            content()
        }
    }
}