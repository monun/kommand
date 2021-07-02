
rootProject.name = "kommand"

val nms = "kommand-nms"

include(
    "kommand-core",
    nms,
    "kommand-spigot"
)

file(nms).listFiles()?.filter {
    it.isDirectory && it.name.startsWith("v")
}?.forEach { file ->
    include(":$nms:${file.name}")
}