plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

version = "1.0"

kotlin {
    android()
    jvm()
    ios ()

    cocoapods {
        summary = "Shared module for Circolapp"
        homepage = "Link to a Kotlin/Native module homepage"

        frameworkName = "Shared"

        pod("HTMLKit", "~> 3.1.0")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // KotlinX
                implementation(Dependencies.Kotlin.coroutinesCore) {
                    version {
                        strictly("1.5.2-native-mt")
                    }
                }
                implementation(Dependencies.Kotlin.serializationJson)

                // Ktor
                implementation(Dependencies.Ktor.ktorCore)
                implementation(Dependencies.Ktor.ktorJson)
                implementation(Dependencies.Ktor.ktorSerialization)

                // SqlDelight
                implementation(Dependencies.SQLDelight.sqlDelightRuntime)
                implementation(Dependencies.SQLDelight.sqlDelightCoroutines)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Dependencies.Kotlin.coroutinesAndroid)

                // Ktor
                implementation(Dependencies.Ktor.ktorOkhttp)

                // SqlDelight
                implementation(Dependencies.SQLDelight.sqlDelightAndroid)

                // Misc
                implementation(Dependencies.Misc.jsoup)
            }
        }
        val iosMain by getting {
            dependencies {
                // Ktor
                implementation(Dependencies.Ktor.ktorIos)

                // SqlDelight
                implementation(Dependencies.SQLDelight.sqlDelightNative)
            }
        }
        val jvmMain by getting {
            dependencies {
                // Ktor
                implementation(Dependencies.Ktor.ktorOkhttp)
                implementation(Dependencies.Ktor.slf4j)

                // SqlDelight
                implementation(Dependencies.SQLDelight.sqlDelightSQLite)

                // Misc
                implementation(Dependencies.Misc.jsoup)
            }
        }
    }
}

android {
    compileSdkVersion(Config.Android.compileSdk)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(Config.Android.minSdk)
        targetSdkVersion(Config.Android.targetSdk)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

sqldelight {
    database("AppDatabase") {
        packageName = "net.underdesk.circolapp.shared.data"
        sourceFolders = listOf("sqldelight")
    }
}
