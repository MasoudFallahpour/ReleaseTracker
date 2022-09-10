package ir.fallahpoor.releasetracker.data.repository.storage

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.fakes.FakeStorage
import ir.fallahpoor.releasetracker.data.utils.SortOrder
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
class StorageRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var storageRepository: StorageRepository
    private lateinit var fakeStorage: FakeStorage

    @Before
    fun runBeforeEachTest() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakeStorage = FakeStorage()
        storageRepository = StorageRepositoryImpl(fakeStorage)
    }

    @Before
    fun runAfterEachTest() {
        Dispatchers.resetMain()
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