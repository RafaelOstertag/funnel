package ch.guengel.funnel.build

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class GitInfo(
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
        noTime,
        "n/a",
        "n/a",
        noTime,
        true
    )
}