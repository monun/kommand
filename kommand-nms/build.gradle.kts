subprojects {
    configurations {
        create("mojangMapping")
        create("spigotMapping")
    }

    repositories {
        mavenLocal()
        maven("https://libraries.minecraft.net")
    }

    dependencies {
        implementation(project(":kommand-core"))
    }
}
