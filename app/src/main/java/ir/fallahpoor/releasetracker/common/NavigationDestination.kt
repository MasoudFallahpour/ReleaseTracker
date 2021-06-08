package ir.fallahpoor.releasetracker.common

sealed class NavigationDestination(val route: String) {
    object LibrariesList : NavigationDestination("librariesList")
    object AddLibrary : NavigationDestination("addLibrary")
}