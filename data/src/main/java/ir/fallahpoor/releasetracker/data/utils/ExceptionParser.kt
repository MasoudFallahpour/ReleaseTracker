package ir.fallahpoor.releasetracker.data.utils

import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ExceptionParser @Inject constructor() {

    fun getMessage(t: Throwable): String {
        return when (t) {
            is HttpException -> "Library URL does not exist."
            is IOException -> "Internet not connected."
            else -> "Unfortunately, something went wrong."
        }
    }

}