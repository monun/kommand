import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

private fun Project.subproject(name: String) = project(":${rootProject.name}-$name")

val Project.projectApi
    get() = subproject("api")

val Project.projectCore
    get() = subproject("core")

val Project.projectDongle
    get() = findProject(":${rootProject.name}-dongle")

val Project.projectPlugin
    get() = subproject("plugin")

private fun Project.coreTask(name: String) = projectCore.tasks.named(name, Jar::class.java)

val Project.coreDevJar
    get() = coreTask("coreDevJar")

val Project.coreReobfJar
    get() = coreTask("coreReobfJar")

val Project.coreSourcesJar
    get() = coreTask("sourcesJar")