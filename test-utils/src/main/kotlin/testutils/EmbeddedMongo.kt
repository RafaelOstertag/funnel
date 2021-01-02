package ch.guengel.funnel.testutils

import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfig

import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network

class EmbeddedMongo {
    val mongoPort = Network.getFreeServerPort()

    private var mongodConfig = MongodConfig.builder()
        .version(Version.Main.PRODUCTION)
        .net(Net(mongoPort, Network.localhostIsIPv6()))
        .build()
    private val mongoServer: MongodExecutable = MongodStarter.getDefaultInstance().prepare(mongodConfig)
    private var mongod: MongodProcess? = null

    fun start() {
        if (mongod != null) {
            System.err.println("Warning: Embedded mongo already started on port ${mongoPort}")
            return
        }

        mongod = mongoServer.start()
    }

    fun stop() {
        if (mongod == null) {
            System.err.println("Warning: Embedded mongo not started. Call start() first")
            return
        }
        mongod?.stop()
        mongod = null
    }
}