import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("android")
    kotlin("kapt")
}

val properties: java.util.Properties = gradleLocalProperties(rootDir)
val sp: String = properties.getProperty("storePassword")
val kp: String = properties.getProperty("keyPassword")

android {
    compileSdk = SdkVersions.compileSdk

    defaultConfig {
        applicationId = "ir.fallahpoor.releasetracker"
        minSdk = SdkVersions.minSdk
        targetSdk = SdkVersions.targetSdk
        versionCode = AppVersion.versionCode
        versionName = AppVersion.versionName
        setProperty("archivesBaseName", "ReleaseTracker")
        testInstrumentationRunner = "ir.fallahpoor.releasetracker.CustomTestRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("../SigningKey.jks")
            storePassword = sp
            keyAlias = "android app signing certificate"
            keyPassword = kp
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android.txt"
                ),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs["release"]
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
        kotlinCompilerExtensionVersion = Dependencies.Compose.version
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        resources.excludes.add("**/attach_hotspot_windows.dll")
        resources.excludes.add("META-INF/licenses/ASM")
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
    }

    testOptions {
        animationsDisabled = true

        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

kapt {
    correctErrorTypes = true
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

dependencies {
    implementation(Dependencies.kotlinStdLib)
    implementation(Dependencies.appCompat)
    implementation(Dependencies.core)
    implementation(Dependencies.dataStore)
    implementation(Dependencies.activityCompose)
    implementation(Dependencies.navigationCompose)
    implementation(Dependencies.material)
    implementation(Dependencies.Lifecycle.viewModel)
    implementation(Dependencies.Lifecycle.liveData)
    implementation(Dependencies.Room.runtime)
    implementation(Dependencies.WorkManager.runtime)
    implementation(Dependencies.timber)

    implementation(platform(Dependencies.Firebase.bom))
    implementation(Dependencies.Firebase.crashlytics)

    implementation(Dependencies.Hilt.android)
    implementation(Dependencies.Hilt.workManager)
    implementation(Dependencies.Hilt.navigationCompose)
    kapt(Dependencies.Hilt.androidCompiler)
    kapt(Dependencies.Hilt.compiler)

    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.tooling)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.runtime)
    implementation(Dependencies.Compose.runtimeLiveData)

    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.truth)
    testImplementation(Dependencies.coreTesting)
    testImplementation(Dependencies.Coroutines.test)
    testImplementation(Dependencies.AndroidXTest.core)
    testImplementation(Dependencies.robolectric)
    testImplementation(Dependencies.Hilt.androidTesting)
    kaptTest(Dependencies.Hilt.androidCompiler)
    testImplementation(Dependencies.Mockito.kotlin)

    androidTestImplementation(Dependencies.AndroidXTest.runner)
    androidTestImplementation(Dependencies.AndroidXTest.rules)
    androidTestImplementation(Dependencies.truth)
    androidTestImplementation(Dependencies.Compose.uiTestJunit)
    debugImplementation(Dependencies.Compose.uiTestManifest)
    androidTestImplementation(Dependencies.Mockito.kotlin)
    androidTestImplementation(Dependencies.Mockito.android)
    androidTestImplementation(Dependencies.coreTesting)
    androidTestImplementation(Dependencies.Espresso.core)
    androidTestImplementation(Dependencies.Espresso.intents)
    androidTestImplementation(Dependencies.Hilt.androidTesting)
    kaptAndroidTest(Dependencies.Hilt.androidCompiler)
    androidTestImplementation(Dependencies.Coroutines.test)

    implementation(project(":data"))
}