object Config {
    object Plugin {
        const val android = "com.android.tools.build:gradle:4.1.0"
        const val kotlin =
            "org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.Kotlin.version}"
        const val google = "com.google.gms:google-services:4.3.4"
        const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:9.4.0"
        const val aboutLibraries =
            "com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:${Dependencies.AboutLibraries.version}"
    }

    object Android {
        const val compileSdk = 30
        const val buildToolsVersion = "30.0.2"

        const val minSdk = 21
        const val targetSdk = 30
    }
}