plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    //  KSP
    id("com.google.devtools.ksp")

    //  Dagger and Hilt
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")

    //  Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.lab_ass_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lab_ass_app"
        minSdk = 27
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //  QR code scanner
    implementation("com.google.mlkit:barcode-scanning:17.1.0")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.2.0")

    //  Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    //  Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-android-compiler:2.48")
    ksp("androidx.hilt:hilt-compiler:1.0.0")

    //  Activity KTX for viewModels()
    implementation("androidx.activity:activity-ktx:1.8.0")

    //  SSP and SDP
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    //  Justify TextView
    implementation("com.uncopt:android.justified:1.0")

    //  Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))

    //  Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")

    //  Firebase FireStore
    implementation("com.google.firebase:firebase-firestore-ktx")

    //  Firebase Cloud Storage
    implementation("com.google.firebase:firebase-storage-ktx")

    //  Google Play services authentication library
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    //  Facebook SDK
    implementation("com.facebook.android:facebook-login:latest.release")

    //  Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    //  Firebase UI for Glide use
    implementation("com.firebaseui:firebase-ui-storage:8.0.2")

    //  Splash screen
    implementation("androidx.core:core-splashscreen:1.1.0-alpha02")
}