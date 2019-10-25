package ch.guengel.funnel.build

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

private val logger = LoggerFactory.getLogger("build-info")
internal val noTime = OffsetDateTime.of(1979, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)

data class BuildInfo(val gitInfo: GitInfo, val mavenInfo: MavenInfo)

private object JSON {
    val objectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
        .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
}

fun readBuildInfo(): BuildInfo {
    val gitInfo = readGitInfo("/git.json")
    val mavenInfo = readMavenInfo("/build.info")
    return BuildInfo(gitInfo, mavenInfo)
}

internal fun readGitInfo(resourcePath: String): GitInfo {
    try {
        return object {}.javaClass.getResourceAsStream(resourcePath).use {
            val inputStreamReader = InputStreamReader(it)
            val gitInfo = JSON.objectMapper.readValue<GitInfo>(inputStreamReader)
            return@use gitInfo
        }
    } catch (e: NullPointerException) {
        logger.warn("Cannot read ${resourcePath}. Return empty git info")
        return GitInfo()
    }
}

internal fun readMavenInfo(resourcePath: String): MavenInfo {
    try {
        return object {}.javaClass.getResourceAsStream(resourcePath).use {
            val inputStreamReader = InputStreamReader(it)
            val mavenInfo = Properties()
            mavenInfo.load(inputStreamReader)
            return@use MavenInfo(
                mavenInfo.getProperty("project.groupId"),
                mavenInfo.getProperty("project.artifactId"),
                mavenInfo.getProperty("project.version")
            )
        }
    } catch (e: NullPointerException) {
        logger.warn("Cannot read ${resourcePath}. Return empty maven info")
        return MavenInfo()
    }
}