package ir.fallahpoor.releasetracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
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
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import ir.fallahpoor.releasetracker.libraries.view.LibrariesListScreen
import ir.fallahpoor.releasetracker.libraries.view.dialogs.SortOrder
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var nightModeManager: NightModeManager

    @Inject
    lateinit var localStorage: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()

            NavHost(navController, startDestination = Screen.LibrariesList.route) {
                composable(Screen.LibrariesList.route) {
                    val factory = HiltViewModelFactory(LocalContext.current, it)
                    val librariesViewModel: LibrariesViewModel =
                        viewModel(LibrariesViewModel::class.simpleName, factory)
                    LibrariesListScreen(
                        librariesViewModel = librariesViewModel,
                        nightModeManager = nightModeManager,
                        currentSortOrder = getCurrentSortOrder(localStorage),
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
                composable(Screen.AddLibrary.route) {
                    val factory = HiltViewModelFactory(LocalContext.current, it)
                    val addLibraryViewModel: AddLibraryViewModel =
                        viewModel(AddLibraryViewModel::class.simpleName, factory)
                    AddLibraryScreen(
                        addLibraryViewModel = addLibraryViewModel,
                        navController = navController,
                        isDarkTheme = nightModeManager.isDarkTheme()
                    )
                }
            }

        }

    }

    private fun getCurrentSortOrder(localStorage: LocalStorage): SortOrder {
        val sortingOrder = localStorage.getOrder()
        return if (sortingOrder != null) {
            SortOrder.valueOf(sortingOrder)
        } else {
            SortOrder.A_TO_Z
        }
    }

}