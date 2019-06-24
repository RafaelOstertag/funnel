package ch.guengel.funnel

import assertk.assert
import assertk.assertions.isEqualTo
import com.uchuhimo.konf.ConfigSpec
import org.junit.jupiter.api.Test

internal class ConfigurationKtTest {
    object TestConfig : ConfigSpec("prefix") {
        val config1 by required<String>()
        val config2 by required<Int>()
    }

    @Test
    fun `read configuration`() {
        val configuration = readConfiguration(TestConfig, "src/test/resources/testconfig.yml")

        assert(configuration[TestConfig.config1]).isEqualTo("value")
        assert(configuration[TestConfig.config2]).isEqualTo(42)
    }
}
