import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}



android {
    namespace = "com.example.movieapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.movieapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = false

            val localProperties = Properties().apply {
                val localPropertiesFile = rootProject.file("local.properties")
                if (localPropertiesFile.exists()) {
                    localPropertiesFile.inputStream().use { load(it) }
                }
            }

            val tmdbApiKey = localProperties.getProperty("TMDB_API_KEY", "")
            val newsApiKey = localProperties.getProperty("NEWS_API_KEY", "")

            buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
            buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")
        }
        debug {
            val localProperties = Properties().apply {
                val localPropertiesFile = rootProject.file("local.properties")
                if (localPropertiesFile.exists()) {
                    localPropertiesFile.inputStream().use { load(it) }
                }
            }

            val tmdbApiKey = localProperties.getProperty("TMDB_API_KEY", "")
            val newsApiKey = localProperties.getProperty("NEWS_API_KEY", "")

            buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
            buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")

        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Library untuk membaca JSON (Gson)
    implementation("com.google.code.gson:gson:2.10.1")

    // Lifecycle & ViewModel KTX untuk Coroutines (viewModelScope)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")


    // Jetpack Compose BOM (Bill of Materials) untuk mengatur versi otomatis
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // UI Compose Dasar (Foundation, Layout, Material3)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")

    // Lifecycle & ViewModel untuk Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.0")

    //navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    //Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended")

    //Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    //Coil
    implementation("io.coil-kt:coil-compose:2.7.0")

    //Data Store
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // YouTube Player Component for Android
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    // Retrofit & OkHttp untuk Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Installing Koin
    implementation("io.insert-koin:koin-android:3.5.3")
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")

    //installing Koil
    implementation("io.coil-kt:coil-compose:2.6.0")

    //Custom Tabs
    implementation("androidx.browser:browser:1.8.0")
}