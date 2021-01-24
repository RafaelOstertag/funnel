package ch.guengel.funnel.testutils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

@DisabledOnOs(OS.OTHER)
internal class LocalMongoDBTest {
    @Test
    fun `start and stop`() {
        val mongoDB = LocalMongoDB()
        mongoDB.start()
        mongoDB.stop()
    }

    @Test
    fun `start and stop twice`() {
        val mongoDB = LocalMongoDB()
        mongoDB.start()
        mongoDB.stop()

        mongoDB.start()
        mongoDB.stop()
    }

    @Test
    fun `start twice`() {
        val mongoDB = LocalMongoDB()
        mongoDB.start()
        mongoDB.start()

        mongoDB.stop()
    }

    @Test
    fun `stop on non-started`() {
        val mongoDB = LocalMongoDB()
        mongoDB.stop()
    }

    @Test
    fun `stop twice`() {
        val mongoDB = LocalMongoDB()
        mongoDB.start()

        mongoDB.stop()
        mongoDB.stop()
    }
}