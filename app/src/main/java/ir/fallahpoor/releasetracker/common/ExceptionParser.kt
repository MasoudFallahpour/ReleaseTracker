package ir.fallahpoor.releasetracker.common

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import ir.fallahpoor.releasetracker.R
import javax.inject.Inject

class ExceptionParser @Inject constructor(private val context: Context) {

    fun getMessage(t: Throwable): String {
        return when (t) {
            is SQLiteConstraintException -> context.getString(R.string.library_name_already_exists)
            else -> context.getString(R.string.something_went_wrong)
        }
    }

}