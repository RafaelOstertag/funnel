package ch.guengel.funnel.testutils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

@DisabledOnOs(OS.OTHER)
class EmbeddedMongoTest {
    @Test
    fun `start and stop`() {
        val embeddedMongo = EmbeddedMongo()
        embeddedMongo.start()
        embeddedMongo.stop()
    }

    @Test
    fun `start and stop twice`() {
        val embeddedMongo = EmbeddedMongo()
        embeddedMongo.start()
        embeddedMongo.stop()

        embeddedMongo.start()
        embeddedMongo.stop()
    }

    @Test
    fun `start twice`() {
        val embeddedMongo = EmbeddedMongo()
        embeddedMongo.start()
        embeddedMongo.start()

        embeddedMongo.stop()
    }

    @Test
    fun `stop on non-started`() {
        val embeddedMongo = EmbeddedMongo()
        embeddedMongo.stop()
    }

    @Test
    fun `stop twice`() {
        val embeddedMongo = EmbeddedMongo()
        embeddedMongo.start()

        embeddedMongo.stop()
        embeddedMongo.stop()
    }
}