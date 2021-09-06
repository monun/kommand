package io.github.monun.paperstrap

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request

object PaperAPI {
    private const val paperAPI = "https://papermc.io/api/v2/projects/paper"
    private val client = OkHttpClient()

    private fun infoVersion(version: String): JsonObject {
        val request = Request.Builder().apply {
            header("accept", "application/json")
            url("$paperAPI/versions/$version")
        }.build()

        client.newCall(request).execute().use { response ->
            return JsonParser.parseString(response.body?.string()) as JsonObject
        }
    }

    private fun infoBuild(version: String, build: String): JsonObject {
        Request.Builder().apply {
            header("accept", "application/json")
            url("$paperAPI/versions/$version/builds/$build")
        }.build().let { request ->
            client.newCall(request).execute().use { response ->
                return JsonParser.parseString(response.body?.string()) as JsonObject
            }
        }
    }

    fun latestBuild(version: String): String {
        val versionInfo = infoVersion(version)
        if (!versionInfo.has("builds")) error(versionInfo.toString())
        return versionInfo.getAsJsonArray("builds").last().asString
    }

    fun commit(version: String, build: String): String {
        val response = infoBuild(version, build)
        return (response.getAsJsonArray("changes").first().asJsonObject.get("commit").asString)
    }
}