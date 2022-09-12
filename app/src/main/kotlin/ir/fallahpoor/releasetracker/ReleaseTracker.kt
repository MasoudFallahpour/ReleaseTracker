package ir.fallahpoor.releasetracker

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.fallahpoor.releasetracker.addlibrary.ui.AddLibraryScreen
import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.repository.library.Library
import ir.fallahpoor.releasetracker.libraries.ui.LibrariesListScreen

@Composable
fun ReleaseTracker() {
    val navController = rememberNavController()
    val nightModeViewModel: NightModeViewModel = hiltViewModel()
    val nightMode: NightMode by nightModeViewModel.state.collectAsState()
    NavHost(
        navController = navController,
        startDestination = Screen.LibrariesList
    ) {
        composable(Screen.LibrariesList) {
            val context = LocalContext.current
            LibrariesListScreen(
                isNightModeSupported = nightModeViewModel.isNightModeSupported,
                currentNightMode = nightMode,
                onNightModeChange = { nightMode: NightMode ->
                    nightModeViewModel.handleEvent(Event.ChangeNightMode(nightMode))
                },
                onLibraryClick = { library: Library ->
                    openUrl(context, library)
                },
                onAddLibraryClick = { navController.navigate(Screen.AddLibrary) },
            )
        }
        composable(Screen.AddLibrary) {
            val isNightModeOn = when (nightMode) {
                NightMode.OFF -> false
                NightMode.ON -> true
                NightMode.AUTO -> isSystemInDarkTheme()
            }
            AddLibraryScreen(
                isDarkTheme = isNightModeOn,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}

private fun openUrl(context: Context, library: Library) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(library.url))
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.no_browser_found_message, library.url),
            Toast.LENGTH_LONG
        ).show()
    }
}

private object Screen {
    const val LibrariesList = "librariesList"
    const val AddLibrary = "addLibrary"
}