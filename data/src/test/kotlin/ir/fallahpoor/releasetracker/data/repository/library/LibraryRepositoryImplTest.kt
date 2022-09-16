package ir.fallahpoor.releasetracker.data.repository.library

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.TestData.COIL
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_NAME_1
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_NAME_2
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_URL_1
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_URL_2
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_VERSION_1
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_VERSION_2
import ir.fallahpoor.releasetracker.data.TestData.RELEASE_TRACKER
import ir.fallahpoor.releasetracker.data.TestData.TIMBER
import ir.fallahpoor.releasetracker.data.TestData.VERSION_1
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
            githubWebservice = FakeGitHubApi()
        )
    }

    @Before
    fun runAfterEachTest() {
        Dispatchers.resetMain()
    }

    @Test
    fun `get library`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER)

        // When
        val library: Library? = libraryRepository.getLibrary(LIBRARY_NAME_1)

        // Then
        Truth.assertThat(library).isEqualTo(RELEASE_TRACKER.toLibrary())

    }

    @Test
    fun `library version name is returned when it is available`() = runTest {

        // Given

        // When
        val version: String = libraryRepository.getLibraryVersion(LIBRARY_NAME_1, LIBRARY_URL_1)

        // Then
        Truth.assertThat(version).isEqualTo(VERSION_1)

    }

    @Test
    fun `library tag name is returned when its version name is not available`() = runTest {

        // Given

        // When
        val version: String = libraryRepository.getLibraryVersion(LIBRARY_NAME_2, LIBRARY_URL_2)

        // Then
        Truth.assertThat(version).isEqualTo(LIBRARY_VERSION_2)

    }

    @Test
    fun `add library`() = runTest {

        // Given

        // When
        libraryRepository.addLibrary(LIBRARY_NAME_1, LIBRARY_URL_1, LIBRARY_VERSION_1)

        // Then
        val library: Library? = libraryRepository.getLibrary(LIBRARY_NAME_1)
        Truth.assertThat(library).isEqualTo(RELEASE_TRACKER.toLibrary())

    }

    @Test
    fun `delete library`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER)
        fakeLibraryDao.insert(COIL)

        // When
        libraryRepository.deleteLibrary(RELEASE_TRACKER.toLibrary())

        // Then
        Truth.assertThat(libraryRepository.getLibraries()).isEqualTo(listOf(COIL.toLibrary()))

    }

    @Test
    fun `update library`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER)

        // When
        val updatedLibrary = RELEASE_TRACKER.copy(version = "0.3")
        libraryRepository.updateLibrary(updatedLibrary.toLibrary())

        // Then
        Truth.assertThat(libraryRepository.getLibrary(RELEASE_TRACKER.name))
            .isEqualTo(updatedLibrary.toLibrary())

    }

    @Test
    fun `pin library`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER)

        // When
        libraryRepository.pinLibrary(RELEASE_TRACKER.toLibrary(), true)

        // Then
        val pinnedLibrary = RELEASE_TRACKER.copy(pinned = 1)
        Truth.assertThat(libraryRepository.getLibrary(RELEASE_TRACKER.name))
            .isEqualTo(pinnedLibrary.toLibrary())

    }

    @Test
    fun `unpin library`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER.copy(pinned = 1))

        // When
        libraryRepository.pinLibrary(RELEASE_TRACKER.toLibrary(), false)

        // Then
        val unpinnedLibrary = RELEASE_TRACKER.copy(pinned = 0)
        Truth.assertThat(libraryRepository.getLibrary(RELEASE_TRACKER.name))
            .isEqualTo(unpinnedLibrary.toLibrary())

    }

    @Test
    fun `get libraries`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER)
        fakeLibraryDao.insert(COIL)

        // When
        val libraries = libraryRepository.getLibraries()

        // Then
        Truth.assertThat(libraries).isEqualTo(listOf(COIL, RELEASE_TRACKER).map { it.toLibrary() })

    }

    @Test
    fun `flow of libraries should return all libraries sorted by name in ascending order`() =
        runTest {

            // Given
            fakeLibraryDao.insert(RELEASE_TRACKER)
            fakeLibraryDao.insert(COIL)
            fakeLibraryDao.insert(TIMBER)

            // When
            val libraries: List<Library> = libraryRepository.getLibrariesAsFlow().first()

            // Then
            Truth.assertThat(libraries)
                .isEqualTo(listOf(COIL, RELEASE_TRACKER, TIMBER).map { it.toLibrary() })

        }

}