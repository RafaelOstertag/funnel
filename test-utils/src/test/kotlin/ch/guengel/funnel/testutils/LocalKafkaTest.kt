package ch.guengel.funnel.testutils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

@DisabledOnOs(OS.OTHER)
internal class LocalKafkaTest {
    @Test
    fun `start and stop`() {
        val localKafka = LocalKafka()
        localKafka.start()
        localKafka.stop()
    }

    @Test
    fun `start and stop twice`() {
        val localKafka = LocalKafka()
        localKafka.start()
        localKafka.stop()

        localKafka.start()
        localKafka.stop()
    }

    @Test
    fun `start twice`() {
        val localKafka = LocalKafka()
        localKafka.start()
        localKafka.start()

        localKafka.stop()
    }

    @Test
    fun `stop on non-started`() {
        val localKafka = LocalKafka()
        localKafka.stop()
    }

    @Test
    fun `stop twice`() {
        val localKafka = LocalKafka()
        localKafka.start()

        localKafka.stop()
        localKafka.stop()
    }
}