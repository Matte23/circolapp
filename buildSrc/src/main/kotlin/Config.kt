/*
 * Circolapp
 * Copyright (C) 2019-2021  Matteo Schiff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

object Config {
    object Plugin {
        const val android = "com.android.tools.build:gradle:4.2.2"
        const val kotlin =
            "org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.Kotlin.version}"
        const val serialization =
            "org.jetbrains.kotlin:kotlin-serialization:${Dependencies.Kotlin.version}"
        const val google = "com.google.gms:google-services:4.3.5"
        const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:10.0.0"
        const val sqlDelight =
            "com.squareup.sqldelight:gradle-plugin:${Dependencies.SQLDelight.version}"
        const val aboutLibraries =
            "com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:${Dependencies.AboutLibraries.version}"
        const val dependencies =
            "com.github.ben-manes:gradle-versions-plugin:0.39.0"
        const val shadow =
            "com.github.jengelman.gradle.plugins:shadow:6.1.0"
    }

    object Android {
        const val compileSdk = 30
        const val buildToolsVersion = "30.0.3"

        const val minSdk = 21
        const val targetSdk = 30
    }
}