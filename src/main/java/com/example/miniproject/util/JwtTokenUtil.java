package com.example.miniproject.util;

import com.example.miniproject.domain.member.dto.TokenDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtTokenUtil {

    @Value("${token.secret.key}")
    private String secretKey;

    @Value("${token.access-token.plus-hour}")
    private Long accessTokenPlusHour;

    @Value("${token.refresh-token.plus-hour}")
    private Long refreshTokenPlusHour;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String issueToken(String email, Long hour) {
        var expiredLocalDateTime = LocalDateTime.now().plusHours(hour);
        var expiredAt = Date.from(expiredLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Claims claims = Jwts.claims();
        claims.put("email", email);

        return Jwts.builder()
          .signWith(getKey(), SignatureAlgorithm.HS256)
          .setClaims(claims)
          .setExpiration(expiredAt)
          .compact();
    }

    public TokenDTO generatedToken(String email) {
        return TokenDTO.of(
          issueToken(email, accessTokenPlusHour),
          issueToken(email, refreshTokenPlusHour));
    }

    public TokenDTO regeneratedToken(String accessToken) throws JsonProcessingException {
        String email = decodeJwtPayloadEmail(accessToken);
        return generatedToken(email);
    }

    public String validationTokenWithThrow(String token) {
        var parser = Jwts.parserBuilder()
          .setSigningKey(getKey())
          .build();
        var result = parser.parseClaimsJws(token).getBody();
        var username = result.get("email");
        return String.valueOf(username);
    }

    public String decodeJwtPayloadEmail(String accessToken) throws JsonProcessingException {
        return objectMapper.readValue(
          new String(Base64.getDecoder().decode(accessToken.split("\\.")[1]), StandardCharsets.UTF_8),
          Map.class
        ).get("email").toString();
    }

    public boolean isExpired(String token) {
        Date expiredDate = extractClaims(token).getExpiration();
        return expiredDate.before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey())
          .build().parseClaimsJws(token).getBody();
    }

    private Key getKey() {
        byte[] ketBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(ketBytes);
    }

}
