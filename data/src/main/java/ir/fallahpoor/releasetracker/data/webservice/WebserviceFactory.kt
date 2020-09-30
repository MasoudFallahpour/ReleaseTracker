package ir.fallahpoor.releasetracker.data.webservice

import ir.fallahpoor.releasetracker.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebserviceFactory @Inject constructor() {

    companion object {
        private const val GITHUB_BASE_URL = "https://api.github.com/"
        private const val ACCESS_TOKEN = BuildConfig.ACCESS_TOKEN
        private const val HEADER_NAME_AUTHORIZATION = "Authorization"
        private const val HEADER_VALUE_AUTHORIZATION = "token $ACCESS_TOKEN"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(GITHUB_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getOkhttpClient())
        .build()

    private fun getOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HeadersInterceptor())
            .addInterceptor(getLoggingInterceptor())
            .build()
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                setLevel(HttpLoggingInterceptor.Level.NONE)
            }
        }
    }

    fun <S> createGithubService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }

    private inner class HeadersInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {

            val original = chain.request()

            val request = original.newBuilder()
                .header(
                    HEADER_NAME_AUTHORIZATION,
                    HEADER_VALUE_AUTHORIZATION
                )
                .method(original.method, original.body)
                .build()

            return chain.proceed(request)

        }

    }

}