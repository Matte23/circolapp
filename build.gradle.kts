// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath(Config.Plugin.android)
        classpath(Config.Plugin.kotlin)
        classpath(Config.Plugin.serialization)
        classpath(Config.Plugin.google)
        classpath(Config.Plugin.sqlDelight)
        classpath(Config.Plugin.ktlint)
        classpath(Config.Plugin.aboutLibraries)
        classpath(Config.Plugin.dependencies)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://jitpack.io")
        }
    }

    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
