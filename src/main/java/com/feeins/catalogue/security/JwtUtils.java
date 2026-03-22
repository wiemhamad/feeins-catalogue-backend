package com.feeins.catalogue.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs; // ✅ long au lieu de int

    private SecretKey getSigningKey() { // ✅ SecretKey au lieu de Key
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // ✅ subject()
                .issuedAt(new Date()) // ✅ issuedAt()
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // ✅ expiration()
                .signWith(getSigningKey()) // ✅ sans SignatureAlgorithm
                .compact();
    }

    public String generateTokenFromEmail(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser() // ✅ parser() au lieu de parserBuilder()
                .verifyWith(getSigningKey()) // ✅ verifyWith() au lieu de setSigningKey()
                .build()
                .parseSignedClaims(token) // ✅ parseSignedClaims() au lieu de parseClaimsJws()
                .getPayload() // ✅ getPayload() au lieu de getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException e) {
            logger.error("Signature JWT invalide : {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformé : {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expiré : {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supporté : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims vide : {}", e.getMessage());
        }
        return false;
    }
}