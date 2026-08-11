package ec.edu.puce.pucemarket.client.impl

import ec.edu.puce.pucemarket.client.UserContactClient
import ec.edu.puce.pucemarket.exception.ExternalServiceException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "app.user-contact.stub")
class StubUserContactProperties {
    var phoneByUsername: Map<String, String> = emptyMap()
}

@Component
@EnableConfigurationProperties(StubUserContactProperties::class)
class StubUserContactClient(
    private val properties: StubUserContactProperties,
) : UserContactClient {
    override fun getPhoneByUsername(username: String): String = properties.phoneByUsername[username]
        ?.filter(Char::isDigit)
        ?.takeIf { it.length in 8..15 }
        ?: throw ExternalServiceException("No existe un teléfono disponible para el usuario seleccionado")
}
