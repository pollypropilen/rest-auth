package com.pollypropilen.web.security;

import com.pollypropilen.web.dto.TokenDTO;
import com.pollypropilen.web.entity.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.pollypropilen.web.security.SecurityConstants.*;

@Component
public class JWTTokenProvider {
    public static final Logger LOG = LoggerFactory.getLogger(JWTTokenProvider.class);

    public TokenDTO generateToken(HttpServletRequest httpServletRequest) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiredAt = new Date(issuedAt.getTime() + SecurityConstants.FIRST_TOKEN_EXP_TIME);
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(TOKEN_AGENT, JWTAuthenticationFilter.getUserAgent(httpServletRequest));
        claimsMap.put(TOKEN_IP, JWTAuthenticationFilter.getClientIp(httpServletRequest));
        claimsMap.put(TOKEN_EXPIRE, expiredAt);
        String token = createToken(claimsMap, "NEW-TOKEN", issuedAt, expiredAt);
        return new TokenDTO(token, expiredAt);
    }

    public TokenDTO generateUserToken(
            Authentication authentication,
            HttpServletRequest httpServletRequest
    ) {
        User user = (User) authentication.getPrincipal();
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiredAt = new Date(issuedAt.getTime() + SecurityConstants.EXPIRATION_TIME);

        Map<String, Object> claimsMap = new HashMap<>();
        String userId = Long.toString(user.getId());
        claimsMap.put(TOKEN_ID, userId);
        claimsMap.put(TOKEN_SUBJECT, user.getUsername());
        claimsMap.put(TOKEN_AGENT, JWTAuthenticationFilter.getUserAgent(httpServletRequest));
        claimsMap.put(TOKEN_IP, JWTAuthenticationFilter.getClientIp(httpServletRequest));
        claimsMap.put(TOKEN_EXPIRE, expiredAt);
        String token = createToken(claimsMap, userId, issuedAt, expiredAt);
        return new TokenDTO(token, expiredAt);
    }

    private String createToken(Map<String, Object> claims, String subject, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException |
                 MalformedJwtException |
                 ExpiredJwtException |
                 UnsupportedJwtException |
                 IllegalArgumentException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SecurityConstants.SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public Long getUserIdFromToken(String token) {
        Claims claims = extractAllClaims(token);
        String id = (String) claims.get(TOKEN_ID);
        if (id == null || id.isEmpty() || id.isBlank()) {
            id = "0";
        }
        return Long.parseLong(id);
    }

    public Long getTimeFromToken(String token) {
        Claims claims = extractAllClaims(token);
        String time = claims.get(TOKEN_EXPIRE).toString();
        if (time == null || time.isEmpty() || time.isBlank()) {
            time = "0";
        }
        return Long.parseLong(time);
    }

    public String getUserIPFromToken(String token) {
        Claims claims = extractAllClaims(token);
        String ip = (String) claims.get(TOKEN_IP);
        if (ip == null || ip.isEmpty() || ip.isBlank()) {
            ip = "LH";
        }
        return ip;
    }

    public String getUserUAFromToken(String token) {
        Claims claims = extractAllClaims(token);
        String ua = (String) claims.get(TOKEN_AGENT);
        if (ua == null || ua.isEmpty() || ua.isBlank()) {
            ua = "AVAJ";
        }
        return ua;
    }

}