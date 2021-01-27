package ir.fallahpoor.releasetracker.libraries.view.states

sealed class LibraryDeleteState {

    object Fresh : LibraryDeleteState()
    object InProgress : LibraryDeleteState()
    object Deleted : LibraryDeleteState()
    class Error(val message: String) : LibraryDeleteState()

}