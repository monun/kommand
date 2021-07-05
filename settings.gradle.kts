rootProject.name = "kommand"

val nms = "kommand-nms"

include(
    "kommand-core",
    nms,
    "kommand-paper"
)

file(nms).listFiles()?.filter {
    it.isDirectory && it.name.startsWith("v")
}?.forEach { file ->
    include(":$nms:${file.name}")
}