repositories {
    mavenLocal()
}


dependencies {
    implementation(project(":kommand-api"))
}

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
        }
    }

    create<Jar>("debugMojangJar") {
        archiveBaseName.set("Kommand")
        archiveVersion.set("")
        archiveClassifier.set("DEBUG")
        archiveAppendix.set("MOJANG")

        (listOf(
            project(":kommand-api"),
            project
        ) + project(":kommand-core").let { listOf(it) + it.subprojects }).forEach {
            from(it.sourceSets["main"].output)
        }

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug-mojang/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }

    create<Jar>("debugPaperJar") {
        archiveBaseName.set("Kommand")
        archiveVersion.set("")
        archiveClassifier.set("DEBUG")
        archiveAppendix.set("PAPER")

        (listOf(project(":kommand-api"), project)).forEach {
            from(it.sourceSets["main"].output)
        }

        (project(":kommand-core").tasks.named("paperJar").get() as Jar).let { paperJar ->
            dependsOn(paperJar)
            from(zipTree(paperJar.archiveFile))
        }

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug-paper/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }
}
