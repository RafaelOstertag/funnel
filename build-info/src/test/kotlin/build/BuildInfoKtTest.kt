package ch.guengel.funnel.build

import assertk.assertThat
import assertk.assertions.isEqualTo

import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class BuildInfoKtTest {

    @Test
    fun `read non existing info`() {
        val actualEmpty = readBuildInfo("choke_on_this")
        assertThat(actualEmpty).isEqualTo(BuildInfo())
    }

    @Test
    fun `read info`() {
        val actual = readBuildInfo("/git.json")
        assertThat(actual.branch).isEqualTo("development")
        assertThat(actual.buildHost).isEqualTo("raptor")
        assertThat(actual.buildTime).isEqualTo(OffsetDateTime.of(2019, 6, 23, 14, 43, 39, 0, ZoneOffset.ofHours(2)))
        assertThat(actual.buildVersion).isEqualTo("2.2.0-SNAPSHOT")
        assertThat(actual.commitIdAbbrev).isEqualTo("d3da4d2")
        assertThat(actual.commitTime).isEqualTo(OffsetDateTime.of(2019, 6, 23, 13, 44, 23, 0, ZoneOffset.ofHours(2)))
    }
}