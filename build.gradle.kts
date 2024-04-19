// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven{
            url = uri("https://maven.pkg.github.com/guilhermehs00/Projeto-Android-Studio-Automacao-Avancada")
            credentials{
                username = "guilhermehs00"
                password = "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"
            }
        }
    }

    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}