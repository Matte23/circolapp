object Dependencies {
    object Kotlin {
        const val version = "1.4.20"
        const val core = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${version}"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9-native-mt"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.2.0"
        const val core = "androidx.core:core-ktx:1.3.2"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.3"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
        const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
        const val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0"
        const val preference = "androidx.preference:preference-ktx:1.1.1"

        private const val navigationVersion = "2.3.1"
        const val navigationFragment =
            "androidx.navigation:navigation-fragment-ktx:${navigationVersion}"
        const val navigationUi = "androidx.navigation:navigation-ui-ktx:${navigationVersion}"

        const val workManager = "androidx.work:work-runtime-ktx:2.4.0"
    }

    object Google {
        const val material = "com.google.android.material:material:1.2.1"
    }

    object Firebase {
        const val bom = "com.google.firebase:firebase-bom:26.0.0"
        const val messaging = "com.google.firebase:firebase-messaging-ktx"
    }

    object Ktor {
        private const val version = "1.4.2"
        const val ktorCore = "io.ktor:ktor-client-core:$version"
        const val ktorOkhttp = "io.ktor:ktor-client-okhttp:$version"
        const val ktorIos = "io.ktor:ktor-client-ios:$version"
        const val ktorJson = "io.ktor:ktor-client-json:$version"
        const val ktorSerialization = "io.ktor:ktor-client-serialization:$version"
    }

    object Serialization {
        const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1"
    }

    object SQLDelight {
        const val version = "1.4.4"
        const val sqlDelightRuntime = "com.squareup.sqldelight:runtime:$version"
        const val sqlDelightCoroutines = "com.squareup.sqldelight:coroutines-extensions:$version"
        const val sqlDelightAndroid = "com.squareup.sqldelight:android-driver:$version"
        const val sqlDelightNative = "com.squareup.sqldelight:native-driver:$version"
    }

    object AboutLibraries {
        const val version = "8.3.0"
        const val aboutLibrariesCore = "com.mikepenz:aboutlibraries-core:$version"
        const val aboutLibraries = "com.mikepenz:aboutlibraries:$version"
    }

    object Misc {
        const val jsoup = "org.jsoup:jsoup:1.13.1"
        const val appIntro = "com.github.AppIntro:AppIntro:6.0.0"
        const val materialSpinner = "com.github.tiper:MaterialSpinner:1.4.2"
    }

    object Testing {
        const val junit = "junit:junit:4.13"
        const val testRunner = "androidx.test:runner:1.3.0"
        const val espresso = "androidx.test.espresso:espresso-core:3.3.0"
    }
}