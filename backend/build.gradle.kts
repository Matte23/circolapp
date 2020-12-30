plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow")
}

group = "net.underdesk"
version = "0.0.1"

dependencies {
    implementation(project(":shared"))
    implementation(Dependencies.Kotlin.core)
    implementation(Dependencies.Kotlin.coroutinesCore)

    implementation(Dependencies.Ktor.ktorCore)

    implementation(Dependencies.Firebase.adminSDK)
}

application {
    mainClass.set("net.underdesk.circolapp.backend.ServerKt")
    mainClassName = "net.underdesk.circolapp.backend.ServerKt"
}
