package ir.fallahpoor.releasetracker.addlibrary.view

data class AddLibraryScreenUiState(
    val libraryName: String = "",
    val libraryUrlPath: String = "",
    val addLibraryState: AddLibraryState = AddLibraryState.Initial
)

sealed class AddLibraryState {
    object Initial : AddLibraryState()
    object EmptyLibraryName : AddLibraryState()
    object EmptyLibraryUrl : AddLibraryState()
    object InvalidLibraryUrl : AddLibraryState()
    object InProgress : AddLibraryState()
    object LibraryAdded : AddLibraryState()
    data class Error(val message: String) : AddLibraryState()
}