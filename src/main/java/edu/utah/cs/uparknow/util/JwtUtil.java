package edu.utah.cs.uparknow.util;

import java.util.Date;
import edu.utah.cs.uparknow.model.Managers;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
    private static final String SECRET_KEY = "some_secret_key";
    private static final long EXPIRATION = 180L * 24 * 60 * 60 * 1000;

    public static String generateToken(Managers manager) {
        return Jwts.builder()
                .setSubject(manager.getManaUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
        Claims body = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return body.getSubject();
    }
}
