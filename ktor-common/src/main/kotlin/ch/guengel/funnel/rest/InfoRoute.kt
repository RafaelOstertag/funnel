package ch.guengel.funnel.rest

import ch.guengel.funnel.build.readBuildInfo
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.infoRoute() = createRouteFromPath("/info").apply {
    val buildInfo = readBuildInfo()
    get {
        call.respond(buildInfo)
    }
}
