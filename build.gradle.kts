import de.undercouch.gradle.tasks.download.Download
import org.apache.tools.ant.taskdefs.condition.Os
import java.io.FileFilter

plugins {
    kotlin("jvm") version "1.5.20"
}

java {
    toolchain {
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
        compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")

        implementation(kotlin("stdlib"))

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.2")
        testImplementation("org.mockito:mockito-core:3.6.28")
    }
}

tasks {
    val mavenLocal = File("${System.getProperty("user.home")}/.m2/repository/")
    val mcVersions =
        requireNotNull(project.properties["mc_versions"]) { "Not found properties in mc_versions" } as String
    val mcVersionList = mcVersions.split(',').toSortedSet(reverseOrder())

    val buildToolsDir = File(rootDir, ".buildtools")
    val buildToolsJar = File(buildToolsDir, "BuildTools.jar")
    val buildToolsMemory = project.properties["buildtoolsMemory"]?.toString() ?: "1G"

    val downloadBuildToolsTask = register<Download>("downloadBuildTools") {
        src("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar")
        dest(buildToolsJar)
        onlyIfModified(true)
    }

    val spigot = "spigot"
    val spigotRepo = File(mavenLocal, "org/spigotmc/spigot/")
    val spigotRepoVersions = spigotRepo.listFiles(FileFilter { it.isDirectory }) ?: emptyArray()
    val spigotTasks = arrayListOf<TaskProvider<JavaExec>>()

    mcVersionList.forEach { version ->
        val mustRunAfters = spigotTasks.toList()
        spigotTasks.add(register<JavaExec>("spigot-$version") {
            onlyIf {
                spigotRepoVersions.find { it.name.startsWith("$version-") }?.let { repo ->
                    val artifactName = "$spigot-${repo.name}"
                    val jar = File(repo, "$artifactName.jar")
                    val pom = File(repo, "$artifactName.pom")
                    val mojang = File(repo, "$artifactName-remapped-mojang.jar")
                    val obf = File(repo, "$artifactName-remapped-obf.jar")
                    return@onlyIf !(jar.exists() && pom.exists() && mojang.exists() && obf.exists())
                }
                true
            }

            dependsOn(downloadBuildToolsTask)
            mustRunAfter(mustRunAfters)
            workingDir(buildToolsDir)
            mainClass.set("-jar")
            jvmArgs("-Xmx$buildToolsMemory")
            args(buildToolsJar.name, "--rev", version, "--remapped")
        })
    }

    val paperDir = File(rootDir, ".paper")
    val paper = "paper"
    val paperRepo = File(mavenLocal, "io/papermc/paper/$paper")
    val paperRepoVersions = paperRepo.listFiles(FileFilter { it.isDirectory }) ?: emptyArray()
    val paperGitInfos = mapOf(
        "1.17.1" to ("master" to "40b34615b0027b3d059242dcf1e0bbbdd64cd985"),
        "1.17" to ("master" to "a831634d446341efc70f027851effe02a0e7f1d3")
    )
    val paperTasks = arrayListOf<TaskProvider<DefaultTask>>()

    mcVersionList.forEach { version ->
        val mustRunAfters = paperTasks.toList()
        paperTasks.add(register<DefaultTask>("paper-$version") {
            val paperGitInfo = paperGitInfos[version] ?: error("Not found paper commit for $version")
            onlyIf {
                paperRepoVersions.find { it.name.startsWith("$version-") }?.let { repo ->
                    val artifactName = "$paper-${repo.name}"
                    val jar = File(repo, "$artifactName.jar")
                    val pom = File(repo, "$artifactName.pom")
                    val mojang = File(repo, "$artifactName-mojang-mapped.jar")
                    return@onlyIf !(jar.exists() && pom.exists() && mojang.exists())
                }
                true
            }

            mustRunAfter(mustRunAfters)

            doLast {
                fun git(vararg args: String) = exec {
                    workingDir(paperDir)
                    commandLine("git")
                    args(*args)
                }

                fun gradlew(vararg args: String) = exec {
                    workingDir(paperDir)
                    commandLine(if (Os.isFamily(Os.FAMILY_DOS)) "gradlew.bat" else "./gradlew")
                    args(*args)
                }

                git("fetch", "--all")
                git("checkout", paperGitInfo.first)
                git("reset", "--hard", paperGitInfo.second)
                gradlew("applyPatches")
                gradlew("publishToMavenLocal")
                gradlew("clean", "shadowJar")
            }
        })
    }

    val setupSpigot = register<DefaultTask>("setupSpigot") {
        dependsOn(spigotTasks)
    }
    val setupPaper = register<DefaultTask>("setupPaper") {
        mustRunAfter(setupSpigot)
        dependsOn(paperTasks)
    }

    register<DefaultTask>("setupWorkspace") {
        dependsOn(setupSpigot)
        dependsOn(setupPaper)
    }
}