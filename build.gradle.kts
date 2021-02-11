plugins {
    kotlin("jvm") version "1.4.30"
    `maven-publish`
}

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("com.destroystokyo.paper:paper-api:1.13.2-R0.1-SNAPSHOT")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
    create<Jar>("sourcesJar") {
        from(sourceSets["main"].allSource)
        archiveClassifier.set("sources")
    }
}

publishing {
    publications {
        create<MavenPublication>("Kommand") {
            artifactId = project.name
            from(components["java"])
            artifact(tasks["sourcesJar"])
        }
    }
}