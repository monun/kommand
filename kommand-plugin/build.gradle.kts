import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import java.util.*

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation(projectApi)
}

extra.apply {
    set("pluginName", rootProject.name.split('-').joinToString("") {
        it.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
        }
    })
    set("packageName", rootProject.name.replace("-", ""))
    set("kotlinVersion", Libraries.Kotlin.Version)
    set("paperVersion", Libraries.Paper.Version.split('.').take(2).joinToString("."))

    val pluginLibraries = LinkedHashSet<String>()

    configurations.findByName("implementation")?.allDependencies?.forEach { dependency ->
        val group = dependency.group ?: error("group is null")
        var name = dependency.name ?: error("name is null")
        var version = dependency.version

        if (group == "org.jetbrains.kotlin" && version == null) {
            version = getKotlinPluginVersion()
        } else if (dependency is ProjectDependency && dependency.dependencyProject == projectApi) {
            name = projectCore.name
        }

        requireNotNull(version) { "version is null" }
        require(version != "latest.release") { "version is latest.release" }

        pluginLibraries += "$group:$name:$version"
        set("pluginLibraries", pluginLibraries.joinToString("\n  ") { "- $it" })
    }
}

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    fun registerJar(name: String, bundle: Boolean) {
        val taskName = name + "Jar"

        register<ShadowJar>(taskName) {
            archiveClassifier.set(name)
            archiveAppendix.set(if (bundle) "bundle" else "clip")

            from(sourceSets["main"].output)

            if (bundle) {
                from(projectCore.tasks[taskName])
                configurations = listOf(
                    project.configurations.runtimeClasspath.get(),
                    projectCore.configurations.runtimeClasspath.get()
                )
                exclude { it.file in projectCore.tasks.jar.get().outputs.files }

                exclude("clip-plugin.yml")
                rename("bundle-plugin.yml", "plugin.yml")
            } else {
                exclude("bundle-plugin.yml")
                rename("clip-plugin.yml", "plugin.yml")
            }

            doLast {
                val plugins = rootProject.file(".server/plugins-$name")
                val update = plugins.resolve("update")

                copy {
                    from(archiveFile)

                    if (plugins.resolve(archiveFileName.get()).exists())
                        into(update)
                    else
                        into(plugins)
                }

                update.resolve("UPDATE").deleteOnExit()
            }
        }
    }

    registerJar("dev", true)
    registerJar("reobf", true)
    registerJar("clip", false)
}
