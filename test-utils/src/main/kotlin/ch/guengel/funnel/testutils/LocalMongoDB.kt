package ch.guengel.funnel.testutils

import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

class LocalMongoDB {
    private val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:4"))
    val mongoPort: Int get() = mongoDBContainer.getMappedPort(27017)

    fun start() {
        mongoDBContainer.start()
    }

    fun stop() {
        mongoDBContainer.stop()
    }
}