import io.github.monun.paperstrap.paperstrap

plugins {
    kotlin("jvm") version "1.5.21"
    id("io.github.monun.paperstrap") //buildSrc
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

paperstrap {
    File(rootDir, "${rootProject.name}-core").listFiles { file ->
        file.isDirectory && file.name.startsWith("v")
    }?.map { it.name.removePrefix("v") }?.forEach { version ->
        include(version)
    }
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("net.md-5:SpecialSource:1.10.0")
    }
}

allprojects {
    repositories {
        mavenCentral()
    }

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "16"
            }
        }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")

        implementation(kotlin("stdlib"))

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.2")
        testImplementation("org.mockito:mockito-core:3.6.28")
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }
}

project(":${rootProject.name}-core") {
    configurations {
        create("mojangMapping")
        create("spigotMapping")
    }
}
