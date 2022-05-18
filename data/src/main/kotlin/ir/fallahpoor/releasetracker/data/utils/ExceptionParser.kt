package ir.fallahpoor.releasetracker.data.utils

import io.ktor.client.plugins.*
import java.io.IOException
import javax.inject.Inject

class ExceptionParser @Inject constructor() {

    companion object {
        const val LIBRARY_DOES_NOT_EXIST = "Library URL does not exist."
        const val INTERNET_NOT_CONNECTED = "Internet not connected."
        const val SOMETHING_WENT_WRONG = "Unfortunately, something went wrong."
    }

    fun getMessage(t: Throwable): String = when (t) {
        is ClientRequestException -> LIBRARY_DOES_NOT_EXIST
        is IOException -> INTERNET_NOT_CONNECTED
        else -> SOMETHING_WENT_WRONG
    }

}