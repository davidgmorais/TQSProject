package com.example.book2door.component;

import com.example.book2door.entities.JwtUser;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class JwtUtils {
    private static final Logger logger = Logger.getLogger(JwtUtils.class.getName());

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateJwtToken(Authentication authentication) {
        JwtUser userPrincipal = (JwtUser) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getEmailFromJwt(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.log(Level.SEVERE, "Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e){
            logger.log(Level.SEVERE, "Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.log(Level.SEVERE, "Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.log(Level.SEVERE, "Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "JWT toke string is empty: {}", e.getMessage());
        }

        return false;
    }


}
