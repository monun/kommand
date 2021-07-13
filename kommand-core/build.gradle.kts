import net.md_5.specialsource.JarMapping
import net.md_5.specialsource.JarRemapper
import net.md_5.specialsource.provider.JarProvider
import net.md_5.specialsource.provider.JointProvider
import org.gradle.api.tasks.bundling.Jar
import net.md_5.specialsource.Jar as SpecialJar

plugins {
    id("org.jetbrains.dokka") version "1.5.0"
    `maven-publish`
    signing
}

dependencies {
    implementation(project(":kommand-api"))
}

subprojects {
    configurations {
        create("mojangMapping")
        create("spigotMapping")
    }

    repositories {
        maven("https://libraries.minecraft.net")
        mavenLocal()
    }

    dependencies {
        implementation(project(":kommand-api"))
        implementation(requireNotNull(parent)) // kommand-core
    }

    tasks {
        jar {
            doLast {
                fun remap(jarFile: File, outputFile: File, mappingFile: File, reversed: Boolean = false) {
                    val inputJar = SpecialJar.init(jarFile)

                    val mapping = JarMapping()
                    mapping.loadMappings(mappingFile.canonicalPath, reversed, false, null, null)

                    val provider = JointProvider()
                    provider.add(JarProvider(inputJar))
                    mapping.setFallbackInheritanceProvider(provider)

                    val mapper = JarRemapper(mapping)
                    mapper.remapJar(inputJar, outputFile)
                    inputJar.close()
                }

                val archiveFile = archiveFile.get().asFile
                val obfOutput = File(archiveFile.parentFile, "remapped-obf.jar")
                val spigotOutput = File(archiveFile.parentFile, "remapped-spigot.jar")

                val configurations = project.configurations
                val mojangMapping = configurations.named("mojangMapping").get().firstOrNull()
                val spigotMapping = configurations.named("spigotMapping").get().firstOrNull()

                if (mojangMapping != null && spigotMapping != null) {
                    remap(archiveFile, obfOutput, mojangMapping, true)
                    remap(obfOutput, spigotOutput, spigotMapping)

                    spigotOutput.copyTo(archiveFile, true)
                    obfOutput.delete()
                    spigotOutput.delete()
                } else {
                    throw IllegalStateException("Mojang and Spigot mapping should be specified for ${project.path}")
                }
            }

        }
    }
}

tasks {
    create<Jar>("paperJar") {
        from(project(":kommand-api").sourceSets["main"].output)
        from(sourceSets["main"].output)

        subprojects.forEach {
            val paperJar = it.tasks.jar.get()
            dependsOn(paperJar)
            from(zipTree(paperJar.archiveFile))
        }
    }

    create<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    create<Jar>("dokkaJar") {
        archiveClassifier.set("javadoc")
        dependsOn("dokkaHtml")

        from("$buildDir/dokka/html/") {
            include("**")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("kommand") {
            artifactId = "kommand"

            artifact(tasks["paperJar"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["dokkaJar"])

            repositories {
                mavenLocal()

                maven {
                    name = "central"

                    credentials.runCatching {
                        val nexusUsername: String by project
                        val nexusPassword: String by project
                        username = nexusUsername
                        password = nexusPassword
                    }.onFailure {
                        logger.warn("Failed to load nexus credentials, Check the gradle.properties")
                    }

                    url = uri(
                        if ("SNAPSHOT" in version) {
                            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                        } else {
                            "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                        }
                    )
                }
            }

            pom {
                name.set("kommand")
                description.set("pom description")
                url.set("https://github.com/monun/kommand")

                licenses {
                    license {
                        name.set("GNU General Public License version 3")
                        url.set("https://opensource.org/licenses/GPL-3.0")
                    }
                }

                developers {
                    developer {
                        id.set("monun")
                        name.set("Monun")
                        email.set("monun1010@gmail.com")
                        url.set("https://github.com/monun")
                        roles.addAll("developer")
                        timezone.set("Asia/Seoul")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/monun/kommand.git")
                    developerConnection.set("scm:git:ssh://github.com:monun/kommand.git")
                    url.set("https://github.com/monun/kommand")
                }
            }
        }
    }
}

signing {
    isRequired = true
    sign(tasks["paperJar"], tasks["sourcesJar"], tasks["dokkaJar"])
    sign(publishing.publications["kommand"])
}