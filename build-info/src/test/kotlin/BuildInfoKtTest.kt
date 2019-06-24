import assertk.assert
import assertk.assertions.isEqualTo
import ch.guengel.funnel.build.info.BuildInfo
import ch.guengel.funnel.build.info.readBuildInfo
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class BuildInfoKtTest {

    @Test
    fun `read non existing info`() {
        val actualEmpty = readBuildInfo("choke_on_this")
        assert(actualEmpty).isEqualTo(BuildInfo())
    }

    @Test
    fun `read info`() {
        val actual = readBuildInfo("/git.json")
        assert(actual.branch).isEqualTo("development")
        assert(actual.buildHost).isEqualTo("raptor")
        assert(actual.buildTime).isEqualTo(OffsetDateTime.of(2019, 6, 23, 14, 43, 39, 0, ZoneOffset.ofHours(2)))
        assert(actual.buildVersion).isEqualTo("2.2.0-SNAPSHOT")
        assert(actual.commitIdAbbrev).isEqualTo("d3da4d2")
        assert(actual.commitTime).isEqualTo(OffsetDateTime.of(2019, 6, 23, 13, 44, 23, 0, ZoneOffset.ofHours(2)))
    }
}