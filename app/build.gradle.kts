plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    compileSdkVersion(Config.Android.compileSdk)
    buildToolsVersion = Config.Android.buildToolsVersion

    defaultConfig {
        applicationId = "net.underdesk.circolapp"

        minSdkVersion(Config.Android.minSdk)
        targetSdkVersion(Config.Android.targetSdk)

        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = System.getenv("VERSION_NAME") ?: "LOCAL"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        named("release") {
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
    androidExtensions {
        isExperimental = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(Dependencies.Kotlin.core)

    // AndroidX
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.constraintLayout)
    implementation(Dependencies.AndroidX.swipeRefreshLayout)
    implementation(Dependencies.AndroidX.lifecycleExtensions)
    implementation(Dependencies.AndroidX.preference)
    implementation(Dependencies.AndroidX.navigationFragment)
    implementation(Dependencies.AndroidX.navigationUi)
    implementation(Dependencies.AndroidX.workManager)
    implementation(Dependencies.AndroidX.Room.roomRuntime)
    implementation(Dependencies.AndroidX.Room.roomKtx)
    kapt(Dependencies.AndroidX.Room.roomCompiler)

    // Google
    implementation(Dependencies.Google.material)

    //Firebase
    implementation(platform(Dependencies.Firebase.bom))
    implementation(Dependencies.Firebase.messaging)

    // Square
    implementation(Dependencies.Square.okhttp)
    implementation(Dependencies.Square.moshi)
    kapt(Dependencies.Square.moshiCodegen)

    // AboutLibraries
    implementation(Dependencies.AboutLibraries.aboutLibrariesCore)
    implementation(Dependencies.AboutLibraries.aboutLibraries)

    // Misc
    implementation(Dependencies.Misc.jsoup)
    implementation(Dependencies.Misc.appIntro)
    implementation(Dependencies.Misc.materialSpinner)

    // Testing
    testImplementation(Dependencies.Testing.junit)
    androidTestImplementation(Dependencies.Testing.espresso)
    androidTestImplementation(Dependencies.Testing.testRunner)
}