package ir.fallahpoor.releasetracker.data.fakes

import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import ir.fallahpoor.releasetracker.data.network.LibraryVersion
import ir.fallahpoor.releasetracker.data.network.models.SearchResults
import ir.fallahpoor.releasetracker.data.repository.library.Library
import ir.fallahpoor.releasetracker.data.toSearchResultItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object FakeEngine {

    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val latestReleaseRegex = Regex("/repos/([\\w\\d-]+)/([\\w\\d-]+)/releases/latest")
    private val searchRepositoriesRegex =
        Regex("/search/repositories\\?q=([\\w\\d-]+)\\+in%3Aname&sort=stars&page=\\d+&per_page=\\d+")
    var throwException = false

    val engine = MockEngine { request ->
        if (throwException) {
            throw Exception()
        }
        val fullPath = request.url.fullPath
        if (fullPath.matches(latestReleaseRegex)) {
            handleGetLatestReleaseRequest(fullPath)
        } else if (fullPath.matches(searchRepositoriesRegex)) {
            handleSearchRepositoriesRequest(fullPath)
        } else {
            respondBadRequest()
        }
    }

    private fun MockRequestHandleScope.handleGetLatestReleaseRequest(fullPath: String): HttpResponseData {
        val matchResult: MatchResult? = latestReleaseRegex.find(fullPath)
        val (repositoryOwner, repositoryName) = matchResult!!.destructured
        val library = FakeData.allLibraries.find { library ->
            library.url.contains("$repositoryOwner/$repositoryName", ignoreCase = true)
        }
        return if (library != null) {
            respondOk(convertToJson(library))
        } else {
            error("Library not found")
        }
    }

    private fun convertToJson(library: Library): String = json.encodeToString(
        LibraryVersion(
            name = library.version,
            tagName = ""
        )
    )

    private fun MockRequestHandleScope.respondOk(content: String): HttpResponseData = respond(
        content = ByteReadChannel(content),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    )

    private fun MockRequestHandleScope.handleSearchRepositoriesRequest(fullPath: String): HttpResponseData {
        val matchResult: MatchResult? = searchRepositoriesRegex.find(fullPath)
        val searchQuery = matchResult!!.groupValues[1]
        val libraries = FakeData.allLibraries.filter { library ->
            library.name.contains(searchQuery, ignoreCase = true)
        }
        return respondOk(content = convertToJson(libraries))
    }

    private fun convertToJson(libraries: List<Library>): String {
        val searchResultItems = libraries.mapIndexed { index, library ->
            library.toSearchResultItem(index)
        }
        val searchResults = SearchResults(
            totalCount = searchResultItems.size,
            incompleteResults = false,
            items = searchResultItems
        )
        return json.encodeToString(searchResults)
    }

}