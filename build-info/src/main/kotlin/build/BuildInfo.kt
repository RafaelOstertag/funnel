package ch.guengel.funnel.build

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.time.OffsetDateTime
import java.time.ZoneOffset

private val logger = LoggerFactory.getLogger("build-info")

data class BuildInfo(
    @get:JsonProperty("git.branch")
    val branch: String,
    @get:JsonProperty("git.build.host")
    val buildHost: String,
    @get:JsonProperty("git.build.time")
    val buildTime: OffsetDateTime,
    @get:JsonProperty("git.build.version")
    val buildVersion: String,
    @get:JsonProperty("git.commit.id.abbrev")
    val commitIdAbbrev: String,
    @get:JsonProperty("git.commit.time")
    val commitTime: OffsetDateTime,
    @get:JsonProperty("git.dirty")
    val dirty: Boolean
) {
    constructor() : this(
        "n/a",
        "n/a",
        OffsetDateTime.of(1979, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
        "n/a",
        "n/a",
        OffsetDateTime.of(1979, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
        true
    )
}

private object JSON {
    val objectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
        .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
}

fun readBuildInfo(resourcePath: String): BuildInfo {
    try {
        return object {}.javaClass.getResourceAsStream(resourcePath).use {
            val inputStreamReader = InputStreamReader(it)
            val buildInfo = JSON.objectMapper.readValue<BuildInfo>(inputStreamReader)
            return@use buildInfo
        }
    } catch (e: NullPointerException) {
        logger.warn("Cannot read ${resourcePath}. Return empty build info")
        return BuildInfo()
    }
}