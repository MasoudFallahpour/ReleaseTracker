package ir.fallahpoor.releasetracker.data.utils

import com.google.common.truth.Truth
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.HttpResponseData
import ir.fallahpoor.releasetracker.data.network.ConnectionChecker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectionCheckerTest {

    @Test
    fun `isInternetConnected returns true given that the Internet is connected`() = runTest {

        // Given
        val connectionChecker = createConnectionChecker { respondOk() }

        // When
        val isInternetConnected = connectionChecker.isInternetConnected()

        // Then
        Truth.assertThat(isInternetConnected).isTrue()

    }

    @Test
    fun `isInternetConnected returns false given that the Internet is not connected`() =
        runTest {

            // Given
            val connectionChecker = createConnectionChecker { throw IOException() }

            // When
            val isInternetConnected = connectionChecker.isInternetConnected()

            // Then
            Truth.assertThat(isInternetConnected).isFalse()

        }

    private fun createConnectionChecker(response: MockRequestHandleScope.() -> HttpResponseData): ConnectionChecker {
        val mockEngine = MockEngine { response() }
        val httpClient = HttpClient(mockEngine)
        return ConnectionChecker(httpClient)
    }

}