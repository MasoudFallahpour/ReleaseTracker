package ir.fallahpoor.releasetracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.addlibrary.view.AddLibraryScreen
import ir.fallahpoor.releasetracker.addlibrary.viewmodel.AddLibraryViewModel
import ir.fallahpoor.releasetracker.common.managers.NightModeManager
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.libraries.view.LibrariesListScreen
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
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

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = NavigationDestination.LibrariesList.route
            ) {

                composable(route = NavigationDestination.LibrariesList.route) { navBackStackEntry: NavBackStackEntry ->

                    val librariesViewModel = hiltViewModel<LibrariesViewModel>(navBackStackEntry)

                    LibrariesListScreen(
                        librariesViewModel = librariesViewModel,
                        nightModeManager = nightModeManager,
                        onLibraryClick = { library: Library ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(library.url))
                            startActivity(intent)
                        },
                        onAddLibraryClick = {
                            navController.navigate(NavigationDestination.AddLibrary.route)
                        }
                    )

                }

                composable(route = NavigationDestination.AddLibrary.route) { navBackStackEntry: NavBackStackEntry ->

                    val addLibraryViewModel = hiltViewModel<AddLibraryViewModel>(navBackStackEntry)

                    AddLibraryScreen(
                        addLibraryViewModel = addLibraryViewModel,
                        isDarkTheme = nightModeManager.isNightModeOn,
                        onBackClick = { navController.navigateUp() }
                    )

                }

            }

        }

    }

}

private sealed class NavigationDestination(val route: String) {
    object LibrariesList : NavigationDestination("librariesList")
    object AddLibrary : NavigationDestination("addLibrary")
}