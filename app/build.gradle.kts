plugins {
    id("com.android.application")
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

dependencies {
    implementation(Dependencies.App.kotlinStdLib)
    implementation(Dependencies.App.core)
    implementation(Dependencies.App.appCompat)
    implementation(Dependencies.App.constraintLayout)
    implementation(Dependencies.App.coordinatorLayout)
    implementation(Dependencies.App.navigationFragment)
    implementation(Dependencies.App.navigationUi)
}