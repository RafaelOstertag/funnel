package ch.guengel.funnel.chronos

import com.uchuhimo.konf.ConfigSpec

object Configuration : ConfigSpec("retrieval.connector") {
    val kafka by optional("localhost:9092")
    val mongoDbURL by optional("mongodb://localhost", name = "mongodburl")
    val mongoDb by optional("funnel", name = "mongodb")
    val interval by optional(180)
}