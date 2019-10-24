package ch.guengel.funnel.configuration

import assertk.assertThat
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

        assertThat(configuration[TestConfig.config1]).isEqualTo("value")
        assertThat(configuration[TestConfig.config2]).isEqualTo(42)
    }
}
