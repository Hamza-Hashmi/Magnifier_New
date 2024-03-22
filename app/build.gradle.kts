plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.magnifier_new"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.magnifier_new"
        minSdk = 24
        targetSdk = 34
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

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")



    // CameraX core library
    implementation ("androidx.camera:camera-core:1.4.0-alpha04")
    // CameraX Camera2 extensions
    implementation ("androidx.camera:camera-camera2:1.4.0-alpha04")
    // CameraX Lifecycle library
    implementation ("androidx.camera:camera-lifecycle:1.4.0-alpha04")
    // CameraX View class
    implementation ("androidx.camera:camera-view:1.4.0-alpha04")

    // Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")



    implementation ("com.intuit.ssp:ssp-android:1.1.0")
    implementation ("com.intuit.sdp:sdp-android:1.1.0")

    implementation ("io.insert-koin:koin-android:3.1.5")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")


    //Gson
    implementation ("com.google.code.gson:gson:2.9.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

//    implementation ("com.google.android.gms:play-services-ads:22.6.0")

    //permission handling
    implementation("com.guolindev.permissionx:permissionx:1.7.1")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.6.0"))

}