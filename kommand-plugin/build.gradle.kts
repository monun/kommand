import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(projectApi)
}

extra.apply {
    set("kotlinVersion", libs.versions.kotlin)
    set("paperVersion", libs.versions.paper.get().split('.').take(2).joinToString(separator = "."))
}

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    fun Jar.copyToServer(suffix: String) = doLast {
        val pluginsFolder = rootProject.file(".server/plugins-$suffix")
        val updateFolder = pluginsFolder.resolve("update")

        copy {
            from(archiveFile)

            // archiveBaseName 으로 시작하는 파일이 존재하면 update 폴더에 복사
            if (pluginsFolder.listFiles()?.any { it.name.startsWith(archiveBaseName.get()) } == true) {
                into(pluginsFolder.resolve("update"))
            } else {
                into(pluginsFolder)
            }
        }

        updateFolder.resolve("RELOAD").delete()
    }

    register<Jar>("clipPluginJar") {
        archiveAppendix.set("clip")

        from(sourceSets["main"].output)

        copyToServer("clip")
    }

    register<ShadowJar>("devBundlePluginJar") {
        archiveAppendix.set("bundle")
        archiveClassifier.set("dev")

        configurations += projectApi.configurations.runtimeClasspath.get()

        from(sourceSets["main"].output)
        from(projectApi.sourceSets["main"].output)
        from(projectCore.sourceSets["main"].output)

        projectCore.subprojects.forEach { compat ->
            val reobfJar = compat.tasks["reobfJar"]
            dependsOn(reobfJar)

            from(compat.sourceSets["main"].output)
        }

        copyToServer("bundle")
    }
}