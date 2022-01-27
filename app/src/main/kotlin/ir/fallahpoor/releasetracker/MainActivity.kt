package ir.fallahpoor.releasetracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.addlibrary.view.AddLibraryScreen
import ir.fallahpoor.releasetracker.common.managers.NightModeManager
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.libraries.view.LibrariesListScreen
import javax.inject.Inject

// FIXME: when navigating back from AddLibraryScreen, LibrariesListScreen runs twice.

@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var nightModeManager: NightModeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberAnimatedNavController()
            AnimatedNavHost(
                navController = navController,
                startDestination = Screen.LibrariesList
            ) {
                composable(Screen.LibrariesList) {
                    LibrariesListScreen(
                        nightModeManager = nightModeManager,
                        onLibraryClick = { library: Library ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(library.url))
                            // TODO handle the ActivityNotFoundException
                            startActivity(intent)
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