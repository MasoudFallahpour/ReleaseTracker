object Dependencies {

    private object Common {
        const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
        const val room = "androidx.room:room-runtime:${Versions.room}"
        const val core = "androidx.core:core-ktx:${Versions.core}"
        const val rxkprefs = "com.afollestad.rxkprefs:core:${Versions.kprefs}"
        const val liveData =
            "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
        const val preference = "androidx.preference:preference-ktx:${Versions.preference}"
    }

    private object CommonTest {
        const val junit = "junit:junit:${Versions.junit}"
        const val truth = "com.google.truth:truth:${Versions.truth}"
        const val coreTesting = "androidx.arch.core:core-testing:${Versions.coreTesting}"
        const val coroutinesTest =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
        const val testCore = "androidx.test:core:${Versions.testCore}"
        const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    }

    object App {
        const val kotlinStdLib = Common.kotlinStdLib
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
        const val core = Common.core
        const val room = Common.room
        const val liveData = Common.liveData
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifecycle}"
        const val workManager = "androidx.work:work-runtime-ktx:${Versions.workManager}"
        const val preference = Common.preference
        const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
        const val navigationCompose =
            "androidx.navigation:navigation-compose:${Versions.navigationCompose}"
        const val material = "com.google.android.material:material:${Versions.material}"
        const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
        const val rxkprefs = Common.rxkprefs

        const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
        const val hiltAndroidCompiler =
            "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
        const val hiltCompiler = "androidx.hilt:hilt-compiler:${Versions.hiltJetpack}"
        const val hiltWorkManager = "androidx.hilt:hilt-work:${Versions.hiltJetpack}"
        const val hiltNavigationCompose =
            "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigationCompose}"

        const val firebase = "com.google.firebase:firebase-bom:${Versions.firebase}"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"

        const val composeUi = "androidx.compose.ui:ui:${Versions.compose}"
        const val composeTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
        const val composeMaterial = "androidx.compose.material:material:${Versions.compose}"
        const val composeRuntime = "androidx.compose.runtime:runtime:${Versions.compose}"
        const val composeRuntimeLiveData =
            "androidx.compose.runtime:runtime-livedata:${Versions.compose}"
    }

    object AppTest {
        const val junit = CommonTest.junit
        const val truth = CommonTest.truth
        const val coreTesting = CommonTest.coreTesting
        const val coroutinesTest = CommonTest.coroutinesTest
        const val testCore = CommonTest.testCore
        const val robolectric = CommonTest.robolectric
        const val uiTestJunit = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:${Versions.compose}"
        const val runner = "androidx.test:runner:${Versions.testRunner}"
        const val rules = "androidx.test:rules:${Versions.testRules}"
        const val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}"
        const val mockitoAndroid = "org.mockito:mockito-android:${Versions.mockitoKotlin}"
        const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
        const val hiltAndroidTesting = "com.google.dagger:hilt-android-testing:${Versions.hilt}"
    }

    object Data {
        const val kotlinStdLib = Common.kotlinStdLib
        const val core = Common.core
        const val preference = Common.preference
        const val liveData = Common.liveData
        const val inject = "javax.inject:javax.inject:${Versions.inject}"
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

        const val room = Common.room
        const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
        const val roomKtx = "androidx.room:room-ktx:${Versions.room}"

        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val retrofitConverterGson =
            "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
        const val okhttpLoggingInterceptor =
            "com.squareup.okhttp3:logging-interceptor:${Versions.okhttpLoggingInterceptor}"

        const val rxkprefs = Common.rxkprefs
        const val rxkprefsCoroutines = "com.afollestad.rxkprefs:coroutines:${Versions.kprefs}"
    }

    object DataTest {
        const val junit = CommonTest.junit
        const val truth = CommonTest.truth
        const val coroutinesTest = CommonTest.coroutinesTest
        const val coreTesting = CommonTest.coreTesting
        const val testCore = CommonTest.testCore
        const val robolectric = CommonTest.robolectric
    }

}