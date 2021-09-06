repositories {
    mavenLocal()
}

val projectAPI = project(":kommand-api")
val projectCore = project(":kommand-core")

dependencies {
    implementation(projectAPI)
}

val pluginName = rootProject.name.split('-').joinToString("") { it.capitalize() }
val packageName = rootProject.name.replace("-", "")
extra.set("pluginName", pluginName)
extra.set("packageName", packageName)

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    /**
     * mojang mapping jar
     *
     * 빠른 디버그를 위해서 모든 모듈을 jar안에 포함
     */
    create<Jar>("mojangJar") {
        archiveBaseName.set(pluginName)
        archiveVersion.set("")
        archiveClassifier.set("MOJANG")

        (listOf(projectAPI, project, projectCore) + projectCore.subprojects).forEach {
            from(it.sourceSets["main"].output)
        }

        from(project.sourceSets["main"].output)
        exclude("paper-plugin.yml")
        rename("mojang-plugin.yml", "plugin.yml")

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug-mojang/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }

    /**
     * spigot mapping jar
     *
     * 실제 환경 테스트를 위해서 서버에 모듈을 배포
     * jar 파일에는 debug 모듈만 포함
     */
    create<Jar>("paperJar") {
        dependsOn(projectAPI.tasks.named("publishAllPublicationsToDebugRepository"))
        dependsOn(projectCore.tasks.named("publishAllPublicationsToDebugRepository"))

        archiveBaseName.set(pluginName)
        archiveVersion.set("")
        archiveClassifier.set("PAPER")

        from(project.sourceSets["main"].output)
        exclude("mojang-plugin.yml")
        rename("paper-plugin.yml", "plugin.yml")

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug-paper/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }
}
