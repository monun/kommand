dependencies {
    api("io.papermc.paper:paper-mojangapi:1.17-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.17-R0.1-SNAPSHOT:remapped-mojang")
    mojangMapping("org.spigotmc:minecraft-server:1.17-R0.1-SNAPSHOT:maps-mojang@txt")
    spigotMapping("org.spigotmc:minecraft-server:1.17-R0.1-SNAPSHOT:maps-spigot@csrg")
}