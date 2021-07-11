dependencies {
    // source
    compileOnly("com.mojang:brigadier:1.0.18")
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-mojangapi:1.17.1-R0.1-SNAPSHOT")

    // binary
    compileOnly("io.papermc.paper:paper:1.17.1-R0.1-SNAPSHOT:mojang-mapped")
    mojangMapping("org.spigotmc:minecraft-server:1.17.1-R0.1-SNAPSHOT:maps-mojang@txt")
    spigotMapping("org.spigotmc:minecraft-server:1.17.1-R0.1-SNAPSHOT:maps-spigot@csrg")
}
