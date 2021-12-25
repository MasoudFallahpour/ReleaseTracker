const val kotlinVersion = "1.5.31"

const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
const val core = "androidx.core:core-ktx:1.7.0"
const val dataStore = "androidx.datastore:datastore-preferences:1.0.0"
const val appCompat = "androidx.appcompat:appcompat:1.3.1"
const val workManager = "androidx.work:work-runtime-ktx:2.7.1"
const val activityCompose = "androidx.activity:activity-compose:1.3.1"
const val navigationCompose =
    "androidx.navigation:navigation-compose:2.4.0-beta02"
const val material = "com.google.android.material:material:1.4.0"
const val timber = "com.jakewharton.timber:timber:5.0.1"
const val accompanistNavigationAnimation =
    "com.google.accompanist:accompanist-navigation-animation:0.20.2"
const val inject = "javax.inject:javax.inject:1"
const val okhttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.0"
const val junit = "junit:junit:4.13.2"
const val truth = "com.google.truth:truth:1.1.3"
const val coreTesting = "androidx.arch.core:core-testing:2.1.0"
const val robolectric = "org.robolectric:robolectric:4.7.3"

object Lifecycle {
    private const val lifecycleVersion = "2.3.1"
    const val liveData =
        "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel:$lifecycleVersion"
}

object Room {
    private const val version = "2.3.0"
    const val runtime = "androidx.room:room-runtime:$version"
    const val ktx = "androidx.room:room-ktx:$version"
    const val compiler = "androidx.room:room-compiler:$version"
}

object Compose {
    const val version = "1.0.5"
    const val ui = "androidx.compose.ui:ui:$version"
    const val tooling = "androidx.compose.ui:ui-tooling:$version"
    const val material = "androidx.compose.material:material:$version"
    const val runtime = "androidx.compose.runtime:runtime:$version"
    const val runtimeLiveData = "androidx.compose.runtime:runtime-livedata:$version"
    const val uiTestJunit = "androidx.compose.ui:ui-test-junit4:$version"
    const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:$version"
}

object Coroutines {
    private const val version = "1.6.0"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
}

object Firebase {
    private const val version = "29.0.1"
    const val bom = "com.google.firebase:firebase-bom:$version"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
}

object Hilt {
    private const val jetpackVersion = "1.0.0"
    private const val navigationComposeVersion = "1.0.0-beta01"
    const val version = "2.38.1"
    const val android = "com.google.dagger:hilt-android:$version"
    const val androidCompiler =
        "com.google.dagger:hilt-android-compiler:$version"
    const val compiler = "androidx.hilt:hilt-compiler:$jetpackVersion"
    const val workManager = "androidx.hilt:hilt-work:$jetpackVersion"
    const val navigationCompose =
        "androidx.hilt:hilt-navigation-compose:$navigationComposeVersion"
    const val androidTesting = "com.google.dagger:hilt-android-testing:${version}"
}

object Retrofit {
    private const val version = "2.9.0"
    const val retrofit = "com.squareup.retrofit2:retrofit:$version"
    const val converterGson = "com.squareup.retrofit2:converter-gson:$version"
}

object AndroidXTest {
    const val core = "androidx.test:core:1.0.0"
    const val runner = "androidx.test:runner:1.1.0"
    const val rules = "androidx.test:rules:1.1.0"
}

object Espresso {
    private const val version = "3.4.0"
    const val core = "androidx.test.espresso:espresso-core:$version"
    const val intents = "androidx.test.espresso:espresso-intents:$version"
}

object Mockito {
    private const val version = "3.2.0"
    const val kotlin = "org.mockito.kotlin:mockito-kotlin:$version"
    const val android = "org.mockito:mockito-android:3.12.4"
}

