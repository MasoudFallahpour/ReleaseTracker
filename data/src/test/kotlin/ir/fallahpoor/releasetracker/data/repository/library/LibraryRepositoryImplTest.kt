package ir.fallahpoor.releasetracker.data.repository.library

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.fakes.FakeData
import ir.fallahpoor.releasetracker.data.fakes.FakeGitHubApi
import ir.fallahpoor.releasetracker.data.fakes.FakeLibraryDao
import ir.fallahpoor.releasetracker.data.toLibrary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var libraryRepository: LibraryRepositoryImpl
    private lateinit var fakeLibraryDao: FakeLibraryDao

    @Before
    fun runBeforeEachTest() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakeLibraryDao = FakeLibraryDao()
        libraryRepository = LibraryRepositoryImpl(
            libraryDao = fakeLibraryDao,
            gitHubApi = FakeGitHubApi()
        )
    }

    @Before
    fun runAfterEachTest() {
        Dispatchers.resetMain()
    }

    @Test
    fun `get library`() = runTest {

        // Given
        val expectedLibrary: Library = FakeData.ReleaseTracker.library
        fakeLibraryDao.insert(expectedLibrary.toLibraryEntity())

        // When
        val actualLibrary: Library? = libraryRepository.getLibrary(expectedLibrary.name)

        // Then
        Truth.assertThat(actualLibrary).isEqualTo(expectedLibrary)

    }

    @Test
    fun `get library version`() = runTest {

        // When
        val version: String = libraryRepository.getLibraryVersion(
            FakeData.ReleaseTracker.NAME,
            FakeData.ReleaseTracker.URL
        )

        // Then
        Truth.assertThat(version).isEqualTo(FakeData.ReleaseTracker.VERSION)

    }

    @Test
    fun `add library`() = runTest {

        // Given
        val expectedLibrary: Library = FakeData.Coil.library

        // When
        libraryRepository.addLibrary(
            libraryName = expectedLibrary.name,
            libraryUrl = expectedLibrary.url,
            libraryVersion = expectedLibrary.version
        )

        // Then
        val actualLibrary: Library? = fakeLibraryDao.get(expectedLibrary.name)?.toLibrary()
        Truth.assertThat(actualLibrary).isEqualTo(expectedLibrary)

    }

    @Test
    fun `delete library`() = runTest {

        // Given
        fakeLibraryDao.insert(FakeData.ReleaseTracker.library.toLibraryEntity())
        fakeLibraryDao.insert(FakeData.Coil.library.toLibraryEntity())

        // When
        libraryRepository.deleteLibrary(FakeData.ReleaseTracker.library)

        // Then
        Truth.assertThat(libraryRepository.getLibraries()).isEqualTo(listOf(FakeData.Coil.library))

    }

    @Test
    fun `update library`() = runTest {

        // Given
        val expectedLibrary: Library = FakeData.ReleaseTracker.library
        fakeLibraryDao.insert(expectedLibrary.toLibraryEntity())

        // When
        val updatedLibrary = expectedLibrary.copy(version = "0.3")
        libraryRepository.updateLibrary(updatedLibrary)

        // Then
        Truth.assertThat(libraryRepository.getLibrary(expectedLibrary.name))
            .isEqualTo(updatedLibrary)

    }

    @Test
    fun `pin library`() = runTest {

        // Given
        val expectedLibrary: Library = FakeData.ReleaseTracker.library
        fakeLibraryDao.insert(expectedLibrary.toLibraryEntity())

        // When
        libraryRepository.pinLibrary(expectedLibrary, true)

        // Then
        val pinnedLibrary = expectedLibrary.copy(isPinned = true)
        Truth.assertThat(libraryRepository.getLibrary(expectedLibrary.name))
            .isEqualTo(pinnedLibrary)

    }

    @Test
    fun `unpin library`() = runTest {

        // Given
        val expectedLibrary: Library = FakeData.ReleaseTracker.library
        fakeLibraryDao.insert(expectedLibrary.copy(isPinned = true).toLibraryEntity())

        // When
        libraryRepository.pinLibrary(expectedLibrary, false)

        // Then
        val actualLibrary = expectedLibrary.copy(isPinned = false)
        Truth.assertThat(libraryRepository.getLibrary(expectedLibrary.name))
            .isEqualTo(actualLibrary)

    }

    @Test
    fun `get libraries`() = runTest {

        // Given
        fakeLibraryDao.insert(FakeData.ReleaseTracker.library.toLibraryEntity())
        fakeLibraryDao.insert(FakeData.Coil.library.toLibraryEntity())

        // When
        val actualLibraries = libraryRepository.getLibraries()

        // Then
        val expectedLibraries = listOf(FakeData.Coil.library, FakeData.ReleaseTracker.library)
        Truth.assertThat(actualLibraries).isEqualTo(expectedLibraries)

    }

    @Test
    fun `flow of libraries should return all libraries sorted by name in ascending order`() =
        runTest {

            // Given
            fakeLibraryDao.insert(FakeData.ReleaseTracker.library.toLibraryEntity())
            fakeLibraryDao.insert(FakeData.Coil.library.toLibraryEntity())
            fakeLibraryDao.insert(FakeData.Timber.library.toLibraryEntity())

            // When
            val actualLibraries: List<Library> = libraryRepository.getLibrariesAsFlow().first()

            // Then
            val expectedLibraries = listOf(
                FakeData.Coil.library,
                FakeData.ReleaseTracker.library,
                FakeData.Timber.library
            )
            Truth.assertThat(actualLibraries).isEqualTo(expectedLibraries)

        }

    // TODO add a test for searchLibraries
}