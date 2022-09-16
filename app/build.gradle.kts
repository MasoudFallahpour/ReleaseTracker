import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    // TODO re-enable Crashlytics after resolving the build error
//    id("com.google.gms.google-services")
//    id("com.google.firebase.crashlytics")
    kotlin("android")
    kotlin("kapt")
}

val properties: java.util.Properties = gradleLocalProperties(rootDir)
val sp: String = properties.getProperty("storePassword")
val kp: String = properties.getProperty("keyPassword")
val accessToken: String = properties.getProperty("accessToken")

android {
    namespace = "ir.fallahpoor.releasetracker"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ir.fallahpoor.releasetracker"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 5
        versionName = "1.1"
        setProperty("archivesBaseName", "ReleaseTracker")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            buildConfigField("String", "ACCESS_TOKEN", "\"$accessToken\"")
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
        debug {
            buildConfigField("String", "ACCESS_TOKEN", "\"$accessToken\"")
            isMinifyEnabled = false
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
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
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
    implementation(libs.kotlin.stdlib)
    implementation(libs.appcompat)
    implementation(libs.core)
    implementation(libs.datastore.preferences)
    implementation(libs.activityCompose)
    implementation(libs.navigationCompose)
    implementation(libs.material)
    implementation(libs.lifecycle.viewModel)
    implementation(libs.lifecycle.liveData)
    implementation(libs.workManagar.runtime)
    implementation(libs.timber)
    implementation(libs.sqlDelight.driver)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    implementation(libs.hilt.android)
    implementation(libs.hilt.workManager)
    implementation(libs.hilt.navigationCompose)
    kapt(libs.hilt.androidCompiler)
    kapt(libs.hilt.compiler)

    implementation(libs.compose.ui)
    debugImplementation(libs.compose.tooling)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.compose.material)
    implementation(libs.compose.runtime)

    implementation(libs.bundles.ktor)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.coreTesting)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.androidxTest.core)
    testImplementation(libs.robolectric)
    kaptTest(libs.hilt.androidCompiler)
    testImplementation(libs.mockito.inline)

    androidTestImplementation(libs.androidxTest.runner)
    androidTestImplementation(libs.androidxTest.rules)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.compose.uiTestJunit)
    debugImplementation(libs.compose.uiTestManifest)
    androidTestImplementation(libs.dexMaker)
    androidTestImplementation(libs.coreTesting)
    androidTestImplementation(libs.bundles.espresso)
    kaptAndroidTest(libs.hilt.androidCompiler)
    androidTestImplementation(libs.coroutines.test)

    implementation(project(":data"))
}