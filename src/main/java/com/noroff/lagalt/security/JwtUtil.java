package com.noroff.lagalt.security;

import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    // Fields

    /*
     We store these values in our application.properties files.
     These would ideally be environment variables on a server, which is why we access them this way.
    */
    private String jwtSecret = "superSecretKey";

    private int jwtExpirationMs = 5 * 60 * 60;

    // Here we make use of the standard JWT library for java called io.jsonwebtoken.
    public String generateJwtToken(Authentication authentication) {
        // Here we access the current user through Spring Security.
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        // We use the builder pattern to create a JWT
        return Jwts.builder()
                // We set our subject as our user's username - this who the token is for
                .setSubject((userPrincipal.getUsername()))
                // We set the issued at date to now
                .setIssuedAt(new Date())
                // Expiration date is determined by our jwtExpirationMs value
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                // And we finally sign the token with our secret, this secret is important for validation.
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String jwt) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT Claims string is empty: " + e.getMessage());
        }
        return false;
    }
}

