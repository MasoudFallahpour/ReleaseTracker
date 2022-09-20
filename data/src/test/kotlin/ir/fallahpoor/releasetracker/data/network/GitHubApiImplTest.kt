package ir.fallahpoor.releasetracker.data.network

import com.google.common.truth.Truth
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import ir.fallahpoor.releasetracker.data.fakes.FakeData
import ir.fallahpoor.releasetracker.data.fakes.FakeEngine
import ir.fallahpoor.releasetracker.data.network.models.SearchResults
import ir.fallahpoor.releasetracker.data.toSearchResultItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubApiImplTest {

    private val httpClient = HttpClient(FakeEngine.engine) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }
    private val gitHubApiImpl = GitHubApiImpl(httpClient)

    @Test
    fun `getLatestRelease returns the latest release given that the library exists`() =
        runTest {

            // Given
            FakeEngine.throwException = false

            // When
            val libraryVersion: LibraryVersion = gitHubApiImpl.getLatestRelease(
                owner = FakeData.ReleaseTracker.OWNER,
                repository = FakeData.ReleaseTracker.REPOSITORY_NAME
            )

            // Then
            Truth.assertThat(libraryVersion.name).isEqualTo(FakeData.ReleaseTracker.VERSION)

        }

    @Test(expected = Exception::class)
    fun `getLatestRelease throws an exception given that the library does not exist`() =
        runTest {

            // Given
            FakeEngine.throwException = true

            // When
            gitHubApiImpl.getLatestRelease(
                owner = "someNonExistentOwner", repository = "someNonExistentRepository"
            )

            // Then we expect to receive an exception

        }

    @Test
    fun `searchRepositories returns the search results given that there is no exception`() =
        runTest {

            // Given
            FakeEngine.throwException = false
            val searchQuery = "CO"
            val expectedSearchResults = createFakeSearchResults(searchQuery)

            // When
            val actualSearchResults = gitHubApiImpl.searchRepositories(repositoryName = searchQuery)

            // Then
            Truth.assertThat(actualSearchResults).isEqualTo(expectedSearchResults)
        }

    private fun createFakeSearchResults(searchQuery: String): SearchResults {
        val searchResultItems = FakeData.allLibraries.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }.mapIndexed { index, library ->
            library.toSearchResultItem(id = index)
        }
        return SearchResults(
            totalCount = searchResultItems.size,
            incompleteResults = false,
            items = searchResultItems
        )
    }

    @Test(expected = Exception::class)
    fun `searchRepositories throws an exception given that there is an exception`() =
        runTest {

            // Given
            FakeEngine.throwException = true

            // When
            gitHubApiImpl.searchRepositories(repositoryName = "re")

            // Then
        }

}