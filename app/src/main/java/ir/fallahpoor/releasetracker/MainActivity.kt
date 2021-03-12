package ir.fallahpoor.releasetracker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.addlibrary.view.AddLibraryScreen
import ir.fallahpoor.releasetracker.addlibrary.viewmodel.AddLibraryViewModel
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.common.Screen
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.storage.Storage
import ir.fallahpoor.releasetracker.libraries.view.LibrariesListScreen
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
import javax.inject.Inject

// FIXME: when navigating back from AddLibraryScreen, LibrariesListScreen runs twice.
// FIXME: app crashes when clicking on a library

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var nightModeManager: NightModeManager

    @Inject
    lateinit var localStorage: Storage

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Screen.LibrariesList.route
            ) {
                composable(
                    route = Screen.LibrariesList.route
                ) { navBackStackEntry: NavBackStackEntry ->
                    val librariesViewModel = getViewModel<LibrariesViewModel>(
                        context = LocalContext.current,
                        navBackStackEntry = navBackStackEntry
                    )
                    LibrariesListScreen(
                        librariesViewModel = librariesViewModel,
                        nightModeManager = nightModeManager,
                        currentSortOrder = localStorage.getSortOrder(),
                        onLibraryClick = { library: Library ->
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(library.url)
                            }
                            startActivity(intent)
                        },
                        onAddLibraryClick = {
                            navController.navigate(Screen.AddLibrary.route)
                        }
                    )
                }
                composable(
                    route = Screen.AddLibrary.route
                ) { navBackStackEntry: NavBackStackEntry ->
                    val addLibraryViewModel = getViewModel<AddLibraryViewModel>(
                        context = LocalContext.current,
                        navBackStackEntry = navBackStackEntry
                    )
                    AddLibraryScreen(
                        addLibraryViewModel = addLibraryViewModel,
                        isDarkTheme = nightModeManager.isNightModeOn(),
                        onBackClick = {
                            navController.navigateUp()
                        }
                    )
                }
            }

        }

    }

    @Composable
    private inline fun <reified T : ViewModel> getViewModel(
        context: Context,
        navBackStackEntry: NavBackStackEntry
    ): T {
        val factory = HiltViewModelFactory(context, navBackStackEntry)
        return viewModel(T::class.simpleName, factory)
    }

}