package ch.guengel.funnel.xmlfeeds.network

import awaitStringResponse
import ch.guengel.funnel.domain.Source
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.runBlocking

class HttpTransport(private val source: Source) {
    private var contentFetched: Boolean = false
    private var contentType: String = ""
    private var response: String = ""
    private val defaultHeaders = mapOf<String, String>(
            "Accept" to "*/*",
            "User-Agent" to "Funnel/1.0"
    )

    private fun fetchResource() {
        if (contentFetched) {
            return
        }
        val result = runBlocking(Dispatchers.IO) {
            val (_, response, result) = source.address.httpGet()
                    .header(defaultHeaders)
                    .awaitStringResponse()

            result.fold({ data ->
                val contentType = getContentTypeFromResponse(response)

                Pair(contentType, result.get())
            }, { error ->
                throw HttpError("Error retrieving ${source.name} from ${source.address}", error.exception)
            })
        }

        contentType = result.first
        response = result.second
        contentFetched = true
    }

    private fun getContentTypeFromResponse(response: Response): String {
        val contentTypeList = response.headers["Content-Type"].orEmpty()

        return if (contentTypeList.isEmpty()) "text/plain" else contentTypeList.get(0)
    }

    fun contentType(): String {
        fetchResource()
        return contentType
    }

    fun response(): String {
        fetchResource()
        return response
    }
}

class HttpError(message: String, cause: Throwable) : Exception(message, cause)