pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
        mavenCentral()
    }

}
rootProject.name = "Circolapp"

include(":backend")
include(":shared")
include(":app")