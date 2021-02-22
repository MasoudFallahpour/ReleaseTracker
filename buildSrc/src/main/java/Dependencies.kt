object Dependencies {

    private object Common {
        const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
        const val room = "androidx.room:room-runtime:${Versions.roomVersion}"
        const val core = "androidx.core:core-ktx:1.3.2"
        const val rxkprefs = "com.afollestad.rxkprefs:core:${Versions.kprefs}"
    }

    object App {
        const val kotlinStdLib = Common.kotlinStdLib
        const val core = Common.core
        const val preference = "androidx.preference:preference-ktx:1.1.0"
        const val appCompat = "androidx.appcompat:appcompat:1.2.0"
        const val navigationFragment =
            "androidx.navigation:navigation-fragment-ktx:${Versions.navigationVersion}"
        const val navigationUi =
            "androidx.navigation:navigation-ui-ktx:${Versions.navigationVersion}"
        const val material = "com.google.android.material:material:1.3.0-rc01"
        const val hilt = "com.google.dagger:hilt-android:${Versions.hiltVersion}"
        const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}"
        const val hiltViewModel =
            "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.hiltJetpackVersion}"
        const val hiltJetpackCompiler = "androidx.hilt:hilt-compiler:${Versions.hiltJetpackVersion}"
        const val hiltWorkManager = "androidx.hilt:hilt-work:${Versions.hiltJetpackVersion}"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifeCycleVersion}"
        const val liveDataKtx =
            "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifeCycleVersion}"
        const val room = Common.room
        const val workManager = "androidx.work:work-runtime-ktx:2.4.0"
        const val timber = "com.jakewharton.timber:timber:4.7.1"
        const val firebase = "com.google.firebase:firebase-bom:25.12.0"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
        const val rxkprefs = Common.rxkprefs
        const val composeUi = "androidx.compose.ui:ui:${Versions.composeVersion}"
        const val composeTooling = "androidx.compose.ui:ui-tooling:${Versions.composeVersion}"
        const val composeMaterial = "androidx.compose.material:material:${Versions.composeVersion}"
        const val composeMaterialIcons =
            "androidx.compose.material:material-icons-extended:${Versions.composeVersion}"
        const val composeRuntime = "androidx.compose.runtime:runtime:${Versions.composeVersion}"
        const val composeRuntimeLiveData =
            "androidx.compose.runtime:runtime-livedata:${Versions.composeVersion}"
    }

    object Data {
        const val kotlinStdLib = Common.kotlinStdLib
        const val core = Common.core
        const val room = Common.room
        const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
        const val roomKtx = "androidx.room:room-ktx:${Versions.roomVersion}"
        const val inject = "javax.inject:javax.inject:1"
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}"
        const val retrofitConverterGson =
            "com.squareup.retrofit2:converter-gson:${Versions.retrofitVersion}"
        const val okhttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.0"
        const val liveData =
            "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifeCycleVersion}"
        const val rxkprefs = Common.rxkprefs
        const val rxkprefsCoroutines = "com.afollestad.rxkprefs:coroutines:${Versions.kprefs}"
    }

}