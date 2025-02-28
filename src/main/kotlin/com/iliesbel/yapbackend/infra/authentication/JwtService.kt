package com.iliesbel.yapbackend.infra.authentication

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey


@Service
class JwtService(private val securityConfiguration: SecurityConfigProperties) {

    fun extractUsername(token: String?): String {
        return extractClaim<String>(token, Claims::getSubject)
    }

    fun <T> extractClaim(token: String?, claimsResolver: (Claims) -> T): T {
        val claims: Claims = extractAllClaims(token)

        return claimsResolver(claims)
    }

    fun generateToken(userDetails: UserDetails): String {
        return generateToken(HashMap(), userDetails)
    }

    fun generateToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String {
        return buildToken(extraClaims, userDetails, securityConfiguration.expirationTime)
    }

    private fun buildToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails,
        expiration: Long,
    ): String {
        return Jwts
            .builder().apply {
                claims(extraClaims)
                subject(userDetails.username)
                issuedAt(Date(System.currentTimeMillis()))
                expiration(Date(System.currentTimeMillis() + expiration))
                signWith(signInKey)
            }
            .compact()
    }

    fun getExpirationTime(): Long {
        return securityConfiguration.expirationTime
    }

    fun isTokenValid(token: String?, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username) && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String?): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String?): Date {
        return extractClaim(token, Claims::getExpiration)
    }


    private fun extractAllClaims(token: String?): Claims {
        return Jwts
            .parser()
            .verifyWith(signInKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private val signInKey: SecretKey
        get() {
            val keyBytes: ByteArray = Decoders.BASE64.decode(securityConfiguration.secretKey)
            return Keys.hmacShaKeyFor(keyBytes)
        }
}
