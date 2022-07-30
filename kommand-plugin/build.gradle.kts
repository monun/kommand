import org.gradle.configurationcache.extensions.capitalized

dependencies {
    implementation(projectApi)
}

extra.apply {
    set("pluginName", rootProject.name.split('-').joinToString("") { it.capitalize() })
    set("packageName", rootProject.name.replace("-", ""))
    set("kotlinVersion", Dependency.Kotlin.Version)
    set("paperVersion", Dependency.Paper.Version)
}

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    fun registerJar(
        classifier: String,
        bundleProject: Project? = null,
        bundleTask: TaskProvider<org.gradle.jvm.tasks.Jar>? = null
    ) = register<Jar>("${classifier}Jar") {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set(classifier)

        from(sourceSets["main"].output)

        if (bundleProject != null) from(bundleProject.sourceSets["main"].output)

        if (bundleTask != null) {
            bundleTask.let { bundleJar ->
                dependsOn(bundleJar)
                from(zipTree(bundleJar.get().archiveFile))
            }
            exclude("clip.yml")
            rename("bundle.yml", "plugin.yml")
        } else {
            exclude("bundle.yml")
            rename("clip.yml", "plugin.yml")
        }
    }.also { jar ->
        register<Copy>("test${classifier.capitalized()}Jar") {
            val prefix = project.name
            val plugins = rootProject.file(".server/plugins-$classifier")
            val update = File(plugins, "update")
            val regex = Regex("($prefix).*(.jar)")

            from(jar)
            into(if (plugins.listFiles { _, it -> it.matches(regex) }?.isNotEmpty() == true) update else plugins)

            doLast {
                update.mkdirs()
                File(update, "RELOAD").delete()
            }
        }
    }

    registerJar("dev", projectApi, coreDevJar)
    registerJar("reobf", projectApi, coreReobfJar)
    registerJar("clip")
}