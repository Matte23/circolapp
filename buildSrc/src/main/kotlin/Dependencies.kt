object Dependencies {
    object Kotlin {
        const val version = "1.4.10"
        const val core = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${version}"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.2.0"
        const val core = "androidx.core:core-ktx:1.3.2"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.3"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
        const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
        const val preference = "androidx.preference:preference-ktx:1.1.1"

        private const val navigationVersion = "2.3.1"
        const val navigationFragment =
            "androidx.navigation:navigation-fragment-ktx:${navigationVersion}"
        const val navigationUi = "androidx.navigation:navigation-ui-ktx:${navigationVersion}"

        const val workManager = "androidx.work:work-runtime-ktx:2.4.0"

        object Room {
            private const val version = "2.2.5"
            const val roomRuntime = "androidx.room:room-runtime:${version}"
            const val roomKtx = "androidx.room:room-ktx:${version}"
            const val roomCompiler = "androidx.room:room-compiler:${version}"
        }
    }

    object Google {
        const val material = "com.google.android.material:material:1.2.1"
    }

    object Firebase {
        const val bom = "com.google.firebase:firebase-bom:26.0.0"
        const val messaging = "com.google.firebase:firebase-messaging-ktx"
    }

    object Square {
        const val okhttp = "com.squareup.okhttp3:okhttp:4.8.1"

        private const val moshiVersion = "1.9.3"
        const val moshi = "com.squareup.moshi:moshi:${moshiVersion}"
        const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${moshiVersion}"
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