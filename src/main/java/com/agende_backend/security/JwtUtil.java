package com.agende_backend.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // injeta o valor da chave secreta do application.properties
    @Value("${jwt.secret}")
    private String secret;

    // injeta o valor do tempo de expiração do token do application.properties
    @Value("${jwt.expiration}")
    private Long expiration;

    // Gera a chave de assinatura a partir da chave secreta
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    // Gera o token a partir do email e do perfil
    public String generateToken(String email, String perfil) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("perfil", perfil);
        return createToken(claims, email);
    }
    // Cria o token a partir das informações fornecidas
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    // Extrai o email do token e o retorna
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // Extrai o email do token e o retorna para o extractUsername
    public String extractUsername(String token) {
        return extractEmail(token);
    }
    // Extrai o perfil do token e o retorna para o extractClaim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    // Extrai todas as informações do token e o retorna para o extractAllClaims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    // Verifica se o token expirou e o retorna para o isTokenExpired
    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
    // Verifica se o token é valido e o retorna para o isTokenValid
    public boolean isTokenValid(String token, String username) {
        final String email = extractUsername(token);
        return email != null && email.equals(username) && !isTokenExpired(token);
    }
    // Verifica se o token é valido e o retorna para o validateToken
    public Boolean validateToken(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails.getUsername());
    }
}
