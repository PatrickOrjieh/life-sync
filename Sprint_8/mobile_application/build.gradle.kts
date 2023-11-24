// Top-level build file where you can add configuration options common to all sub-projects/modules.
//Parts of code taken from: https://github.com/mitchtabian/Google-Maps-Compose
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.42")
    }
}

plugins {
    id("com.android.application") version "8.1.1" apply false
    id ("com.android.library") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}