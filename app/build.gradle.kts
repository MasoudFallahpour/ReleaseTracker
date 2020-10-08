plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(Versions.compileSdkVersion)
    buildToolsVersion(Versions.buildToolsVersion)
    defaultConfig {
        applicationId = "ir.fallahpoor.releasetracker"
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
        versionCode = Versions.versionCode
        versionName = Versions.versionName
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                    getDefaultProguardFile(
                            "proguard-android-optimize.txt"
                    ),
                    "proguard-rules.pro"
            )

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

kapt {
    correctErrorTypes = true

}

dependencies {
    implementation(Dependencies.App.kotlinStdLib)
    implementation(Dependencies.App.core)
    implementation(Dependencies.App.preference)
    implementation(Dependencies.App.appCompat)
    implementation(Dependencies.App.constraintLayout)
    implementation(Dependencies.App.coordinatorLayout)
    implementation(Dependencies.App.navigationFragment)
    implementation(Dependencies.App.navigationUi)
    implementation(Dependencies.App.material)
    implementation(Dependencies.App.viewModel)
    implementation(Dependencies.App.hilt)
    kapt(Dependencies.App.hiltCompiler)
    implementation(Dependencies.App.hiltViewModel)
    implementation(Dependencies.App.hiltWorkManager)
    kapt(Dependencies.App.hiltJetpackCompiler)
    implementation(Dependencies.App.room)
    implementation(Dependencies.App.materialProgressBar)
    implementation(Dependencies.App.workManager)
    implementation(Dependencies.App.timber)
    implementation(platform(Dependencies.App.firebase))
    implementation(Dependencies.App.crashlytics)
    implementation(project(":data"))
}