package ch.guengel.funnel.testutils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

@DisabledOnOs(OS.OTHER)
class LocalMongoDBTest {
    @Test
    fun `start and stop`() {
        val embeddedMongo = LocalMongoDB()
        embeddedMongo.start()
        embeddedMongo.stop()
    }

    @Test
    fun `start and stop twice`() {
        val embeddedMongo = LocalMongoDB()
        embeddedMongo.start()
        embeddedMongo.stop()

        embeddedMongo.start()
        embeddedMongo.stop()
    }

    @Test
    fun `start twice`() {
        val embeddedMongo = LocalMongoDB()
        embeddedMongo.start()
        embeddedMongo.start()

        embeddedMongo.stop()
    }

    @Test
    fun `stop on non-started`() {
        val embeddedMongo = LocalMongoDB()
        embeddedMongo.stop()
    }

    @Test
    fun `stop twice`() {
        val embeddedMongo = LocalMongoDB()
        embeddedMongo.start()

        embeddedMongo.stop()
        embeddedMongo.stop()
    }
}