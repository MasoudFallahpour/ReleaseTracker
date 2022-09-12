package ir.fallahpoor.releasetracker.data.utils

import ir.fallahpoor.releasetracker.data.InternetNotConnectedException
import ir.fallahpoor.releasetracker.data.LibraryDoesNotExistException
import javax.inject.Inject

class ExceptionParser @Inject constructor() {

    companion object {
        const val LIBRARY_DOES_NOT_EXIST = "Library URL does not exist."
        const val INTERNET_NOT_CONNECTED = "Internet not connected."
        const val SOMETHING_WENT_WRONG = "Unfortunately, something went wrong."
    }

    fun getMessage(t: Throwable): String = when (t) {
        is LibraryDoesNotExistException -> LIBRARY_DOES_NOT_EXIST
        is InternetNotConnectedException -> INTERNET_NOT_CONNECTED
        else -> SOMETHING_WENT_WRONG
    }

}