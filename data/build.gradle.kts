import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.6.10"
    id("com.squareup.sqldelight")
}

val accessToken: String = gradleLocalProperties(rootDir).getProperty("accessToken")

android {
    namespace = "ir.fallahpoor.releasetracker.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    buildTypes {
        release {
            buildConfigField("String", "ACCESS_TOKEN", "\"$accessToken\"")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android.txt"
                ),
                "proguard-rules.pro"
            )
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

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.core)
    implementation(libs.datastore.preferences)
    implementation(libs.lifecycle.liveData)
    implementation(libs.coroutines.android)
    implementation(libs.inject)
    implementation(libs.bundles.ktor)
    implementation(libs.sqlDelight.driver)
    implementation(libs.sqlDelight.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.coreTesting)
    testImplementation(libs.androidxTest.core)
    testImplementation(libs.robolectric)
    testImplementation(libs.ktor.mock)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}