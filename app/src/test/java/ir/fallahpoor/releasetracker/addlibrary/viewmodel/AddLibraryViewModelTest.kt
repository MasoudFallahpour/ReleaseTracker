package ir.fallahpoor.releasetracker.addlibrary.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.FakeLibraryRepository
import ir.fallahpoor.releasetracker.MainCoroutineScopeRule
import ir.fallahpoor.releasetracker.addlibrary.view.State
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddLibraryViewModelTest {

    private companion object {
        const val LIBRARY_NAME = "ReleaseTracker"
        const val LIBRARY_URL = "masoodfallahpoor/ReleaseTracker"
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    private lateinit var addLibraryViewModel: AddLibraryViewModel

    @Before
    fun runBeforeEachTest() {
        addLibraryViewModel = AddLibraryViewModel(
            libraryRepository = FakeLibraryRepository(),
            exceptionParser = ExceptionParser()
        )
    }

    @Test
    fun `addLibrary() should set the state to EmptyLibraryName when library name is empty`() {

        // Given

        // When
        addLibraryViewModel.addLibrary(
            libraryName = "",
            libraryUrl = LIBRARY_URL
        )

        // Then
        Truth.assertThat(addLibraryViewModel)
        Truth.assertThat(addLibraryViewModel.state.value)
            .isInstanceOf(State.EmptyLibraryName::class.java)

    }

    @Test
    fun `addLibrary() should set the state to EmptyLibraryUrl when library URL is empty`() {

        // Given

        // When
        addLibraryViewModel.addLibrary(
            libraryName = LIBRARY_NAME,
            libraryUrl = ""
        )

        // Then
        Truth.assertThat(addLibraryViewModel.state.value)
            .isInstanceOf(State.EmptyLibraryUrl::class.java)

    }

    @Test
    fun `addLibrary() should set the state to InvalidLibraryUrl when library URL is invalid`() {

        // Given

        // When
        addLibraryViewModel.addLibrary(
            libraryName = LIBRARY_NAME,
            libraryUrl = "InvalidLibraryUrl"
        )

        // Then
        Truth.assertThat(addLibraryViewModel.state.value)
            .isInstanceOf(State.InvalidLibraryUrl::class.java)

    }

    @Test
    fun `addLibrary() should set the state to Error when library name already exists`() {

        // Given

        // When
        addLibraryViewModel.addLibrary(
            libraryName = LIBRARY_NAME,
            libraryUrl = LIBRARY_URL
        )

        // Then
        Truth.assertThat(addLibraryViewModel.state.value)
            .isInstanceOf(State.Error::class.java)

    }

    @Test
    fun `addLibrary() should set the state to LibraryAdded when library is added successfully`() {

        // Given

        // When
        addLibraryViewModel.addLibrary(
            libraryName = "Coil",
            libraryUrl = LIBRARY_URL
        )

        // Then
        Truth.assertThat(addLibraryViewModel.state.value)
            .isInstanceOf(State.LibraryAdded::class.java)

    }

}