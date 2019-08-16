package io.github.tiscs.scp.config

import io.github.tiscs.scp.services.DbClientDetailsService
import io.github.tiscs.scp.services.RedisAuthCodeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore

@Configuration
@EnableAuthorizationServer
class OAuth2ServerConfig(
        @Autowired private val passwordEncoder: PasswordEncoder,
        @Autowired private val authenticationManager: AuthenticationManager,
        @Autowired private val clientDetailsService: DbClientDetailsService,
        @Autowired private val authCodeService: RedisAuthCodeService
) : AuthorizationServerConfigurer {
    @Bean
    fun tokenStore(): TokenStore = InMemoryTokenStore()

    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security.tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("isAuthenticated()")
                .passwordEncoder(passwordEncoder)
    }

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.withClientDetails(clientDetailsService)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.authenticationManager(authenticationManager)
                .authorizationCodeServices(authCodeService)
                .tokenStore(tokenStore())
                .reuseRefreshTokens(false)
    }
}