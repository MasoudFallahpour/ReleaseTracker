package ir.fallahpoor.releasetracker.data.utils

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConnectionChecker
@Inject constructor(private val httpClient: HttpClient) {

    suspend fun isInternetConnected(): Boolean = withContext(Dispatchers.IO) {
        try {
            httpClient.get("https://www.google.com")
            true
        } catch (e: Exception) {
            false
        }
    }

}