dependencies {
    api(projectApi)
}

tasks {
    jar {
        archiveClassifier.set("origin")
    }

    register<Jar>("coreDevJar") {
        from(sourceSets["main"].output)
    }

    register<Jar>("coreReobfJar") {
        from(sourceSets["main"].output)
    }
}