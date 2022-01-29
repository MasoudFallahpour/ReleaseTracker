import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

val accessToken: String = gradleLocalProperties(rootDir).getProperty("accessToken")

android {

    compileSdk = SdkVersions.compileSdk

    defaultConfig {
        minSdk = SdkVersions.minSdk
        targetSdk = SdkVersions.targetSdk
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
    implementation(kotlinStdLib)
    implementation(core)
    implementation(dataStore)
    implementation(Lifecycle.liveData)
    implementation(Coroutines.android)

    implementation(Room.runtime)
    kapt(Room.compiler)
    implementation(Room.ktx)

    implementation(inject)

    implementation(Retrofit.retrofit)
    implementation(Retrofit.converterGson)
    implementation(okhttpLoggingInterceptor)

    testImplementation(junit)
    testImplementation(truth)
    testImplementation(Coroutines.test)
    testImplementation(coreTesting)
    testImplementation(AndroidXTest.core)
    testImplementation(robolectric)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}