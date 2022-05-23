buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath(libs.androidPlugin)
        classpath(libs.kotlinPlugin)
        classpath(libs.daggerPlugin)
        classpath(libs.googleServicePlugin)
        classpath(libs.firebaseCrashlyticsPlugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}