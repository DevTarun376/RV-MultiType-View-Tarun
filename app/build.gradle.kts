plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.rv_multitype_view"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.rv_multitype_view"
        minSdk = 29
        targetSdk = 37
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
            // Demo app — keep classes readable for contributors inspecting the APK.
            optimization {
                enable = false
            }
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
//    implementation(project(":recyclerview-multitype-view"))
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    implementation("com.github.DevTarun376:RV-MultiType-View-Tarun:1.1.0")
}