plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.vyatsuapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.vyatsuapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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

    configurations.all {
        resolutionStrategy {
            force ("com.android.support:support-v4:27.1.0")
        }}
    buildToolsVersion = "34.0.0"
}

dependencies {
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("org.jsoup:jsoup:1.17.2")
    implementation ("com.github.skydoves:powerspinner:1.2.7")
    implementation("com.github.leandroborgesferreira:loading-button-android:2.3.0")
    implementation ("com.github.StevenDXC:DxLoadingButton:2.4")
}

