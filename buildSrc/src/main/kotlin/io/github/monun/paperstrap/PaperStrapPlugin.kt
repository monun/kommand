package io.github.monun.paperstrap

import de.undercouch.gradle.tasks.download.Download
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.task
import java.io.File

abstract class PaperStrapExtension {
    internal val versions = linkedSetOf<String>()

    fun include(vararg versions: String) {
        this.versions.addAll(versions)
    }

    fun include(versions: Iterable<String>) {
        this.versions.addAll(versions)
    }
}

class PaperStrapPlugin : Plugin<Project> {
    private lateinit var paperstrap: PaperStrapExtension

    override fun apply(project: Project) {
        paperstrap = project.extensions.create("paperstrap")

        project.afterEvaluate {
            whenComplete(this)
        }
    }

    private fun whenComplete(project: Project) {
        val paperstrapDir = File(project.rootDir, ".paperstrap")
        val paperDir = File(paperstrapDir, "Paper")
        val spigotDir = File(paperstrapDir, "Spigot")
        val buildToolsJar = File(spigotDir, "BuildTools.jar")

        fun File.git(vararg args: String) {
            mkdirs()
            project.exec {
                workingDir = this@git
                commandLine("git")
                args(*args)
            }
        }

        fun File.gradle(vararg args: String) {
            mkdirs()
            project.exec {
                workingDir = this@gradle
                commandLine(if (Os.isFamily(Os.FAMILY_DOS)) "gradlew.bat" else "./gradlew")
                args(*args)
            }
        }

        fun File.java(vararg args: String) {
            mkdirs()
            project.javaexec {
                workingDir = this@java
                mainClass.set("-jar")
                args(*args)
            }
        }

        val clonePaper = project.task("setupPaperClone") {
            doLast {
                paperDir.git("clone", "https://github.com/PaperMC/Paper.git", ".")
            }
        }

        val downloadBuildTools = project.task<Download>("downloadBuildTools") {
            src("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar")
            dest(buildToolsJar)
            onlyIfModified(true)
        }

        val cleanPaperStrap = project.task("cleanPaperStrap") {
            doLast {
                paperstrapDir.deleteRecursively()
            }
        }

        val papers = arrayListOf<Task>()
        val spigots = arrayListOf<Task>()

        for (version in paperstrap.versions) {
            papers += project.task("setupPaper-$version") {
                dependsOn(clonePaper)

                doLast {
                    val commit = PaperAPI.commit(version, PaperAPI.latestBuild(version))

                    paperDir.git("fetch", "--all", "--quiet")
                    paperDir.git("checkout", commit, "--quiet")
                    paperDir.gradle("applyPatches")
                    paperDir.gradle("publishToMavenLocal")
                    paperDir.gradle("clean")
                }

                finalizedBy(cleanPaperStrap)
            }

            spigots += project.task("setupSpigot$version") {
                dependsOn(downloadBuildTools)

                doLast {
                    println(buildToolsJar.name)
                    spigotDir.java(buildToolsJar.name, "--rev", version)
                }

                finalizedBy(cleanPaperStrap)
            }
        }

        project.task("setupPaperAll") {
            dependsOn(papers)
        }

        project.task("setupSpigotAll") {
            dependsOn(spigots)
        }

        project.task("setupDependencies") {
            dependsOn(papers)
            dependsOn(spigots)
        }
    }
}

fun Project.paperstrap(configure: Action<PaperStrapExtension>): Unit =
    extensions.configure("paperstrap", configure)