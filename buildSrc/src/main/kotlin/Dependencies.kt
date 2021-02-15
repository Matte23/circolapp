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

object Dependencies {
    object Kotlin {
        const val version = "1.4.30"
        const val core = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${version}"
        const val serializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2-native-mt"
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2-native-mt"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.2.0"
        const val core = "androidx.core:core-ktx:1.3.2"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
        const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
        const val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:2.3.0"
        const val preference = "androidx.preference:preference-ktx:1.1.1"
        const val browser = "androidx.browser:browser:1.3.0"

        private const val navigationVersion = "2.3.3"
        const val navigationFragment =
            "androidx.navigation:navigation-fragment-ktx:${navigationVersion}"
        const val navigationUi = "androidx.navigation:navigation-ui-ktx:${navigationVersion}"

        const val workManager = "androidx.work:work-runtime-ktx:2.5.0"
    }

    object Google {
        const val material = "com.google.android.material:material:1.3.0"
    }

    object Firebase {
        const val bom = "com.google.firebase:firebase-bom:26.5.0"
        const val messaging = "com.google.firebase:firebase-messaging-ktx"

        const val adminSDK = "com.google.firebase:firebase-admin:7.1.0"
    }

    object Ktor {
        private const val version = "1.5.1"
        const val ktorCore = "io.ktor:ktor-client-core:$version"
        const val ktorOkhttp = "io.ktor:ktor-client-okhttp:$version"
        const val ktorIos = "io.ktor:ktor-client-ios:$version"
        const val ktorJson = "io.ktor:ktor-client-json:$version"
        const val ktorSerialization = "io.ktor:ktor-client-serialization:$version"

        const val slf4j = "org.slf4j:slf4j-simple:1.7.30"
    }

    object SQLDelight {
        const val version = "1.4.4"
        const val sqlDelightRuntime = "com.squareup.sqldelight:runtime:$version"
        const val sqlDelightCoroutines = "com.squareup.sqldelight:coroutines-extensions:$version"
        const val sqlDelightAndroid = "com.squareup.sqldelight:android-driver:$version"
        const val sqlDelightNative = "com.squareup.sqldelight:native-driver:$version"
        const val sqlDelightSQLite = "com.squareup.sqldelight:sqlite-driver:$version"
    }

    object AboutLibraries {
        const val version = "8.8.2"
        const val aboutLibrariesCore = "com.mikepenz:aboutlibraries-core:$version"
        const val aboutLibraries = "com.mikepenz:aboutlibraries:$version"
    }

    object Misc {
        const val jsoup = "org.jsoup:jsoup:1.13.1"
        const val appIntro = "com.github.AppIntro:AppIntro:6.1.0"
        const val materialSpinner = "com.github.tiper:MaterialSpinner:1.4.2"
        const val materialProgressBar = "me.zhanghai.android.materialprogressbar:library:1.6.1"
    }

    object Testing {
        const val junit = "junit:junit:4.13.2"
        const val testRunner = "androidx.test:runner:1.3.0"
        const val espresso = "androidx.test.espresso:espresso-core:3.3.0"
    }
}