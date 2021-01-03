package ch.guengel.funnel.rest.modules

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import java.net.URL
import java.util.concurrent.TimeUnit

fun Application.authenticationModule() {
    log.info("Setup OAuth")

    val jwkIssuer = environment.config.property("jwk.issuer").getString()
    val jwkRealm = environment.config.property("jwk.realm").getString()
    val jwkAudience = environment.config.property("jwk.audience").getString()

    val jwkProvider = JwkProviderBuilder(URL("$jwkIssuer/protocol/openid-connect/certs"))
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    install(Authentication) {
        jwt {
            verifier(jwkProvider, jwkIssuer)
            realm = jwkRealm
            validate { credential ->
                if (credential.payload.audience.contains(jwkAudience))
                    JWTPrincipal(credential.payload)
                else
                    null
            }
        }
    }
}
