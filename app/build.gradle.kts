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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeVersion
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(Dependencies.App.kotlinStdLib)
    implementation(Dependencies.App.core)
    implementation(Dependencies.App.preference)
    implementation(Dependencies.App.activityCompose)
    implementation(Dependencies.App.navigationCompose)
    implementation(Dependencies.App.material)
    implementation(Dependencies.App.viewModel)
    implementation(Dependencies.App.liveData)
    implementation(Dependencies.App.room)
    implementation(Dependencies.App.workManager)
    implementation(Dependencies.App.timber)
    implementation(Dependencies.App.rxkprefs)

    implementation(platform(Dependencies.App.firebase))
    implementation(Dependencies.App.crashlytics)

    implementation(Dependencies.App.hiltAndroid)
    implementation(Dependencies.App.hiltNavigation)
    implementation(Dependencies.App.hiltWorkManager)
    kapt(Dependencies.App.hiltAndroidCompiler)
    kapt(Dependencies.App.hiltCompiler)

    implementation(Dependencies.App.composeUi)
    implementation(Dependencies.App.composeTooling)
    implementation(Dependencies.App.composeMaterial)
    implementation(Dependencies.App.composeMaterialIcons)
    implementation(Dependencies.App.composeRuntime)
    implementation(Dependencies.App.composeRuntimeLiveData)

    testImplementation(Dependencies.AppTest.junit)
    testImplementation(Dependencies.AppTest.truth)
    testImplementation(Dependencies.AppTest.coreTesting)
    testImplementation(Dependencies.AppTest.coroutinesTest)
    testImplementation(Dependencies.AppTest.testCore)
    testImplementation(Dependencies.AppTest.robolectric)

    implementation(project(":data"))
}