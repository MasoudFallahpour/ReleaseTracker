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
    toolVersion = jacocoVersion
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
        debug {
//            isTestCoverageEnabled = true
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
        kotlinCompilerExtensionVersion = Compose.version
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets.getByName("test").kotlin {
        srcDir("src/sharedTest/kotlin")
    }
    sourceSets.getByName("androidTest").kotlin {
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
    implementation(kotlinStdLib)
    implementation(appCompat)
    implementation(core)
    implementation(preference)
    implementation(activityCompose)
    implementation(navigationCompose)
    implementation(material)
    implementation(Lifecycle.viewModel)
    implementation(Lifecycle.liveData)
    implementation(Room.runtime)
    implementation(workManager)
    implementation(timber)
    implementation(RxkPrefs.core)

    implementation(platform(Firebase.bom))
    implementation(Firebase.crashlytics)

    implementation(Hilt.android)
    implementation(Hilt.workManager)
    implementation(Hilt.navigationCompose)
    kapt(Hilt.androidCompiler)
    kapt(Hilt.compiler)

    implementation(Compose.ui)
    implementation(Compose.tooling)
    implementation(Compose.material)
    implementation(Compose.runtime)
    implementation(Compose.runtimeLiveData)

    implementation(accompanistNavigationAnimation)

    testImplementation(junit)
    testImplementation(truth)
    testImplementation(coreTesting)
    testImplementation(Coroutines.test)
    testImplementation(AndroidXTest.core)
    testImplementation(robolectric)

    androidTestImplementation(AndroidXTest.runner)
    androidTestImplementation(AndroidXTest.rules)
    androidTestImplementation(truth)
    androidTestImplementation(Compose.uiTestJunit)
    debugImplementation(Compose.uiTestManifest)
    androidTestImplementation(Mockito.kotlin)
    androidTestImplementation(Mockito.android)
    androidTestImplementation(coreTesting)
    androidTestImplementation(Espresso.core)
    androidTestImplementation(Espresso.intents)
    androidTestImplementation(Hilt.androidTesting)
    kaptAndroidTest(Hilt.androidCompiler)

    implementation(project(":data"))
}