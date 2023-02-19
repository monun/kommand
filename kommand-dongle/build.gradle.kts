import io.papermc.paperweight.tasks.RemapJar

plugins {
    id("io.papermc.paperweight.userdev") version "1.5.1" apply false
}

subprojects {
    // net.minecraft.server 프로젝트의 이름은 반드시 v로 시작 [v1.19]
    apply(plugin = "io.papermc.paperweight.userdev")
    dependencies {
        implementation(projectApi)
        implementation(projectCore)

        val paperweight = (this as ExtensionAware).extensions.getByName("paperweight")
                as io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
        paperweight.paperDevBundle("${name.substring(1)}-R0.1-SNAPSHOT")
    }
}

// upstream
coreDevJar {
    from(subprojects.map { it.sourceSets["main"].output })
}

coreReobfJar {
    subprojects.map { it.tasks.named("reobfJar").get() as RemapJar }.onEach {
        from(zipTree(it.outputJar))
    }.let {
        dependsOn(it)
    }
}

coreSourcesJar {
    from(subprojects.map { it.sourceSets["main"].allSource })
}
