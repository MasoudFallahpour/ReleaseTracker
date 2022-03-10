package ir.fallahpoor.releasetracker.data.utils

import com.google.common.truth.Truth
import okhttp3.internal.EMPTY_RESPONSE
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ExceptionParserTest {

    private val exceptionParser = ExceptionParser()

    @Test
    fun `correct message is returned when the exception is HttpException`() {

        // Given
        val throwable: Throwable = HttpException(Response.error<Unit>(404, EMPTY_RESPONSE))

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.LIBRARY_DOES_NOT_EXIST)

    }

    @Test
    fun `correct message is returned when the exception is IOException`() {

        // Given
        val throwable: Throwable = IOException()

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.INTERNET_NOT_CONNECTED)

    }

    @Test
    fun `correct message is returned when the exception is neither IOException nor HttpException`() {

        // Given any exception other than than HttpException and IOException
        val throwable: Throwable = ArithmeticException()

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.SOMETHING_WENT_WRONG)

    }

}