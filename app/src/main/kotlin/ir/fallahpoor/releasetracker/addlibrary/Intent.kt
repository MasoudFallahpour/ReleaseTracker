package ir.fallahpoor.releasetracker.addlibrary

sealed class Intent {
    data class AddLibrary(val libraryName: String, val libraryUrlPath: String) : Intent()
    data class UpdateLibraryName(val libraryName: String) : Intent()
    data class UpdateLibraryUrlPath(val libraryUrlPath: String) : Intent()
    object Reset : Intent()
}