package ir.fallahpoor.releasetracker.addlibrary.view

sealed class State {

    object EmptyLibraryName : State()
    object EmptyLibraryUrl : State()
    object InvalidLibraryUrl : State()
    object Loading : State()
    object Fresh : State()
    object LibraryAdded : State()
    class Error(val message: String) : State()

}