package ir.fallahpoor.releasetracker.data.fakes

import ir.fallahpoor.releasetracker.data.repository.library.Library

object FakeData {

    private const val GITHUB_BASE_URL = "https://github.com/"
    val allLibraries = listOf(
        Coil.library,
        Coroutines.library,
        Eks.library,
        Koin.library,
        Kotlin.library,
        Timber.library,
        ReleaseTracker.library
    )

    object Coil {
        const val NAME = "Coil"
        const val VERSION = "1.3.1"
        const val OWNER = "coil-kt"
        const val REPOSITORY_NAME = "coil"
        const val URL = "$GITHUB_BASE_URL${OWNER}/${REPOSITORY_NAME}"
        val library = Library(name = NAME, url = URL, version = VERSION, isPinned = false)
    }

    object Coroutines {
        const val NAME = "Coroutines"
        const val VERSION = "1.7.0"
        const val OWNER = "kotlinx"
        const val REPOSITORY_NAME = "Coroutines"
        const val URL = "$GITHUB_BASE_URL$OWNER/$REPOSITORY_NAME"
        val library = Library(name = NAME, url = URL, version = VERSION, isPinned = true)
    }

    object Eks {
        const val NAME = "Eks"
        const val VERSION = "1.0"
        const val OWNER = "MasoudFallahpour"
        const val REPOSITORY_NAME = "Eks"
        const val URL = "$GITHUB_BASE_URL$OWNER/$REPOSITORY_NAME"
        val library = Library(name = NAME, url = URL, version = VERSION, isPinned = true)
    }

    object Koin {
        const val NAME = "Koin"
        const val VERSION = "3.1.2"
        const val OWNER = "InsertKoinIO"
        const val REPOSITORY_NAME = "koin"
        const val URL = "$GITHUB_BASE_URL$OWNER/$REPOSITORY_NAME"
        val library = Library(name = NAME, url = URL, version = VERSION, isPinned = true)
    }

    object Kotlin {
        const val NAME = "Kotlin"
        const val VERSION = "1.5.21"
        const val OWNER = "JetBrains"
        const val REPOSITORY_NAME = "kotlin"
        const val URL = "$GITHUB_BASE_URL$OWNER/$REPOSITORY_NAME"
        val library = Library(name = NAME, url = URL, version = VERSION, isPinned = false)
    }

    object Timber {
        const val NAME = "Timber"
        const val VERSION = "5.0.1"
        const val OWNER = "JakeWharton"
        const val REPOSITORY_NAME = "Timber"
        const val URL = "$GITHUB_BASE_URL$OWNER/$REPOSITORY_NAME"
        val library = Library(name = NAME, url = URL, version = VERSION, isPinned = true)
    }

    object ReleaseTracker {
        const val NAME = "ReleaseTracker"
        const val VERSION = "1.1"
        const val OWNER = "MasoudFallahpour"
        const val REPOSITORY_NAME = "ReleaseTracker"
        const val URL = "$GITHUB_BASE_URL$OWNER/$REPOSITORY_NAME"
        val library = Library(name = NAME, url = URL, version = VERSION, isPinned = true)
    }

}