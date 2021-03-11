package ir.fallahpoor.releasetracker.data.utils

import com.google.common.truth.Truth
import okhttp3.internal.EMPTY_RESPONSE
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ExceptionParserTest {

    private lateinit var exceptionParser: ExceptionParser

    @Before
    fun setup() {
        exceptionParser = ExceptionParser()
    }

    @Test
    fun test_getMessage_for_HttpException() {

        // Given
        val throwable: Throwable = HttpException(Response.error<Unit>(404, EMPTY_RESPONSE))

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.LIBRARY_DOES_NOT_EXIST)

    }

    @Test
    fun test_getMessage_for_IOException() {

        // Given
        val throwable: Throwable = IOException()

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.INTERNET_NOT_CONNECTED)

    }

    @Test
    fun test_getMessage_for_other_exceptions() {

        // Given any exception other than than HttpException and IOException
        val throwable: Throwable = ArithmeticException()

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.SOMETHING_WENT_WRONG)

    }

}