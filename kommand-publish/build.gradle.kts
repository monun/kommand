plugins {
    `maven-publish`
    signing
}

tasks {
    jar {
        archiveBaseName.set(rootProject.name)

        from(projectApi.sourceSets["main"].output)
        from(projectCore.sourceSets["main"].output)

        projectCore.subprojects.forEach { compat ->
            val reobfJar = compat.tasks.named("reobfJar")
            dependsOn(reobfJar)
            from(zipTree(reobfJar.get().outputs.files.singleFile))
        }
    }

    create<Jar>("devJar") {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("dev")

        from(projectApi.sourceSets["main"].output)
        from(projectCore.sourceSets["main"].output)

        projectCore.subprojects.forEach { compat ->
            from(compat.sourceSets["main"].output)
        }
    }
}

publishing {
    repositories {
        mavenLocal()

        maven {
            name = "server"
            url = rootProject.uri(".server/libraries")
        }

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
                if ("SNAPSHOT" in version as String) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                }
            )
        }
    }

    publications {
        create<MavenPublication>(rootProject.name) {
            artifactId = rootProject.name

            from(project.components["java"])

            if (hasProperty("dev")) {
                artifact(project.tasks["devJar"])
            }
            artifact(projectApi.tasks["sourcesJar"])
            artifact(projectApi.tasks["dokkaJar"])

            pom {
                description.set("Kotlin DSL for PaperMC commands")
                url.set("https://github.com/monun/${rootProject.name}")

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
                    connection.set("scm:git:git://github.com/monun/${rootProject.name}.git")
                    developerConnection.set("scm:git:ssh://github.com:monun/${rootProject.name}.git")
                    url.set("https://github.com/monun/${rootProject.name}")
                }
            }
        }
    }

//    publications {
//        fun MavenPublication.setup(target: Project) {
//            artifactId = target.name
//            from(target.components["java"])
//            artifact(target.tasks["sourcesJar"])
//            artifact(target.tasks["dokkaJar"])
//
//            pom {
//                name.set(target.name)
//                description.set("Kotlin DSL for PaperMC commands")
//                url.set("https://github.com/monun/${rootProject.name}")
//
//                licenses {
//                    license {
//                        name.set("GNU General Public License version 3")
//                        url.set("https://opensource.org/licenses/GPL-3.0")
//                    }
//                }
//
//                developers {
//                    developer {
//                        id.set("monun")
//                        name.set("Monun")
//                        email.set("monun1010@gmail.com")
//                        url.set("https://github.com/monun")
//                        roles.addAll("developer")
//                        timezone.set("Asia/Seoul")
//                    }
//                }
//
//                scm {
//                    connection.set("scm:git:git://github.com/monun/${rootProject.name}.git")
//                    developerConnection.set("scm:git:ssh://github.com:monun/${rootProject.name}.git")
//                    url.set("https://github.com/monun/${rootProject.name}")
//                }
//            }
//        }

//        create<MavenPublication>("api") {
//            setup(projectApi)
//        }
//
//        create<MavenPublication>("core") {
//            setup(projectCore)
//
//            artifact(projectCore.tasks["reobfJar"]) {
//                classifier = null
//            }
//
//            if (hasProperty("dev")) {
//                artifact(projectCore.tasks["devJar"])
//            }
//        }
//    }
}

signing {
    isRequired = true
    sign(publishing.publications[rootProject.name])
}
