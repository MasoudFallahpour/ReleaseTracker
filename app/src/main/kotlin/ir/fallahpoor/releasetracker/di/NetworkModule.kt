package ir.fallahpoor.releasetracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import ir.fallahpoor.releasetracker.BuildConfig
import ir.fallahpoor.releasetracker.data.exceptions.InternetNotConnectedException
import ir.fallahpoor.releasetracker.data.exceptions.LibraryDoesNotExistException
import ir.fallahpoor.releasetracker.data.exceptions.UnknownException
import ir.fallahpoor.releasetracker.data.network.GitHubApi
import ir.fallahpoor.releasetracker.data.network.GitHubApiImpl
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideGithubWebService(gitHubApiImpl: GitHubApiImpl): GitHubApi = gitHubApiImpl

    @Provides
    @Singleton
    fun provideHttpClient() =
        HttpClient {
            expectSuccess = true
            install(DefaultRequest) {
                url("https://api.github.com/")
                header(HttpHeaders.Authorization, "token ${BuildConfig.ACCESS_TOKEN}")
            }
            install(Logging) {
                level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                })
            }
            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, _ ->
                    when (exception) {
                        is ClientRequestException -> throw LibraryDoesNotExistException()
                        is IOException -> throw InternetNotConnectedException()
                        else -> throw UnknownException()
                    }
                }
            }
        }

}