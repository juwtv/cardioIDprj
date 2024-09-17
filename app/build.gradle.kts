plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.testescomunicacao"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.testescomunicacao"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(project(":shared"))

    // Biblioteca cardioid
    implementation(project(":cardioid_ble-release"))

    // viewmodel
    val lifecycle_version = "2.8.4"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // cardioid:
    //Swipe refresh
    implementation(libs.androidx.swiperefreshlayout)
    // Rxjava, RxAndroidBle
    implementation("io.reactivex:rxjava:1.3.6")
    implementation("com.polidea.rxandroidble:rxandroidble:1.4.3")
    // graph github
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // cardioid: v3.0.3
    // Movesense
    //implementation files('mdslib-1.39.0(1)-release.aar')
    implementation(files("mdslib-1.39.0(1)-release.aar"))
    //Gson
    implementation("com.google.code.gson:gson:2.10.1") // cardioid: 2.9.0
    // Volley
    implementation("com.android.volley:volley:1.2.1")
    // lib mds
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("com.polidea.rxandroidble2:rxandroidble:1.14.1")
    implementation("androidx.cardview:cardview:1.0.0")

    /* https://developer.android.com/jetpack/androidx/releases/lifecycle
     * ---> The APIs in lifecycle-extensions have been deprecated. Instead,
     * add dependencies for the specific Lifecycle artifacts you need.
    implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0' */
}