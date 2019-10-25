package rest.modules

import ch.guengel.funnel.build.readBuildInfo
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.createRouteFromPath
import io.ktor.routing.get

fun Route.infoRoute() = createRouteFromPath("/info").apply {
    get {
        val buildInfo = readBuildInfo("/git.json")
        call.respond(buildInfo)
    }
}
