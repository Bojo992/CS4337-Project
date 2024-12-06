package com.project.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.security.model.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtService {
    public static String SECRET;

    private MyUserDetailService myUserDetailService;
    ObjectMapper objectMapper = new ObjectMapper();
    String baseUrl = "ec2-52-212-119-177.eu-west-1.compute.amazonaws.com:8082";

    public JwtService(MyUserDetailService myUserDetailService, Environment environment) {
        this.myUserDetailService = myUserDetailService;
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
        try {
            HttpClient client = HttpClient.newHttpClient();

            String param2 = URLEncoder.encode(RefreshToken, StandardCharsets.UTF_8);

            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(new URI(baseUrl + "/jwt/get?token=" + param2))
                    .GET()
                    .build();
            HttpResponse<String> response1 = client.send(request3, HttpResponse.BodyHandlers.ofString());

            Optional<JwtRefresh> jwtRefresh = Optional.of(null);

            if (response1.statusCode() == 200) {
                var temp = objectMapper.readValue(response1.body(), JwtRefresh.class);

                jwtRefresh = Optional.of(temp);
            }

            if (jwtRefresh.isPresent()) {
                var username = extractUsername(jwtRefresh.get().getCurrentToken());
                var newJwt = generateToken(username);

                var newJwtRefresh = JwtRefresh.builder().refreshToken(RefreshToken).currentToken(newJwt).build();

                var temp = jwtRefresh.get();

                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(new URI(baseUrl + "jwt/delete?token=" + temp.getRefreshToken()))
                        .GET()
                        .build();
                client.send(request2, HttpResponse.BodyHandlers.ofString());

                String jsonBody = "{\"refreshToken\": " + temp.getRefreshToken() + ", \"currentToken\": " + temp.getCurrentToken() + "}";
                HttpRequest request1 = HttpRequest.newBuilder()
                        .uri(new URI(baseUrl + "/jwt/save"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();
                client.send(request1, HttpResponse.BodyHandlers.ofString());


                return JwtResponseDTO.builder().token(newJwt).build();
            }

            return JwtResponseDTO.builder().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        try {
            HttpClient client = HttpClient.newHttpClient();

            try {
                myUserDetailService.loadUserByUsername(userName);
            } catch (Exception ex) {
                return Map.of("error", ex.getMessage());
            }

            var refreshToken = UUID.randomUUID().toString();

            String param2 = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(new URI(baseUrl + "/jwt/get?token=" + param2))
                    .GET()
                    .build();
            HttpResponse<String> response1 = client.send(request2, HttpResponse.BodyHandlers.ofString());

            Optional<JwtRefresh> doesExistJwtRefresh = Optional.of(null);

            if (response1.statusCode() == 200) {
                var temp = objectMapper.readValue(response1.body(), JwtRefresh.class);

                doesExistJwtRefresh = Optional.of(temp);
            }

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


                String jsonBody = "{\"refreshToken\": " + refreshToken + ", \"currentToken\": " + jwt + "}";
                HttpRequest request1 = HttpRequest.newBuilder()
                        .uri(new URI(baseUrl + "/jwt/save"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();
                client.send(request1, HttpResponse.BodyHandlers.ofString());

                return Map.of("Refresh", jwtRefresh.getRefreshToken(), "jwt", jwt);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
        try {
            Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(authRequest.getToken());
            return JwtCheckResponse.builder()
                    .isCorrect(true)
                    .username(extractAllClaims(authRequest.getToken()).get("sub").toString())
                    .build();
        } catch (Exception ex) {
            return JwtCheckResponse.builder()
                    .isCorrect(false)
                    .username("")
                    .build();
        }
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
