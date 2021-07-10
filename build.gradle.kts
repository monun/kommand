import java.io.FileFilter

plugins {
    kotlin("jvm") version "1.5.20"
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("net.md-5:SpecialSource:1.10.0")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")

        implementation(kotlin("stdlib"))

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.2")
        testImplementation("org.mockito:mockito-core:3.6.28")
    }
}

tasks {
    register<Task>("setupWorkspace") {
        // File: $HOME/.gradle/gradle.properties
        // for DOS
        // java8="C:/Program Files/Zulu/zulu-8/bin/java.exe"
        // java16="C:/Program Files/Zulu/zulu-16/bin/java.exe"

        val buildToolsDir = File(rootDir, ".buildtools").also { it.mkdirs() }
        val buildToolsJar = File(buildToolsDir, "BuildTools.jar").also { jar ->
            buildToolsDir.listFiles()?.let { files ->
                files.filter { it != jar }.forEach { it.deleteRecursively() }
            }
        }
        val memory = "1G"
        val versions = linkedSetOf(
            "1.17"
        )

        val home = System.getProperty("user.home")
        val spigot = "spigot"
        val mavenLocal = File("$home/.m2/repository/org/spigotmc/$spigot")
        val repos = mavenLocal.listFiles(FileFilter { it.isDirectory }) ?: emptyArray()
        versions.removeIf { version ->
            repos.find { it.name.startsWith("$version-R") }?.let { repo ->
                val artifactName = "$spigot-${repo.name}"
                val jar = File(repo, "$artifactName.jar")
                val pom = File(repo, "$artifactName.pom")

                return@removeIf (jar.exists() && pom.exists()).also {
                    if (it) println("Skip download version $version")
                }
            }
            false
        }

        val download by registering(de.undercouch.gradle.tasks.download.Download::class) {
            src("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar")
            dest(buildToolsJar)
            onlyIfModified(true)
        }
        download.get().download()

        fun java(name: String): String {
            return project.properties[name]?.toString()
                ?: throw NullPointerException("Please define $name in gradle.properties.")
        }

        versions.forEach { version ->
            logger.info("Download version $version")
            val java = if (version < "1.17") java("java8") else java("java16")
            logger.info("Use java $java")

            runCatching {
                exec {
                    workingDir(buildToolsDir)
                    commandLine(java, "-Xmx$memory", "-jar", buildToolsJar.name, "--rev", version, "--remapped")
                }
            }.onFailure {
                logger.warn("Failed to download version $version")
                throw it
            }
        }
    }
}