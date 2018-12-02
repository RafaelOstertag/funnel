package ch.guengel.funnel.persistence.connector

import com.uchuhimo.konf.ConfigSpec

object Configuration : ConfigSpec("persistence.connector") {
    val kafka by optional("localhost:9092")
    val mongoDbURL by optional("mongodb://localhost")
    val mongoDb by optional("funnel")
}