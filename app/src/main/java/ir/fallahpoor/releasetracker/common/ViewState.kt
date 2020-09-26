package ir.fallahpoor.releasetracker.common

sealed class ViewState<T> {

    class DataLoadedState<T>(val data: T) : ViewState<T>()
    class ErrorState<T>(val errorMessage: String) : ViewState<T>()
    class LoadingState<T> : ViewState<T>()

    companion object {
        fun <T> success(t: T) = DataLoadedState(t)

        fun <T> error(errorMessage: String) = ErrorState<T>(errorMessage)

        fun <T> loading() = LoadingState<T>()
    }

}