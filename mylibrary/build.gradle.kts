plugins {
    alias(libs.plugins.androidLibrary)
    id ("maven-publish")
}

android {
    namespace = "com.guilherme.mylibrary"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

publishing {
    publications {
        create<MavenPublication>("minhapublication") {
            groupId = "com.guilherme"
            artifactId = "bibliotecacalculosautavancada"
            version = "1.3"
            artifact("build/outputs/aar/mylibrary-release.aar")
        }
    }
    repositories{
        maven{
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/guilhermehs00/Projeto-Android-Studio-Automacao-Avancada")
            credentials{
                username = "guilhermehs00"
                password = "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"
            }
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    implementation ("com.google.code.gson:gson:2.10.1")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}