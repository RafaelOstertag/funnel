package ch.guengel.funnel.rest.utils

import ch.guengel.funnel.feed.data.User
import com.auth0.jwt.interfaces.Payload
import io.ktor.auth.*
import io.ktor.auth.jwt.*

private fun getJwtPayload(principal: Principal?): Payload {
    principal ?: throw AuthenticationException("No principal provided")

    if (principal is JWTPrincipal) {
        return principal.payload
    } else {
        throw AuthenticationException("Principal type not supported")
    }
}

fun getUserId(principal: Principal?): String {
    val jwtPayload = getJwtPayload(principal)
    return jwtPayload.subject
}

fun getEmail(principal: Principal?): String {
    val jwtPayload = getJwtPayload(principal)
    val emailClaim = jwtPayload.getClaim("email")
    if (emailClaim.isNull) {
        throw AuthenticationException("No 'email' claim found")
    }

    return emailClaim.asString()
}

fun extractUser(principal: Principal?): User {
    return User(getUserId(principal), getEmail(principal))
}