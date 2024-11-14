package com.project.security.service;

import com.project.security.model.JwtCheckResponse;
import com.project.security.model.JwtRefresh;
import com.project.security.model.JwtRefreshReqest;
import com.project.security.model.JwtResponseDTO;
import com.project.security.repository.JwtRefreshRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    public static String SECRET;

    private final Environment environment;
    private JwtRefreshRepository jwtRefreshRepository;
    private MyUserDetailService myUserDetailService;

    public JwtService(JwtRefreshRepository jwtRefreshRepository, MyUserDetailService myUserDetailService, Environment environment) {
        this.jwtRefreshRepository = jwtRefreshRepository;
        this.myUserDetailService = myUserDetailService;
        this.environment = environment;

        this.SECRET = environment.getProperty("JWT_SECRET");
    }

    // Generate token with given user name
    public Map<String, Object> initialGenerateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return initialCreateToken(claims, userName);
    }

    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    public JwtResponseDTO refresh(String RefreshToken) {
        var jwtRefresh = jwtRefreshRepository.findByRefreshToken(RefreshToken);

        if (jwtRefresh.isPresent()) {
            var username = extractUsername(jwtRefresh.get().getCurrentToken());
            var newJwt = generateToken(username);

            var newJwtRefresh = JwtRefresh.builder().refreshToken(RefreshToken).currentToken(newJwt).build();

            jwtRefreshRepository.delete(jwtRefresh.get());
            jwtRefreshRepository.save(newJwtRefresh);

            return JwtResponseDTO.builder().token(newJwt).build();
        }

        return JwtResponseDTO.builder().build();
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Token valid for 30 minutes
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Map<String, Object> initialCreateToken(Map<String, Object> claims, String userName) {
        UserDetails user;
        try {
            user =  myUserDetailService.loadUserByUsername(userName);
        } catch (Exception ex) {
            return Map.of("error", ex.getMessage());
        }

        var refreshToken = user.hashCode() + "";

        var doesExistJwtRefresh = jwtRefreshRepository.findByRefreshToken(refreshToken);

        if (doesExistJwtRefresh.isPresent()) {
            var refreshJwt = refresh(refreshToken);

            return Map.of("Refresh", refreshToken, "jwt", refreshJwt.getToken());
        } else {
            var jwt = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userName)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Token valid for 30 minutes
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
            var jwtRefresh = JwtRefresh.builder().refreshToken(refreshToken).currentToken(jwt).build();

            jwtRefreshRepository.save(jwtRefresh);

            return Map.of("Refresh", jwtRefresh.getRefreshToken(), "jwt", jwt);
        }
    }

    // Get the signing key for JWT token
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract the username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public JwtCheckResponse checkJwt(JwtRefreshReqest authRequest) {
        var jwtClaims = extractAllClaims(authRequest.getToken());
        var username = jwtClaims.get("sub");

        UserDetails userDetails = myUserDetailService.loadUserByUsername(username.toString());

        var isValid = validateToken(authRequest.getToken(), userDetails);

        return JwtCheckResponse.builder()
                                .isCorrect(isValid)
                                .username(isValid ? username.toString() : "")
                                .build();
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
