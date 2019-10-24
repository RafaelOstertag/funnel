package ch.guengel.funnel.connector.xmlretriever

import com.uchuhimo.konf.ConfigSpec

object Configuration : ConfigSpec("retrieval.connector") {
    val kafka by optional("localhost:9092")
}