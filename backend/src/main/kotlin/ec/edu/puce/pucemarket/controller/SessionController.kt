package ec.edu.puce.pucemarket.controller

import ec.edu.puce.pucemarket.security.CurrentUser
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class SessionResponse(
    val username: String,
    val roles: List<String>,
)

@RestController
@RequestMapping("/api/session")
class SessionController(
    private val currentUser: CurrentUser,
) {
    @GetMapping
    fun currentSession(@AuthenticationPrincipal jwt: Jwt): SessionResponse = SessionResponse(
        username = currentUser.username(jwt),
        roles = jwt.getClaimAsStringList("cognito:groups").orEmpty(),
    )
}