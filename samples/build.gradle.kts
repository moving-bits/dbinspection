plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "net.movingbits.testapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "net.movingbits.testapplication"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
//    implementation(libs.constraintlayout)
//    implementation(libs.navigation.fragment)
//    implementation(libs.navigation.ui)
    implementation (libs.commons.lang3)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)

    implementation(project(":DBInspection"));
}