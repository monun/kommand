rootProject.name = "kommand"

val core = "kommand-core"

include(
    "kommand-api",
    "kommand-core",
    "kommand-debug"
)

// load nms
file(core).listFiles()?.filter {
    it.isDirectory && it.name.startsWith("v")
}?.forEach { file ->
    include(":$core:${file.name}")
}