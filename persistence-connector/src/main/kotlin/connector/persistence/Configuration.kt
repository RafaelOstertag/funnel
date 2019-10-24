package ch.guengel.funnel.connector.persistence

import com.uchuhimo.konf.ConfigSpec

object Configuration : ConfigSpec("persistence.connector") {
    val kafka by optional("localhost:9092")
    val mongoDbURL by optional("mongodb://localhost", name = "mongodburl")
    val mongoDb by optional("funnel", name = "mongodb")
    val retainMaxFeeds by optional(10, name = "retainmaxfeeds")
}