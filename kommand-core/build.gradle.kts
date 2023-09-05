plugins {
    alias(libs.plugins.paperweight) apply false
}

dependencies {
    implementation(projectApi)
}

//tasks {
//    register<Jar>("devJar") {
//        archiveClassifier.set("dev")
//
//        from(sourceSets["main"].output)
//        subprojects {
//            from(sourceSets["main"].output)
//        }
//    }
//
//    register<Jar>("reobfJar") {
//        archiveClassifier.set("reobf")
//
//        from(sourceSets["main"].output)
//        subprojects {
//            val reobfJar = tasks.named("reobfJar").get() as io.papermc.paperweight.tasks.RemapJar
//            dependsOn(reobfJar)
//            from(zipTree(reobfJar.outputJar))
//        }
//    }
//}

subprojects {
    apply(plugin = rootProject.libs.plugins.paperweight.get().pluginId)
    dependencies {
        implementation(projectApi)
        implementation(projectCore)

        val paperweight = (this as ExtensionAware).extensions.getByName("paperweight")
                as io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
        paperweight.paperDevBundle("${name.substring(1)}-R0.1-SNAPSHOT")
    }
}
