plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("android")
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
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android.txt"
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
        useIR = true
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeVersion
    }
}

kapt {
    correctErrorTypes = true
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xallow-jvm-ir-dependencies"
    }
}

dependencies {
    implementation(Dependencies.App.kotlinStdLib)
    implementation(Dependencies.App.core)
    implementation(Dependencies.App.preference)
    implementation(Dependencies.App.appCompat)
    implementation(Dependencies.App.activityCompose)
    implementation(Dependencies.App.navigationFragment)
    implementation(Dependencies.App.navigationUi)
    implementation(Dependencies.App.navigationCompose)
    implementation(Dependencies.App.material)
    implementation(Dependencies.App.viewModel)
    implementation(Dependencies.App.liveDataKtx)
    implementation(Dependencies.App.hilt)
    kapt(Dependencies.App.hiltCompiler)
    implementation(Dependencies.App.hiltViewModel)
    implementation(Dependencies.App.hiltWorkManager)
    kapt(Dependencies.App.hiltJetpackCompiler)
    implementation(Dependencies.App.room)
    implementation(Dependencies.App.workManager)
    implementation(Dependencies.App.timber)
    implementation(platform(Dependencies.App.firebase))
    implementation(Dependencies.App.crashlytics)
    implementation(Dependencies.App.rxkprefs)
    implementation(Dependencies.App.composeUi)
    implementation(Dependencies.App.composeTooling)
    implementation(Dependencies.App.composeMaterial)
    implementation(Dependencies.App.composeMaterialIcons)
    implementation(Dependencies.App.composeRuntime)
    implementation(Dependencies.App.composeRuntimeLiveData)
    implementation(project(":data"))
}