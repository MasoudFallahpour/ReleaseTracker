package ir.fallahpoor.releasetracker.data.repository.storage

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.MainDispatcherRule
import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.fakes.FakeStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StorageRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var storageRepository: StorageRepository
    private lateinit var fakeStorage: FakeStorage

    @Before
    fun runBeforeEachTest() {
        fakeStorage = FakeStorage()
        storageRepository = StorageRepositoryImpl(fakeStorage)
    }

    @Test
    fun `get last update check date`() = runTest {

        // Given
        val expectedLastUpdateCheckDate = "15:30, March"
        fakeStorage.setLastUpdateCheck(expectedLastUpdateCheckDate)

        // When
        val actualLastUpdateCheck: String = storageRepository.getLastUpdateCheck().first()

        // Then
        Truth.assertThat(actualLastUpdateCheck).isEqualTo(expectedLastUpdateCheckDate)

    }

    @Test
    fun `set last update check date`() = runTest {

        // Given
        val lastUpdateCheckDate = "15:30, March"

        // When
        storageRepository.setLastUpdateCheck(lastUpdateCheckDate)

        // Then
        Truth.assertThat(fakeStorage.getLastUpdateCheck().first()).isEqualTo(lastUpdateCheckDate)

    }

    @Test
    fun `set sort order`() = runTest {

        // Given
        fakeStorage.setSortOrder(SortOrder.Z_TO_A)

        // When
        storageRepository.setSortOrder(SortOrder.PINNED_FIRST)

        // Then
        Truth.assertThat(fakeStorage.getSortOrder()).isEqualTo(SortOrder.PINNED_FIRST)

    }

    @Test
    fun `get sort order`() = runTest {

        // Given
        fakeStorage.setSortOrder(SortOrder.Z_TO_A)

        // When
        val actualSortOrder = storageRepository.getSortOrder()

        // Then
        Truth.assertThat(actualSortOrder).isEqualTo(SortOrder.Z_TO_A)

    }

    // TODO add tests for getSortOrderAsFlow()

}