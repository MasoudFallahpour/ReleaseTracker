package ir.fallahpoor.releasetracker.data.utils

import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.exceptions.ExceptionParser
import ir.fallahpoor.releasetracker.data.exceptions.InternetNotConnectedException
import ir.fallahpoor.releasetracker.data.exceptions.LibraryDoesNotExistException
import org.junit.Test

class ExceptionParserTest {

    private val exceptionParser = ExceptionParser()

    @Test
    fun `getMessage returns the correct message given that the exception is LibraryDoesNotExistException`() {

        // Given
        val throwable: Throwable = LibraryDoesNotExistException()

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.LIBRARY_DOES_NOT_EXIST)

    }

    @Test
    fun `getMessage returns the correct message given that the exception is InternetNotConnectedException`() {

        // Given
        val throwable: Throwable = InternetNotConnectedException()

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.INTERNET_NOT_CONNECTED)

    }

    @Test
    fun `getMessage returns the correct message given that the exception is unknown`() {

        // Given any exception other than LibraryDoesNotExistException and InternetNotConnectedException
        val throwable: Throwable = ArithmeticException()

        // When
        val message = exceptionParser.getMessage(throwable)

        // Then
        Truth.assertThat(message).isEqualTo(ExceptionParser.SOMETHING_WENT_WRONG)

    }

}