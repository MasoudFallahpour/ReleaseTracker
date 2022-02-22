package ir.fallahpoor.releasetracker.addlibrary

sealed class Event {
    data class AddLibrary(val libraryName: String, val libraryUrlPath: String) : Event()
    data class UpdateLibraryName(val libraryName: String) : Event()
    data class UpdateLibraryUrlPath(val libraryUrlPath: String) : Event()
    object ErrorDismissed : Event()
}