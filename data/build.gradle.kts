import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

val accessToken: String = gradleLocalProperties(rootDir).getProperty("accessToken")

android {
    compileSdkVersion(Versions.compileSdkVersion)
    buildToolsVersion(Versions.buildToolsVersion)
    defaultConfig {
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
        versionCode = Versions.versionCode
        versionName = Versions.versionName
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
                    "proguard-android-optimize.txt"
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

dependencies {
    implementation(Dependencies.Data.kotlinStdLib)
    implementation(Dependencies.Data.room)
    kapt(Dependencies.Data.roomCompiler)
    implementation(Dependencies.Data.roomKtx)
    implementation(Dependencies.Data.inject)
    implementation(Dependencies.Data.retrofit)
    implementation(Dependencies.Data.retrofitConverterGson)
    implementation(Dependencies.Data.okhttpLoggingInterceptor)
}