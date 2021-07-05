val dependProjects = listOf(project(":kommand-core")) + project(":kommand-nms").subprojects

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")
    dependProjects.forEach { implementation(it) }
}

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
        }
    }

    create<Jar>("debugJar") {
        archiveBaseName.set("Kommand")
        archiveVersion.set("")
        archiveClassifier.set("DEBUG")
        (listOf(project) + dependProjects).forEach {
            println(it.path)
            from(it.sourceSets["main"].output)
        }

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }
}