plugins {
    kotlin("jvm") version "1.5.20"
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.4.32"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    mavenLocal()
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation("org.mockito:mockito-core:3.6.28")
    testImplementation("org.spigotmc:spigot:1.16.5-R0.1-SNAPSHOT")
}

tasks {
    create<Jar>("sourcesJar") {
        from(sourceSets["main"].allSource)
        archiveClassifier.set("sources")
    }

    create<Jar>("dokkaJar") {
        archiveClassifier.set("javadoc")
        dependsOn("dokkaHtml")

        from("$buildDir/dokka/html/") {
            include("**")
        }

        from("$rootDir/src/main/resources/") {
            include("*.html")
        }
    }

    test {
        useJUnitPlatform()
    }
}

publishing {
    publications {
        create<MavenPublication>(rootProject.name) {
            from(components["java"])
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
                name.set(rootProject.name)
                description.set("Command DSL for paper plugin")
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
}

signing {
    isRequired = true
    sign(tasks["sourcesJar"], tasks["dokkaJar"], tasks["jar"])
    sign(publishing.publications[rootProject.name])
}