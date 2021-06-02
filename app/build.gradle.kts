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
    compileSdk = Versions.compileSdkVersion

    defaultConfig {
        applicationId = "ir.fallahpoor.releasetracker"
        minSdk = Versions.minSdkVersion
        targetSdk = Versions.targetSdkVersion
        versionCode = Versions.versionCode
        versionName = Versions.versionName
        setProperty("archivesBaseName", "ReleaseTracker")
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
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android.txt"
                ),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
    implementation(Dependencies.App.appCompat)
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
    implementation(Dependencies.App.hiltWorkManager)
    implementation(Dependencies.App.hiltNavigationCompose)
    kapt(Dependencies.App.hiltAndroidCompiler)
    kapt(Dependencies.App.hiltCompiler)

    implementation(Dependencies.App.composeUi)
    implementation(Dependencies.App.composeTooling)
    implementation(Dependencies.App.composeMaterial)
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