object Dependencies {

    private object Common {
        const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
        const val room = "androidx.room:room-runtime:${Versions.roomVersion}"
        const val core = "androidx.core:core-ktx:1.3.2"
        const val rxkprefs = "com.afollestad.rxkprefs:core:${Versions.kprefs}"
        const val liveData =
            "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycleVersion}"
    }

    private object CommonTest {
        const val junit = "junit:junit:4.13.2"
        const val truth = "com.google.truth:truth:1.1.2"
        const val coreTesting = "androidx.arch.core:core-testing:2.1.0"
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3"
        const val testCore = "androidx.test:core:1.0.0"
        const val robolectric = "org.robolectric:robolectric:4.5.1"
    }

    object App {
        const val kotlinStdLib = Common.kotlinStdLib
        const val appCompat = "androidx.appcompat:appcompat:1.3.0-rc01"
        const val core = Common.core
        const val room = Common.room
        const val liveData = Common.liveData
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifecycleVersion}"
        const val workManager = "androidx.work:work-runtime-ktx:2.5.0"
        const val preference = "androidx.preference:preference-ktx:1.1.0"
        const val activityCompose = "androidx.activity:activity-compose:1.3.0-alpha03"
        const val navigationCompose = "androidx.navigation:navigation-compose:2.4.0-alpha01"
        const val material = "com.google.android.material:material:1.3.0-rc01"
        const val timber = "com.jakewharton.timber:timber:4.7.1"
        const val rxkprefs = Common.rxkprefs

        const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hiltVersion}"
        const val hiltAndroidCompiler =
            "com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}"
        const val hiltCompiler = "androidx.hilt:hilt-compiler:${Versions.hiltJetpackVersion}"
        const val hiltWorkManager = "androidx.hilt:hilt-work:${Versions.hiltJetpackVersion}"
        const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0-alpha02"

        const val firebase = "com.google.firebase:firebase-bom:25.12.0"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"

        const val composeUi = "androidx.compose.ui:ui:${Versions.composeVersion}"
        const val composeTooling = "androidx.compose.ui:ui-tooling:${Versions.composeVersion}"
        const val composeMaterial = "androidx.compose.material:material:${Versions.composeVersion}"
        const val composeRuntime = "androidx.compose.runtime:runtime:${Versions.composeVersion}"
        const val composeRuntimeLiveData =
            "androidx.compose.runtime:runtime-livedata:${Versions.composeVersion}"
    }

    object AppTest {
        const val junit = CommonTest.junit
        const val truth = CommonTest.truth
        const val coreTesting = CommonTest.coreTesting
        const val coroutinesTest = CommonTest.coroutinesTest
        const val testCore = CommonTest.testCore
        const val robolectric = CommonTest.robolectric
        const val uiTestJunit = "androidx.compose.ui:ui-test-junit4:${Versions.composeVersion}"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:${Versions.composeVersion}"
        const val runner = "androidx.test:runner:1.1.0"
        const val rules = "androidx.test:rules:1.1.0"
    }

    object Data {
        const val kotlinStdLib = Common.kotlinStdLib
        const val core = Common.core
        const val liveData = Common.liveData
        const val inject = "javax.inject:javax.inject:1"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3"

        const val room = Common.room
        const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
        const val roomKtx = "androidx.room:room-ktx:${Versions.roomVersion}"

        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}"
        const val retrofitConverterGson =
            "com.squareup.retrofit2:converter-gson:${Versions.retrofitVersion}"
        const val okhttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.0"

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