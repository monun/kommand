import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    kotlin("jvm") version "1.5.20"
    id("org.jetbrains.dokka") version "1.4.32"
    `maven-publish`
    signing
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
        languageVersion.set(JavaLanguageVersion.of(8))
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
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")

        implementation(kotlin("stdlib"))

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
        testImplementation("org.mockito:mockito-core:3.6.28")
    }
}

tasks {
    register<DefaultTask>("setupDebugServer") {
        dependsOn(":debugJar")
        doLast {
            fun runProcess(directory: File, vararg command: String) {
                val process = ProcessBuilder(*command).directory(directory).start()
                val buffer = ByteArray(1000)
                while (process.isAlive) {
                    if (process.inputStream.available() > 0) {
                        val count = process.inputStream.read(buffer)
                        System.out.write(buffer, 0, count)
                    }
                    if (process.errorStream.available() > 0) {
                        val count = process.errorStream.read(buffer)
                        System.err.write(buffer, 0, count)
                    }
                    Thread.sleep(1)
                }
                System.out.writeBytes(process.inputStream.readBytes())
                System.err.writeBytes(process.errorStream.readBytes())

                process.waitFor()
            }

            fun runGitProcess(directory: File, vararg command: String) {
                runProcess(directory, "git", "-c", "commit.gpgsign=false", "-c", "core.safecrlf=false", *command)
            }

            val projectDir = layout.projectDirectory.asFile
            val debugDir = File(projectDir, ".debug")
            val paperDir = File(projectDir, ".paper")
            val buildDir = File(paperDir, "Paper-Server/build/libs")
            val gradle = if (Os.isFamily(Os.FAMILY_WINDOWS)) "gradlew.bat" else "gradlew"

            var shouldUpdate = false

            if (paperDir.listFiles()?.isEmpty() != false) {
                shouldUpdate = true
                runGitProcess(projectDir, "submodule", "update", "--init")
            }

            if (project.hasProperty("updatePaper")) {
                shouldUpdate = true
                runGitProcess(paperDir, "fetch", "--all")
                runGitProcess(paperDir, "reset", "--hard", "\"origin/master\"")
            }

            if (shouldUpdate) {
                runProcess(paperDir, File(paperDir, gradle).absolutePath, "applyPatches")
                runProcess(paperDir, File(paperDir, gradle).absolutePath, "shadowJar")

                buildDir.listFiles()?.forEach { file ->
                    println("Copying ${file.name} into .debug")
                    file.copyTo(File(debugDir, file.name), true)
                }
            }
        }
    }
}