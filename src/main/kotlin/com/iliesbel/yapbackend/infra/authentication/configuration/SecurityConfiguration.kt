package com.iliesbel.yapbackend.infra.authentication.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            cors {}
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/auth/**", permitAll)
                authorize("/api/public/**", permitAll)
                authorize("/reddit/callback", authenticated)
                authorize("/actuator/**", permitAll)

                authorize("/error", permitAll)
                authorize(anyRequest, authenticated)
            }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            addFilterAfter<BasicAuthenticationFilter>(jwtAuthenticationFilter)

            authenticationProvider
        }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {

            allowedOrigins = listOf("*")

            allowedMethods = listOf("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")

            allowedHeaders = listOf(
                "Authorization",
                "Content-Type",
                "Origin",
                "Accept",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-Device"
            )

            maxAge = 3600L
        }


        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}