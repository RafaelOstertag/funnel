package ch.guengel.funnel.retriever.connector

import com.uchuhimo.konf.ConfigSpec

object Configuration : ConfigSpec("retrieval.connector") {
    val kafka by optional("localhost:9092")
    val interval by optional(180)
}