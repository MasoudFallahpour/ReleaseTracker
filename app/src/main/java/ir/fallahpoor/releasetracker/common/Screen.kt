package ir.fallahpoor.releasetracker.common

sealed class Screen(val route: String) {
    object LibrariesList : Screen("librariesList")
    object AddLibrary : Screen("addLibrary")
}