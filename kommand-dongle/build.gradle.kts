plugins {
    id("io.papermc.paperweight.userdev") version "1.3.3-SNAPSHOT" apply false
}

val projectAPI = project(":${rootProject.name}-api")
val projectCORE = project(":${rootProject.name}-core")

subprojects {
    // net.minecraft.server 프로젝트의 이름은 v로 시작
    if (name[0] != 'v') return@subprojects

    apply(plugin = "io.papermc.paperweight.userdev")

    dependencies {
        implementation(projectAPI)
        implementation(projectCORE)
        paperDevBundle("${name.substring(1)}-R0.1-SNAPSHOT")
    }
}

tasks {
    jar {
        also { dongleJar ->
            subprojects.filter { it.name[0] == 'v' }.forEach { subproject ->
                subproject.tasks.named("reobfJar") {
                    dongleJar.dependsOn(this)
                    dongleJar.from(zipTree((this as io.papermc.paperweight.tasks.RemapJar).outputJar))
                }
            }
        }
    }
}





