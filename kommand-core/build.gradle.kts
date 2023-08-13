plugins {
    id("io.papermc.paperweight.userdev") version "1.5.5" apply false
}

dependencies {
    api(projectApi)
}

tasks {
    jar {
        archiveClassifier.set("commons")
    }

    register<Jar>("devJar") {
        archiveClassifier.set("dev")

        from(sourceSets["main"].output)
        subprojects {
            from(sourceSets["main"].output)
        }
    }

    register<Jar>("reobfJar") {
        archiveClassifier.set("reobf")

        from(sourceSets["main"].output)
        subprojects {
            val reobfJar = tasks.named("reobfJar").get() as io.papermc.paperweight.tasks.RemapJar
            dependsOn(reobfJar)
            from(zipTree(reobfJar.outputJar))
        }
    }
}

subprojects {
    apply(plugin = "io.papermc.paperweight.userdev")
    dependencies {
        implementation(projectApi)
        implementation(projectCore)

        val paperweight = (this as ExtensionAware).extensions.getByName("paperweight")
                as io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
        paperweight.paperDevBundle("${name.substring(1)}-R0.1-SNAPSHOT")
    }
}
