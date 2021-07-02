subprojects {
    configurations {
        create("mojangMapping")
        create("spigotMapping")
    }

    repositories {
        mavenLocal()
    }
}
