package ch.guengel.funnel.connector.xmlretriever.http

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

class WiremockTestResource : QuarkusTestResourceLifecycleManager {
    private val wiremockServer: WireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())
    override fun start(): MutableMap<String, String> {
        wiremockServer.start()

        wiremockServer.stubFor(
            get(urlEqualTo("/feed")).willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/xml")
                    .withBody(this::class.java.getResource("/atom-feed.xml").readText())
            )
        )

        wiremockServer.stubFor(get(urlEqualTo("/400")).willReturn(aResponse().withStatus(400)))
        wiremockServer.stubFor(get(urlEqualTo("/500")).willReturn(aResponse().withStatus(500)))

        return mutableMapOf("wiremock.port" to wiremockServer.port().toString())
    }

    override fun stop() {
        wiremockServer.stop()
    }
}
