rootProject.name = "kommand"

val prefix = "kommand"
val core = "$prefix-core"

include(
    "$prefix-api",
    core,
    "$prefix-debug"
)

// load nms
file(core).listFiles()?.filter {
    it.isDirectory && it.name.startsWith("v")
}?.forEach { file ->
    include(":$core:${file.name}")
}