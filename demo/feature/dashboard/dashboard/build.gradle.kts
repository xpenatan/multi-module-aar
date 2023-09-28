plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.xpenatan.aarmodule.demo.dashboard"

    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":demo:feature:base"))
    implementation(project(":demo:lib:components"))

    implementation(project(":demo:standaloneAARlib"))
    implementation(project(":demo:standalonelib"))
}