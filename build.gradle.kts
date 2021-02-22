buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(GradlePlugins.android)
        classpath(GradlePlugins.kotlin)
        classpath(GradlePlugins.daggerHilt)
        classpath(GradlePlugins.googleServices)
        classpath(GradlePlugins.firebaseCrashlytics)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

task("clean") {
    delete(rootProject.buildDir)
}