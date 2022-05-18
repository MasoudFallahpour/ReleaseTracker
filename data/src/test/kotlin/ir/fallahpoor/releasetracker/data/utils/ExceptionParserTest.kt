package ir.fallahpoor.releasetracker.data.utils

import com.google.common.truth.Truth
import org.junit.Test
import java.io.IOException

class ExceptionParserTest {

    private val exceptionParser = ExceptionParser()

    // TODO Find a way to create an instance of ClientRequestException
//    @Test
//    fun `correct message is returned when the exception is ClientResponseException`() {
//
//        // Given
//        val throwable: Throwable = ClientRequestException()
//
//        // When
//        val message = exceptionParser.getMessage(throwable)
//
//        // Then
//        Truth.assertThat(message).isEqualTo(ExceptionParser.LIBRARY_DOES_NOT_EXIST)
//
//    }

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
    fun `correct message is returned when the exception is neither IOException nor ClientResponseException`() {

        // Given any exception other than ClientResponseException and IOException
        val throwable: Throwable = ArithmeticException()

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.SOMETHING_WENT_WRONG)

    }

}