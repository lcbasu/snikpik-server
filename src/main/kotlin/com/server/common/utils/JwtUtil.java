package com.server.common.utils;

import com.server.common.model.UserDetailsForToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private String SECRET_KEY = "secret";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetailsForToken userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userDetails.getUid());
        claims.put("absoluteMobile", userDetails.getAbsoluteMobileNumber());
        return createToken(claims, userDetails.getAbsoluteMobileNumber());
    }

    private String createToken(Map<String, Object> claims, String absoluteMobileNumber) {
        return Jwts.builder().setClaims(claims).setSubject(absoluteMobileNumber).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token) {
        // validate if the username is present in our DB or not
        final String username = extractUsername(token);
        return !isTokenExpired(token);
    }
}
