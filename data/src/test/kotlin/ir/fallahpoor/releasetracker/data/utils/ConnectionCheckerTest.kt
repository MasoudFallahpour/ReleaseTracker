package ir.fallahpoor.releasetracker.data.utils

import com.google.common.truth.Truth
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ConnectionCheckerTest {

    @Test
    fun internetIsConnected() = runBlocking {

        // Given
        val mockEngine = MockEngine { respondOk() }
        val httpClient = HttpClient(mockEngine)
        val connectionChecker = ConnectionChecker(httpClient)

        // When
        val isInternetConnected = connectionChecker.isInternetConnected()

        // Then
        Truth.assertThat(isInternetConnected).isTrue()

    }

    @Test
    fun internetIsNotConnected() = runBlocking {

        // Given
        val mockEngine = MockEngine { respondError(status = HttpStatusCode.BadGateway) }
        val httpClient = HttpClient(mockEngine)
        val connectionChecker = ConnectionChecker(httpClient)

        // When
        val isInternetConnected = connectionChecker.isInternetConnected()

        // Then
        Truth.assertThat(isInternetConnected).isFalse()

    }

}