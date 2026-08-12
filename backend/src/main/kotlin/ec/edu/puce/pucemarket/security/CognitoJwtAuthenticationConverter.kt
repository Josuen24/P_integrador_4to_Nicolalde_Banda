package ec.edu.puce.pucemarket.security

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

@Component
class CognitoJwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {
    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities = jwt.getClaimAsStringList("cognito:groups")
            .orEmpty()
            .map { SimpleGrantedAuthority("ROLE_$it") }
        val username = jwt.getClaimAsString("cognito:username") ?: jwt.subject
        return JwtAuthenticationToken(jwt, authorities, username)
    }
}
