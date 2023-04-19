package me.sa1zer.cdrsystem.crm.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.crm.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_AUTHORIZE = "Authorization";
    private static final String CONTENT_TYPE = "application/json";

    private final UserDetailsServiceImpl userService;

    @Value("${common.security.jwt-secret-key}")
    private String jwtSecretKey;
    @Value("${common.security.jwt-token-duration}")
    private long tokenDuration;

    public String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZE);

        if(StringUtils.hasText(header) && header.startsWith(TOKEN_PREFIX))
            return header.split(" ")[1];

        return null;
    }

    public String generateToken(Authentication authentication) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();

        long curTime = System.currentTimeMillis();
        Date now = new Date(curTime);
        Date expired = new Date(curTime + tokenDuration);

        String username = jwtUser.getUsername();

        Map<String, Object> claims = new HashMap<>();

        claims.put("username", username);
        claims.put("id", jwtUser.getId());

        return TOKEN_PREFIX + Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(getSignInKey(), SignatureAlgorithm.HS384)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token) {
        if(!StringUtils.hasText(token)) return false;

        try {
            parseJwt(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        UserDetails userDetails = userService.loadUserByUsername(getUserLogin(token));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private Jws<Claims> parseJwt(String token) {
        return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token);
    }

    private String getUserLogin(String token) {
        return (String) parseJwt(token).getBody().get("username");
    }
}
