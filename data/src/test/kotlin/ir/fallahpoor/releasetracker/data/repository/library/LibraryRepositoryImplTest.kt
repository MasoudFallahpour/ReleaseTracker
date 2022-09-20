package ir.fallahpoor.releasetracker.data.repository.library

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.MainDispatcherRule
import ir.fallahpoor.releasetracker.data.fakes.FakeData
import ir.fallahpoor.releasetracker.data.fakes.FakeGitHubApi
import ir.fallahpoor.releasetracker.data.fakes.FakeLibraryDao
import ir.fallahpoor.releasetracker.data.toLibrary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var libraryRepository: LibraryRepositoryImpl
    private lateinit var fakeLibraryDao: FakeLibraryDao
    private lateinit var fakeGitHubApi: FakeGitHubApi

    @Before
    fun runBeforeEachTest() {
        fakeLibraryDao = FakeLibraryDao()
        fakeGitHubApi = FakeGitHubApi()
        libraryRepository = LibraryRepositoryImpl(
            libraryDao = fakeLibraryDao,
            gitHubApi = fakeGitHubApi
        )
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
        val actualLibraries = fakeLibraryDao.getAll().map { it.toLibrary() }
        val expectedLibraries = listOf(FakeData.Coil.library)
        Truth.assertThat(actualLibraries).isEqualTo(expectedLibraries)

    }

    @Test
    fun `update library`() = runTest {

        // Given
        val library: Library = FakeData.ReleaseTracker.library
        fakeLibraryDao.insert(library.toLibraryEntity())

        // When
        val expectedLibrary = library.copy(version = "0.3")
        libraryRepository.updateLibrary(expectedLibrary)

        // Then
        val actualLibrary = fakeLibraryDao.get(library.name)?.toLibrary()
        Truth.assertThat(actualLibrary).isEqualTo(expectedLibrary)

    }

    @Test
    fun `pin library`() = runTest {

        // Given
        val library: Library = FakeData.ReleaseTracker.library
        fakeLibraryDao.insert(library.toLibraryEntity())

        // When
        libraryRepository.pinLibrary(library, true)

        // Then
        val expectedLibrary = library.copy(isPinned = true)
        val actualLibrary = fakeLibraryDao.get(library.name)?.toLibrary()
        Truth.assertThat(actualLibrary).isEqualTo(expectedLibrary)

    }

    @Test
    fun `unpin library`() = runTest {

        // Given
        val library: Library = FakeData.ReleaseTracker.library
        fakeLibraryDao.insert(library.copy(isPinned = true).toLibraryEntity())

        // When
        libraryRepository.pinLibrary(library, false)

        // Then
        val expectedLibrary = library.copy(isPinned = false)
        val actualLibrary = fakeLibraryDao.get(library.name)?.toLibrary()
        Truth.assertThat(actualLibrary).isEqualTo(expectedLibrary)

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

    @Test
    fun `search libraries`() = runTest {

        // Given
        val searchQuery = "co"
        val expectedSearchResults = fakeGitHubApi.searchRepositories(searchQuery)

        // When
        val searchResults = libraryRepository.searchLibraries(searchQuery)

        // Then
        Truth.assertThat(searchResults).isEqualTo(expectedSearchResults)

    }

}