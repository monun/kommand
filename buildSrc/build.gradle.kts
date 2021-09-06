plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.5.21"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("commons-cli:commons-cli:1.3.1")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("de.undercouch:gradle-download-task:4.1.2")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "16"
        }
    }
}
