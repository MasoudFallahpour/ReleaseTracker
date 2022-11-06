package ir.fallahpoor.releasetracker.data.network

import com.google.common.truth.Truth
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import ir.fallahpoor.releasetracker.data.fakes.FakeData
import ir.fallahpoor.releasetracker.data.fakes.FakeKtorEngine
import ir.fallahpoor.releasetracker.data.network.models.LatestReleaseDto
import ir.fallahpoor.releasetracker.data.network.models.SearchRepositoriesResultsDto
import ir.fallahpoor.releasetracker.data.toSearchRepositoriesResultDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubApiImplTest {

    private val httpClient = HttpClient(FakeKtorEngine.engine) {
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
            FakeKtorEngine.throwException = false

            // When
            val latestReleaseDto: LatestReleaseDto = gitHubApiImpl.getLatestRelease(
                owner = FakeData.ReleaseTracker.OWNER,
                repository = FakeData.ReleaseTracker.REPOSITORY_NAME
            )

            // Then
            Truth.assertThat(latestReleaseDto.name).isEqualTo(FakeData.ReleaseTracker.VERSION)

        }

    @Test(expected = Exception::class)
    fun `getLatestRelease throws an exception given that the library does not exist`() =
        runTest {

            // Given
            FakeKtorEngine.throwException = true

            // When
            gitHubApiImpl.getLatestRelease(
                owner = "someNonExistentOwner",
                repository = "someNonExistentRepository"
            )

            // Then we expect to receive an exception

        }

    @Test
    fun `searchRepositories returns the search results given that there is no exception`() =
        runTest {

            // Given
            FakeKtorEngine.throwException = false
            val searchQuery = "CO"
            val expectedSearchResults = createFakeSearchResults(searchQuery)

            // When
            val actualSearchResults = gitHubApiImpl.searchRepositories(repositoryName = searchQuery)

            // Then
            Truth.assertThat(actualSearchResults).isEqualTo(expectedSearchResults)

        }

    private fun createFakeSearchResults(searchQuery: String): SearchRepositoriesResultsDto {
        val items = FakeData.allLibraries.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }.mapIndexed { index, library ->
            library.toSearchRepositoriesResultDto(id = index)
        }
        return SearchRepositoriesResultsDto(
            totalCount = items.size,
            incompleteResults = false,
            items = items
        )
    }

    @Test(expected = Exception::class)
    fun `searchRepositories throws an exception given that there is an exception`() =
        runTest {

            // Given
            FakeKtorEngine.throwException = true

            // When
            gitHubApiImpl.searchRepositories(repositoryName = "re")

            // Then we expect to receive an exception

        }

}