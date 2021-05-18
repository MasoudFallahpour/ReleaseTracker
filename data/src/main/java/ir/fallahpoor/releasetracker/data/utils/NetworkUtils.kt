package ir.fallahpoor.releasetracker.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class NetworkUtils
@Inject constructor() {

    suspend fun isNetworkReachable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://www.google.com")
            val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.connect()
            val code: Int = httpURLConnection.responseCode
            code == 200
        } catch (e: Exception) {
            false
        }
    }

}