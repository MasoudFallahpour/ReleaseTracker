import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("android")
    kotlin("kapt")
    id("jacoco")
}

jacoco {
    toolVersion = Versions.jacoco
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register("jacocoTestReport", JacocoReport::class) {

    dependsOn("testDebugUnitTest", "createDebugCoverageReport")

    reports {
        html.isEnabled = true
        xml.isEnabled = false
    }

    val basePath = "ir/fallahpoor/releasetracker"
    val excludedFiles = listOf(
        // App-specific
        "$basePath/di/**",
        "$basePath/libraries/view/states/**",
        "$basePath/theme/**",
        // Jetpack Compose
        "**/ComposableSingletons*",
        "**/LiveLiterals*",

        "**/*lambda*",
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*\$[0-9].*"
    )
    val debugTree = fileTree(
        "dir" to "$buildDir/tmp/kotlin-classes/debug",
        "excludes" to excludedFiles
    )
    val mainSrc = "$projectDir/src/main/kotlin"

    sourceDirectories.setFrom(files(listOf(mainSrc)))
    classDirectories.setFrom(files(listOf(debugTree)))
    executionData.from(
        fileTree(
            "dir" to buildDir,
            "includes" to listOf(
                "jacoco/testDebugUnitTest.exec",
                "outputs/code_coverage/debugAndroidTest/connected/**/*.ec"
            )
        )
    )

}

val properties: java.util.Properties = gradleLocalProperties(rootDir)
val sp: String = properties.getProperty("storePassword")
val kp: String = properties.getProperty("keyPassword")

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "ir.fallahpoor.releasetracker"
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = Versions.versionCode
        versionName = Versions.versionName
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
            isTestCoverageEnabled = true
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
        kotlinCompilerExtensionVersion = Versions.compose
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets.getByName("test").kotlin {
        srcDir("src/sharedTest/kotlin")
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

    androidTestImplementation(Dependencies.AppTest.runner)
    androidTestImplementation(Dependencies.AppTest.rules)
    androidTestImplementation(Dependencies.AppTest.truth)
    androidTestImplementation(Dependencies.AppTest.uiTestJunit)
    androidTestImplementation(Dependencies.AppTest.mockitoAndroid)
    androidTestImplementation(Dependencies.AppTest.mockitoKotlin)
    androidTestImplementation(Dependencies.AppTest.coreTesting)
    androidTestImplementation(Dependencies.AppTest.espresso)
    debugImplementation(Dependencies.AppTest.uiTestManifest)

    implementation(project(":data"))
}