package ir.fallahpoor.releasetracker.addlibrary.view

sealed class AddLibraryState {

    object EmptyLibraryName : AddLibraryState()
    object EmptyLibraryUrl : AddLibraryState()
    object InvalidLibraryUrl : AddLibraryState()
    object InProgress : AddLibraryState()
    object Fresh : AddLibraryState()
    object LibraryAdded : AddLibraryState()
    class Error(val message: String) : AddLibraryState()

}