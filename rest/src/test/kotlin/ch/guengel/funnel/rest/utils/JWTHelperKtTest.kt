package ch.guengel.funnel.rest.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import ch.guengel.funnel.feed.data.User
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.*

internal class JWTHelperKtTest {

    @Test
    fun `throw exception on null principal`() {
        assertThat { getUserId(null) }.isFailure().isInstanceOf(AuthenticationException::class)
        assertThat { getEmail(null) }.isFailure().isInstanceOf(AuthenticationException::class)
        assertThat { extractUser(null) }.isFailure().isInstanceOf(AuthenticationException::class)
    }

    @Test
    fun `throw exception on wrong principal type`() {
        assertThat { getUserId(DummyPrincipal()) }.isFailure().isInstanceOf(AuthenticationException::class)
        assertThat { getEmail(DummyPrincipal()) }.isFailure().isInstanceOf(AuthenticationException::class)
        assertThat { extractUser(DummyPrincipal()) }.isFailure().isInstanceOf(AuthenticationException::class)
    }

    @Test
    fun `get user id`() {
        val payload = mockk<Payload>()
        val userId = UUID.randomUUID().toString()

        every { payload.subject } returns userId

        val jwtPrincipal = JWTPrincipal(payload)

        assertThat(getUserId(jwtPrincipal)).isEqualTo(userId)
    }

    @Test
    fun getEmail() {
        val payload = mockk<Payload>()
        val emailAddress = UUID.randomUUID().toString()
        val emailClaim = mockk<Claim>()

        every { emailClaim.isNull } returns false
        every { emailClaim.asString() } returns emailAddress

        every { payload.getClaim("email") } returns emailClaim

        val jwtPrincipal = JWTPrincipal(payload)

        assertThat(getEmail(jwtPrincipal)).isEqualTo(emailAddress)
    }

    @Test
    fun `throw exception on null email`() {
        val payload = mockk<Payload>()
        val emailClaim = mockk<Claim>()
        every { emailClaim.isNull } returns true
        every { payload.getClaim("email") } returns emailClaim

        val jwtPrincipal = JWTPrincipal(payload)

        assertThat { getEmail(jwtPrincipal) }.isFailure().isInstanceOf(AuthenticationException::class)
    }

    @Test
    fun extractUser() {
        val payload = mockk<Payload>()
        val userId = UUID.randomUUID().toString()

        every { payload.subject } returns userId

        val emailAddress = UUID.randomUUID().toString()
        val emailClaim = mockk<Claim>()

        every { emailClaim.isNull } returns false
        every { emailClaim.asString() } returns emailAddress

        every { payload.getClaim("email") } returns emailClaim

        val jwtPrincipal = JWTPrincipal(payload)

        assertThat(extractUser(jwtPrincipal)).isEqualTo(User(userId, emailAddress))
    }

    private class DummyPrincipal : Principal
}