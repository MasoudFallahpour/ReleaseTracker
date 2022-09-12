package ir.fallahpoor.releasetracker.data.utils

import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.InternetNotConnectedException
import ir.fallahpoor.releasetracker.data.LibraryDoesNotExistException
import org.junit.Test

class ExceptionParserTest {

    private val exceptionParser = ExceptionParser()

    @Test
    fun `correct message is returned when exception is LibraryDoesNotExistException`() {

        // Given
        val throwable: Throwable = LibraryDoesNotExistException()

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.LIBRARY_DOES_NOT_EXIST)

    }

    @Test
    fun `correct message is returned when exception is InternetNotConnectedException`() {

        // Given
        val throwable: Throwable = InternetNotConnectedException()

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.INTERNET_NOT_CONNECTED)

    }

    @Test
    fun `correct message is returned when exception is unknown`() {

        // Given any exception other than LibraryDoesNotExistException and InternetNotConnectedException
        val throwable: Throwable = ArithmeticException()

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.SOMETHING_WENT_WRONG)

    }

}