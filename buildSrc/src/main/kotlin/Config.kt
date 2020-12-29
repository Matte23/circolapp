object Config {
    object Plugin {
        const val android = "com.android.tools.build:gradle:4.1.1"
        const val kotlin =
            "org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.Kotlin.version}"
        const val serialization =
            "org.jetbrains.kotlin:kotlin-serialization:${Dependencies.Kotlin.version}"
        const val google = "com.google.gms:google-services:4.3.4"
        const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:9.4.1"
        const val sqlDelight =
            "com.squareup.sqldelight:gradle-plugin:${Dependencies.SQLDelight.version}"
        const val aboutLibraries =
            "com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:${Dependencies.AboutLibraries.version}"
        const val dependencies =
            "com.github.ben-manes:gradle-versions-plugin:0.36.0"
        const val shadow =
            "com.github.jengelman.gradle.plugins:shadow:5.2.0"
    }

    object Android {
        const val compileSdk = 30
        const val buildToolsVersion = "30.0.2"

        const val minSdk = 21
        const val targetSdk = 30
    }
}