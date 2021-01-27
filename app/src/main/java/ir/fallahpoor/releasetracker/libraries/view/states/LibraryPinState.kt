package ir.fallahpoor.releasetracker.libraries.view.states

sealed class LibraryPinState {

    object Fresh : LibraryPinState()
    object InProgress : LibraryPinState()
    class Error(val message: String) : LibraryPinState()

}