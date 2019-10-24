package funnel.connector.retriever

import com.uchuhimo.konf.ConfigSpec

object Configuration : ConfigSpec("retrieval.connector") {
    val kafka by optional("localhost:9092")
}