package ec.edu.puce.pucemarket.security

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class CurrentUser {
    fun username(jwt: Jwt): String = jwt.getClaimAsString("cognito:username") ?: jwt.subject
}
