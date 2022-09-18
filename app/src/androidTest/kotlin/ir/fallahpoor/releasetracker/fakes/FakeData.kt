package ir.fallahpoor.releasetracker.fakes

import ir.fallahpoor.releasetracker.common.GITHUB_BASE_URL
import ir.fallahpoor.releasetracker.data.repository.library.Library

object FakeData {
    object Coil {
        const val name = "Coil"
        const val url = GITHUB_BASE_URL + "coil-kt/coil"
        const val version = "1.3.1"
        val library = Library(name = name, url = url, version = version, isPinned = false)
    }

    object Coroutines {
        const val name = "Coroutines"
        const val url = GITHUB_BASE_URL + "kotlinx/Coroutines"
        const val version = "1.7.0"
        val library = Library(name = name, url = url, version = version, isPinned = true)
    }

    object Eks {
        const val name = "Eks"
        const val url = GITHUB_BASE_URL + "MasoudFallahpour/Eks"
        const val version = "1.0"
        val library = Library(name = name, url = url, version = version, isPinned = true)
    }

    object Koin {
        const val name = "Koin"
        const val url = GITHUB_BASE_URL + "InsertKoinIO/koin"
        const val version = "3.1.2"
        val library = Library(name = name, url = url, version = version, isPinned = true)
    }

    object Kotlin {
        const val name = "Kotlin"
        const val url = GITHUB_BASE_URL + "JetBrains/kotlin"
        const val version = "1.5.21"
        val library = Library(name = name, url = url, version = version, isPinned = false)
    }

    object Timber {
        const val name = "Timber"
        const val url = GITHUB_BASE_URL + "JakeWharton/Timber"
        const val version = "5.0.1"
        val library = Library(name = name, url = url, version = version, isPinned = true)
    }

    object ReleaseTracker {
        const val name = "ReleaseTracker"
        const val url = GITHUB_BASE_URL + "MasoudFallahpour/ReleaseTracker"
        const val version = "1.1"
        val library = Library(name = name, url = url, version = version, isPinned = true)
    }
}