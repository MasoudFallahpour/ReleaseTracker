package ir.fallahpoor.releasetracker.common.composables

import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun Screen(
    isDarkTheme: Boolean,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    ReleaseTrackerTheme(darkTheme = isDarkTheme) {
        Scaffold(
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