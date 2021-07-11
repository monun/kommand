import java.io.FileFilter
import de.undercouch.gradle.tasks.download.Download

plugins {
    kotlin("jvm") version "1.5.20"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        languageVersion.set(JavaLanguageVersion.of(16))
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
    // create setup task
    val buildToolsDir = File(rootDir, ".buildtools")
    val buildToolsJar = File(buildToolsDir, "BuildTools.jar")
    val buildToolsMemory = "1G"
    val versions = sortedSetOf(reverseOrder(),
        "1.17.1",
        "1.17",
        "1.16.5",
        "1.16.4",
        "1.16.3",
        "1.16.2",
        "1.16.1",
        "1.15.2",
        "1.15.1",
        "1.14.4",
        "1.14.3",
        "1.14.2",
        "1.14.1",
        "1.13.2",
        "1.13.1",
        "1.13"
    )
    val home = System.getProperty("user.home")
    val spigot = "spigot"
    val mavenLocal = File("$home/.m2/repository/org/spigotmc/$spigot")
    val spigotLocalRepos = mavenLocal.listFiles(FileFilter { it.isDirectory }) ?: emptyArray()

    val downloadBuildTools = register<Download>("downloadBuildTools") {
        src("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar")
        dest(buildToolsJar)
        onlyIfModified(true)
    }

    val buildToolsTasks = arrayListOf<TaskProvider<JavaExec>>()
    versions.forEach { version ->
        val mustRunAfters = buildToolsTasks.toList()
        buildToolsTasks.add(register<JavaExec>("buildtools-$version") {
            onlyIf {
                spigotLocalRepos.find { it.name.startsWith("$version-R") }?.let { repo ->
                    val artifactName = "$spigot-${repo.name}"
                    val jar = File(repo, "$artifactName.jar")
                    val pom = File(repo, "$artifactName.pom")
                    return@onlyIf !(jar.exists() && pom.exists())
                }
                true
            }

            dependsOn(downloadBuildTools)
            mustRunAfter(mustRunAfters)
            javaLauncher.set(project.javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(if (version < "1.17") 8 else 16))
            })
            workingDir(buildToolsDir)
            mainClass.set("-jar")
            jvmArgs("-Xmx$buildToolsMemory")
            args(buildToolsJar.name, "--rev", version, "--remapped")
        })
    }

    register<DefaultTask>("buildtools") {
        dependsOn(buildToolsTasks)
    }

    val cleanBuildTools = register<DefaultTask>("cleanBuildTools") {
        doLast {
            buildToolsDir.deleteRecursively()
        }
    }

    clean {
        finalizedBy(cleanBuildTools)
    }
}