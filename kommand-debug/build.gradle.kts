

repositories {
    mavenLocal()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")

    implementation(project(":kommand-api"))
    implementation(project(":kommand-core"))
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

        listOf(project(":kommand-api")) + project(":kommand-core").let { listOf(it) + it.subprojects }.forEach {
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
