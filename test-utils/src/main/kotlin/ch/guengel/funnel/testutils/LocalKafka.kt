package ch.guengel.funnel.testutils

import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

class LocalKafka {
    private val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.0.1-1-ubi8"))
    val bootstrapServer: String? get() = kafkaContainer.bootstrapServers

    fun start() {
        kafkaContainer.start()
    }

    fun stop() {
        kafkaContainer.stop()
    }
}