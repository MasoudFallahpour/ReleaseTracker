object Dependencies {

    private const val kotlinVersion = "1.4.10"
    private const val navigationVersion = "2.3.0"
    private const val hiltVersion = "2.28-alpha"
    private const val hiltJetpackVersion = "1.0.0-alpha01"
    private const val roomVersion = "2.2.5"
    private const val retrofitVersion = "2.9.0"
    private const val lifeCycleVersion = "2.2.0"
    private const val kprefs = "2.0.3"

    private object Common {
        const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
        const val room = "androidx.room:room-runtime:$roomVersion"
        const val core = "androidx.core:core-ktx:1.3.1"
        const val rxkprefs = "com.afollestad.rxkprefs:core:$kprefs"
    }

    object App {
        const val kotlinStdLib = Common.kotlinStdLib
        const val core = Common.core
        const val preference = "androidx.preference:preference-ktx:1.1.0"
        const val appCompat = "androidx.appcompat:appcompat:1.2.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.1"
        const val coordinatorLayout = "androidx.coordinatorlayout:coordinatorlayout:1.1.0"
        const val navigationFragment =
            "androidx.navigation:navigation-fragment-ktx:$navigationVersion"
        const val navigationUi = "androidx.navigation:navigation-ui-ktx:$navigationVersion"
        const val material = "com.google.android.material:material:1.3.0-alpha01"
        const val hilt = "com.google.dagger:hilt-android:$hiltVersion"
        const val hiltCompiler = "com.google.dagger:hilt-android-compiler:$hiltVersion"
        const val hiltViewModel = "androidx.hilt:hilt-lifecycle-viewmodel:$hiltJetpackVersion"
        const val hiltJetpackCompiler = "androidx.hilt:hilt-compiler:$hiltJetpackVersion"
        const val hiltWorkManager = "androidx.hilt:hilt-work:$hiltJetpackVersion"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel:$lifeCycleVersion"
        const val room = Common.room
        const val materialProgressBar = "me.zhanghai.android.materialprogressbar:library:1.6.1"
        const val workManager = "androidx.work:work-runtime-ktx:2.4.0"
        const val timber = "com.jakewharton.timber:timber:4.7.1"
        const val firebase = "com.google.firebase:firebase-bom:25.12.0"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
        const val recyclerViewSelection = "androidx.recyclerview:recyclerview-selection:1.0.0"
        const val rxkprefs = Common.rxkprefs
    }

    object Data {
        const val kotlinStdLib = Common.kotlinStdLib
        const val core = Common.core
        const val room = Common.room
        const val roomCompiler = "androidx.room:room-compiler:$roomVersion"
        const val roomKtx = "androidx.room:room-ktx:$roomVersion"
        const val inject = "javax.inject:javax.inject:1"
        const val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
        const val retrofitConverterGson = "com.squareup.retrofit2:converter-gson:$retrofitVersion"
        const val okhttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.0"
        const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:$lifeCycleVersion"
        const val rxkprefs = Common.rxkprefs
        const val rxkprefsCoroutines = "com.afollestad.rxkprefs:coroutines:$kprefs"
    }

}