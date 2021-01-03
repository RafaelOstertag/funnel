package ch.guengel.funnel.build

import assertk.assertThat
import assertk.assertions.isEqualTo

import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class BuildInfoKtTest {

    @Test
    fun `read non existing git info`() {
        val actualEmpty = readGitInfo("choke_on_this")
        assertThat(actualEmpty).isEqualTo(GitInfo())
    }

    @Test
    fun `read non existing maven info`() {
        val actualEmpty = readMavenInfo("choke_on_this")
        assertThat(actualEmpty).isEqualTo(MavenInfo())
    }

    @Test
    fun `read info`() {
        val actual = readBuildInfo()
        with(actual.gitInfo) {
            assertThat(branch).isEqualTo("development")
            assertThat(buildHost).isEqualTo("raptor")
            assertThat(buildTime).isEqualTo(OffsetDateTime.of(2019, 6, 23, 14, 43, 39, 0, ZoneOffset.ofHours(2)))
            assertThat(buildVersion).isEqualTo("2.2.0-SNAPSHOT")
            assertThat(commitIdAbbrev).isEqualTo("d3da4d2")
            assertThat(commitTime).isEqualTo(
                OffsetDateTime.of(
                    2019,
                    6,
                    23,
                    13,
                    44,
                    23,
                    0,
                    ZoneOffset.ofHours(2)
                )
            )
        }

        with(actual.mavenInfo) {
            assertThat(groupId).isEqualTo("ch.guengel.funnel")
            assertThat(artifactId).isEqualTo("build-info")
            assertThat(version).isEqualTo("4.0.2-SNAPSHOT")
        }
    }
}