package ir.fallahpoor.releasetracker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.addlibrary.view.AddLibraryScreen
import ir.fallahpoor.releasetracker.common.managers.NightModeManager
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.libraries.view.LibrariesListScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var nightModeManager: NightModeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screen.LibrariesList
            ) {
                composable(Screen.LibrariesList) {
                    LibrariesListScreen(
                        nightModeManager = nightModeManager,
                        onLibraryClick = { library: Library ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(library.url))
                            try {
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(
                                    this@MainActivity,
                                    getString(R.string.no_browser_found_message, library.url),
                                    Toast.LENGTH_LONG
                                )
                            }
                        },
                        onAddLibraryClick = { navController.navigate(Screen.AddLibrary) }
                    )
                }
                composable(Screen.AddLibrary) {
                    AddLibraryScreen(
                        isDarkTheme = nightModeManager.isNightModeOn,
                        onBackClick = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}

private object Screen {
    const val LibrariesList = "librariesList"
    const val AddLibrary = "addLibrary"
}