package com.example.blogbackend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.blogbackend.exception.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String createToken(UserDetails userDetails, String username) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("blog-platform")
                .withSubject(userDetails.getUsername())
                .withClaim("role", userDetails.getAuthorities().iterator().next().getAuthority())
                .withClaim("user_name", username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration * 1000))
                .sign(algorithm);
    }

    public DecodedJWT decodeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("blog-platform")
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("JWTVerificationException: {}", e.getMessage());
            throw new TokenExpiredException(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty : {}", e.getMessage());
        }

        return null;
    }

    public String extractUsername(String token) {
        return JWT.decode(token).getSubject();
    }

    public String extractRole(String token) {
        return JWT.decode(token).getClaim("role").asString();
    }

    public String extractUserName(String token) {
        return JWT.decode(token).getClaim("user_name").asString();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject(userDetails.getUsername())
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return !jwt.getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            log.error("JWTVerificationException : {}", e.getMessage());
            return false;
        }
    }
}
