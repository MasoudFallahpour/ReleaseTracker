package ir.fallahpoor.releasetracker.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class NetworkUtils
@Inject constructor() {

    suspend fun networkReachable(): Boolean {
        return urlExists("https://www.google.com")
    }

    private suspend fun urlExists(url: String): Boolean = withContext(Dispatchers.IO) {

        val url = URL(url)
        val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.requestMethod = "GET"
        httpURLConnection.connect()
        val code: Int = httpURLConnection.responseCode
        code == 200

    }

}