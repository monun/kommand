subprojects {
    configurations {
        create("mojangMapping")
        create("spigotMapping")
    }

    repositories {
        mavenLocal()
    }

    dependencies {
        implementation(project(":kommand-core"))
    }
}
