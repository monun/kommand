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

    create<Jar>("debugJar") {
        archiveBaseName.set("Kommand")
        archiveVersion.set("")
        archiveClassifier.set("DEBUG")

        (listOf(project(":kommand-api")) + project(":kommand-core").let { listOf(it) + it.subprojects }).forEach {
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
