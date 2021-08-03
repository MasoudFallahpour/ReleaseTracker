import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

val accessToken: String = gradleLocalProperties(rootDir).getProperty("accessToken")

android {

    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        getByName("release") {
            buildConfigField("String", "ACCESS_TOKEN", "\"$accessToken\"")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android.txt"
                ),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
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
    implementation(Dependencies.Data.kotlinStdLib)
    implementation(Dependencies.Data.core)
    implementation(Dependencies.Data.liveData)
    implementation(Dependencies.Data.coroutinesAndroid)

    implementation(Dependencies.Data.room)
    kapt(Dependencies.Data.roomCompiler)
    implementation(Dependencies.Data.roomKtx)

    implementation(Dependencies.Data.inject)

    implementation(Dependencies.Data.retrofit)
    implementation(Dependencies.Data.retrofitConverterGson)
    implementation(Dependencies.Data.okhttpLoggingInterceptor)

    implementation(Dependencies.Data.rxkprefs)
    implementation(Dependencies.Data.rxkprefsCoroutines)

    testImplementation(Dependencies.DataTest.junit)
    testImplementation(Dependencies.DataTest.truth)
    testImplementation(Dependencies.DataTest.coroutinesTest)
    testImplementation(Dependencies.DataTest.coreTesting)
    testImplementation(Dependencies.DataTest.testCore)
    testImplementation(Dependencies.DataTest.robolectric)
}